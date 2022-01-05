package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import database.DatabaseUser;
import model.UserModel;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends Thread {
    private final Database db;
    private DatabaseUser databaseUser;
    private final ObjectMapper objMapper = new ObjectMapper();
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    //  TO-DO add battle logic

    private ResponseHandler rph;

    public RequestHandler(Database db, Socket clientSocket) throws IOException {
        this.db = db;
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        initalDb();

        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        this.rph = new ResponseHandler(db, bufferedWriter);

        int length = 0;
        String line;
        StringBuilder sbHeader = new StringBuilder();
        StringBuilder sbBody = new StringBuilder();

        try {
            // Header
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    break;
                }

                if (line.startsWith("Content-Length")) {
                    int index = line.indexOf(':') + 1;
                    String len = line.substring(index).trim();
                    length = Integer.parseInt(len);
                }

                sbHeader.append(line).append("\r\n");
            }

            // Body
            if (length > 0) {
                int read;
                while ((read = bufferedReader.read()) != -1) {
                    sbBody.append((char) read);
                    if (sbBody.length() == length) {
                        break;
                    }
                }
            }

            // Handle parsed header and body
            if (handleRequest(sbHeader, sbBody)) {
                rph.responseOK();
            } else {
                rph.responseError();
            }

            socket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void initalDb() {
        this.databaseUser = new DatabaseUser(db.getStmt(), db.getConnection());
    }


    public boolean handleRequest(StringBuilder sbHeader, StringBuilder sbBody) throws IOException {
        // Parse header
        String request = sbHeader.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String username = "";
        String token = "";
        boolean noError;

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];

            if (header.startsWith("Authorization")) {
                String cutUsernameToken = header.split(" ")[2];
                username = cutUsernameToken.split("-")[0];
                token = cutUsernameToken.split("-")[1];
            }

            headers.add(header);
        }

        RequestHeader r = new RequestHeader(username, token, sbBody.toString(), method, path);

        if (username.isEmpty() && token.isEmpty()) {
            noError = this.handleBodyWithoutToken(r);
        } else {
            noError = this.handleBodyWithToken(r);
        }

        return noError;
    }

    private boolean handleBodyWithToken(RequestHeader r) throws IOException, JsonProcessingException {
        String username = r.getUsername();

        if (!databaseUser.compareExchangeToken(username, r.getToken())) {
            logger.error("Wrong exchange token");
            return false;
        }

        String operation = r.getUrlParameter().get(0);
        if (r.getMethod().equals("GET")) {
            switch (operation) {
                case "cards":
                case "deck":
                case "users":
                    return false;

                case "stats":

                case "score":

                case "tradings":

                default:
                    return false;
            }
        } else {
            switch (operation) {
                case "packages":

                case "transactions":

                case "deck":

                case "users":

                case "battles":

                case "tradings":

                default:
                    return false;
            }

        }

    }

    private boolean handleBodyWithoutToken(RequestHeader r) throws JsonProcessingException {
        UserModel u;
        String body = r.getBody();

        switch (r.getUrlParameter().get(0)) {
            case "users":
                u = objMapper.readValue(body, UserModel.class);
                return databaseUser.createUser(u.getUsername(), u.getPassword());

            case "sessions":
                u = objMapper.readValue(body, UserModel.class);
                UserModel loggedInUser = databaseUser.loginUser(u.getUsername(), u.getPassword());
                return loggedInUser != null;

            default:
                return false;
        }
    }
}

