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

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestHandler extends Thread {

    private final Database db;
    private DatabaseUser databaseUser;
    private DatabaseStore databaseStore;

    private final ObjectMapper objMapper = new ObjectMapper();

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private final BattleLogic battleLogic;

    private ResponseHandler handler;
    private final Socket socket;

    public RequestHandler(Database db, Socket clientSocket, BattleLogic battleLogic) {
        this.db = db;
        this.socket = clientSocket;
        this.battleLogic = battleLogic;
    }

    @Override
    public void run() {
        initialDb();
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        this.handler = new ResponseHandler(db, bufferedWriter);

        int length = 0;
        String line;
        StringBuilder sbHeader = new StringBuilder();
        StringBuilder sbBody = new StringBuilder();

        try {
            // Format Header
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

            //  Format Body
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
                handler.response("OK");
            } else {
                handler.response("ERROR");
            }

            socket.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void initialDb() {
        this.databaseStore = new DatabaseStore(db.getStmt(), db.getConnection());
        this.databaseUser = new DatabaseUser(db.getStmt(), db.getConnection());
    }


    public boolean handleRequest(StringBuilder sbHeader, StringBuilder sbBody)  {
        // Parse header
        String request = sbHeader.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String username = "";
        String token = "";
        boolean noError = false;

        try {
            List<String> headers = new ArrayList<>();
            for (int h = 2; h < requestsLines.length; h++) {
                String header = requestsLines[h];

                if (header.startsWith("Authorization")) {
                    String splitUsername = header.split(" ")[2];
                    username = splitUsername.split("-")[0];
                    token = splitUsername.split("-")[1];
                }

                headers.add(header);
            }

            RequestHeader r = new RequestHeader(username, token, sbBody.toString(), method, path);

            if (username.isEmpty() && token.isEmpty()) {
                noError = this.handleBodyWithoutToken(r);
            } else {
                noError = this.handleBodyWithToken(r);
            }


        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return noError;
    }

    private boolean handleBodyWithToken(RequestHeader requestHeader) {
        String username = requestHeader.getUsername();

        if (!databaseUser.compareExchangeToken(username, requestHeader.getToken())) {
            logger.error("Wrong exchange token");
            return false;
        }

        String operation = requestHeader.getUrl().get(0);
        if (requestHeader.getMethod().equals("GET")) {
            switch (operation) {
                case "cards":
                    if (databaseUser.getAllCards(username).isEmpty()) {
                        return false;
                    }

                    return handler.showAllCards(username);

                case "deck":
                    if (requestHeader.getGetParameter().containsKey("format")) {
                        return handler.showDeckPlain(username);
                    } else {
                        return handler.showDeck(username);
                    }

                case "users":
                    String userToEdit = requestHeader.getUrl().get(1);

                    if (userToEdit.equals(username)) {
                        return handler.getUserData(userToEdit);
                    }

                    return false;

                case "stats":
                    return handler.getStats(username);

                case "score":
                    return handler.getScoreboard();

                case "tradings":
                    return handler.getTradingDeals();

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
                case "packages":
                    List<RequestCardHeader> pkgCards = objMapper.readValue(requestHeader.getBody(), new TypeReference<>() {
                    });
                    List<CardModel> packageToAdd = new ArrayList<>();

                    for (RequestCardHeader c : pkgCards) {
                        if (c.getMonstertype().equals("Spell")) {
                            packageToAdd.add(new Spell(c.getId(), username, AuthenticationService.generateAuthToken(), Type.valueOf(c.getElementtype().toUpperCase()), MonsterType.valueOf(c.getMonstertype().toUpperCase()), c.getDamage()));
                        } else {
                            packageToAdd.add(new Monster(c.getId(), username, AuthenticationService.generateAuthToken(), Type.valueOf(c.getElementtype().toUpperCase()), MonsterType.valueOf(c.getMonstertype().toUpperCase()), c.getDamage()));
                        }
                    }
                    return databaseUser.addPackage(packageToAdd);

                case "transactions":
                    if (requestHeader.getUrl().get(1).equals("packages")) {
                        return databaseStore.buyPackage(username);
                    }
                    break;

                case "deck":
                    List<String> deckCards = Arrays.asList(requestHeader.getBody().replaceAll("[\\[\\] \" ]", "").split("\\s*,\\s*"));
                    return databaseUser.configureDeck(username, deckCards);

                case "users":
                    String userToEdit = requestHeader.getUrl().get(1);
                    UserModel u = objMapper.readValue(requestHeader.getBody(), UserModel.class);

                    if (userToEdit.equals(requestHeader.getUsername())) {
                        return databaseUser.editUserData(userToEdit, u.getUsername(), u.getBio(), u.getImage());
                    }

                    return false;

                case "battles":
                    logger.info("rein");
                    battleLogic.queueFighter(handler, username);
                    return true;

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

                default:
                    return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    private boolean handleBodyWithoutToken(RequestHeader r) throws JsonProcessingException {
        UserModel u;
        String body = r.getBody();

        switch (r.getUrl().get(0)) {
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

