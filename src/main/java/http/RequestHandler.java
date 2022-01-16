package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import database.DatabaseStore;
import database.DatabaseUser;
import logic.BattleLogic;
import model.CardModel;
import model.Monster;
import model.Spell;
import model.UserModel;
import model.helper.MonsterType;
import model.helper.Type;
import model.store.Trade;
import org.apache.log4j.Logger;
import service.AuthenticationService;
import service.PackageService;
import service.RandomService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestHandler extends Thread {

    private final Database db;
    private final DatabaseUser databaseUser;
    private final DatabaseStore databaseStore;

    private final ObjectMapper objMapper = new ObjectMapper();

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private final BattleLogic battleLogic;
    private PackageService packageService = new PackageService();

    private ResponseHandler handler;
    private final Socket socket;

    public static final String LINE_END = "\r\n";

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
        String request = sbHeader.toString();
        String[] requestsLines = request.split(LINE_END);
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String[] userNameAndToken = getUserNameAndToken(requestsLines);
        boolean noError;

        RequestHeader r = new RequestHeader(userNameAndToken[0], userNameAndToken[1], sbBody.toString(), method, path);

        if (userNameAndToken[0].isEmpty() && userNameAndToken[1].isEmpty()) {
            noError = this.bodyWithoutToken(r);
        } else {
            noError = this.bodyWithToken(r);
        }

        return noError;
    }


    private String[] getUserNameAndToken(String[] requestsLines) {
        String [] userToken = new String[2];
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            if (header.startsWith("Authorization")) {
                String splitUsername = header.split(" ")[2];
                userToken[0] = splitUsername.split("-")[0];
                userToken[1] = splitUsername.split("-")[1];
            }
        }
        userToken[0] = userToken[0] != null ? userToken[0] : "";
        userToken[1] = userToken[1] != null ? userToken[1] : "";
        return userToken;
    }

    private boolean bodyWithToken(RequestHeader requestHeader) {
        String username = requestHeader.getUsername();
        if (requestHeader.getToken() != null && !databaseUser.compareExchangeToken(username, requestHeader.getToken())) {
            logger.error("Wrong token");
            return false;
        }

        String operation = requestHeader.getUrl().get(0);
        if (requestHeader.getMethod().equals("GET")) {
            switch (operation) {
                case "users":
                    String userToEdit = requestHeader.getUrl().get(1);
                    if (userToEdit.equals(username)) {
                        return handler.respondUserData(userToEdit);
                    }
                    return false;
                case "cards":
                    if (databaseUser.getAllCards(username).isEmpty()) {
                        return false;
                    }
                    return handler.respondsAllCard(username);
                case "deck":
                    if (requestHeader.getGetParameter().containsKey("format")) {
                        return handler.respondPlainDeck(username);
                    } else {
                        return handler.respondDeck(username);
                    }
                case "tradings":
                    return handler.respondTrade();
                case "stats":
                    return handler.respondStats(username);
                case "score":
                    return handler.respondScore();
                default:
                    return false;
            }
        } else {
            return handleOtherRequest(operation, requestHeader, username);
        }
    }


    private boolean handleOtherRequest(String operation, RequestHeader requestHeader, String username) {
        try {
            switch (operation) {
                case "deck":
                    List<String> deckCards = Arrays.asList(requestHeader.getBody().replaceAll("[\\[\\] \" ]", "").split("\\s*,\\s*"));
                    return databaseUser.configureDeck(username, deckCards);
                case "packages":
                    List<RequestCardHeader> pkgCards = objMapper.readValue(requestHeader.getBody(), new TypeReference<>() {});
                    return databaseUser.addPackage(packageService.createPackage(username, pkgCards));
                case "transactions":
                    if (requestHeader.getUrl().get(1).equals("packages")) {
                        return databaseStore.buyPackage(username);
                    }
                    break;

                case "users":
                    String userToEdit = requestHeader.getUrl().get(1);
                    UserModel u = objMapper.readValue(requestHeader.getBody(), UserModel.class);
                    u.setUsername(userToEdit);

                    if (userToEdit.equals(requestHeader.getUsername())) {
                        return databaseUser.editUser(u);
                    }

                    return false;

                case "tradings":
                    if (requestHeader.getMethod().equals("POST")) {
                        if (requestHeader.getUrl().size() == 1) {
                            Trade t = objMapper.readValue(requestHeader.getBody(), Trade.class);

                            return databaseStore.pushTradingDeal(username, t.getUuid(), t.getCardToTrade(), t.getMinDamage(), t.isWantsMonster(), t.isWantsSpell());
                        } else {
                            String tradeUUID = requestHeader.getUrl().get(1);
                            return databaseStore.acceptTradingDeal(username, tradeUUID, requestHeader.getBody().replace("\"", ""));
                        }
                    } else {
                        String tradeUUID = requestHeader.getUrl().get(1);
                        return databaseStore.deleteTradeByUser(username, tradeUUID);
                    }
                case "battles":
                    battleLogic.queueFighter(handler, username);
                    return true;
                default:
                    return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    private boolean bodyWithoutToken(RequestHeader r) {
        UserModel u;
        String body = r.getBody();
        try {
            switch (r.getUrl().get(0)) {
                case "sessions":
                    u = objMapper.readValue(body, UserModel.class);
                    UserModel loggedInUser = databaseUser.loginUser(u.getUsername(), u.getPassword());
                    if (loggedInUser != null) {
                        handler.response("User" + u.getUsername() + "is logged in");
                    } else {
                        return false;
                    }
                case "users":
                    u = objMapper.readValue(body, UserModel.class);
                    if (databaseUser.createUser(u)) {
                        handler.response("User" + u.getUsername() + "is created");
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}