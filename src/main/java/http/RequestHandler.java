package http;

import database.Database;
import database.DatabaseStore;
import database.DatabaseUser;
import logic.BattleLogic;
import org.apache.log4j.Logger;
import service.UserNameAndTokenService;

import java.io.*;
import java.net.Socket;

public class RequestHandler extends Thread {

    private static final Logger logger = Logger.getLogger(RequestHandler.class);

    private final Database db;
    private final DatabaseUser databaseUser;
    private final DatabaseStore databaseStore;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private ResponseHandler handler;
    private RequestTokenHandler requestTokenHandler;
    private final Socket socket;

    public static final String LINE_END = "\r\n";

    private final BattleLogic battleLogic;
    private UserNameAndTokenService userNameAndTokenService = new UserNameAndTokenService();


    public RequestHandler(Database db, DatabaseUser databaseUser, DatabaseStore databaseStore, Socket clientSocket, BattleLogic battleLogic) {
        this.db = db;
        this.databaseUser = databaseUser;
        this.databaseStore = databaseStore;
        this.socket = clientSocket;
        this.battleLogic = battleLogic;
    }

    @Override
    public void run() {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        this.handler = new ResponseHandler(db, bufferedWriter);
        StringBuilder sbHeader = new StringBuilder();
        StringBuilder sbBody = new StringBuilder();

        try {
            int length = formatHeader(0, sbHeader);
            formatBody(length, sbBody);

            // Handle parsed header and body
            if (handleRequest(sbHeader, sbBody)) {
                handler.response("OK");
            } else {
                handler.response("ERROR");
            }

            socket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void formatBody(int length, StringBuilder builder) throws IOException {
        if (length > 0) {
            int read;
            while ((read = bufferedReader.read()) != -1) {
                builder.append((char) read);
                if (builder.length() == length) {
                    break;
                }
            }
        }
    }

    private int formatHeader(int length, StringBuilder builder) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("Content-Length")) {
                int index = line.indexOf(':') + 1;
                String len = line.substring(index).trim();
                length = Integer.parseInt(len);
            }
            builder.append(line).append(LINE_END);
        }
        return length;
    }


    public boolean handleRequest(StringBuilder sbHeader, StringBuilder sbBody) {
        String[] requestsLines = sbHeader.toString().split(LINE_END);
        String[] requestLine = requestsLines[0].split(" ");
        String path = requestLine[1];
        String method = requestLine[0];
        String[] userNameAndToken = userNameAndTokenService.getUserNameAndToken(requestsLines);
        boolean noError;

        RequestHeader header = new RequestHeader(userNameAndToken[0], userNameAndToken[1], sbBody.toString(), method, path);
        requestTokenHandler = new RequestTokenHandler(battleLogic);

        if (userNameAndToken[0].isEmpty() && userNameAndToken[1].isEmpty()) {
            noError = requestTokenHandler.requestWithoutToken(header, databaseUser, handler);

        } else {
            noError = requestTokenHandler.requestWithToken(header, databaseUser, handler, databaseStore);
        }
        return noError;
    }
}