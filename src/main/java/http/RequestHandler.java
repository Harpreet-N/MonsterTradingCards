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

    private void formatBody(int length, StringBuilder sbBody) throws IOException {
        if (length > 0) {
            int read;
            while ((read = bufferedReader.read()) != -1) {
                sbBody.append((char) read);
                if (sbBody.length() == length) {
                    break;
                }
            }
        }
    }

    private int formatHeader(int length, StringBuilder sbHeader) throws IOException {
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
            sbHeader.append(line).append(LINE_END);
        }
        return length;
    }


    public boolean handleRequest(StringBuilder sbHeader, StringBuilder sbBody) {
        // Parse header
        String request = sbHeader.toString();
        String[] requestsLines = request.split(LINE_END);
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String username = "";
        String token = "";
        boolean noError;

        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            if (header.startsWith("Authorization")) {
                String splitUsername = header.split(" ")[2];
                username = splitUsername.split("-")[0];
                token = splitUsername.split("-")[1];
            }
        }

        RequestHeader r = new RequestHeader(username, token, sbBody.toString(), method, path);

        if (username.isEmpty() && token.isEmpty()) {
            noError = this.bodyWithoutToken(r);
        } else {
            noError = this.bodyWithToken(r);
        }

        return noError;
    }

    private boolean bodyWithToken(RequestHeader requestHeader) {
        String username = requestHeader.getUsername();
        if (!databaseUser.compareExchangeToken(username, requestHeader.getToken())) {
            logger.error("Wrong token");
            return false;
        }

        String operation = requestHeader.getUrl().get(0);
        if (requestHeader.getMethod().equals("GET")) {
            switch (operation) {
                case "users":
                    String userToEdit = requestHeader.getUrl().get(1);
                    if (userToEdit.equals(username)) {
                        return handler.getUserData(userToEdit);
                    }

                    return false;
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
                case "tradings":
                    return handler.getTrade();
                case "stats":
                    return handler.getStats(username);
                case "score":
                    return handler.getScoreboard();
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
                    List<RequestCardHeader> pkgCards = objMapper.readValue(requestHeader.getBody(), new TypeReference<>() {
                    });
                    List<CardModel> packageToAdd = new ArrayList<>();

                    for (RequestCardHeader c : pkgCards) {
                        if (c.getName().contains("Spell")) {
                            getElementTyp(c);
                            c.setMonsterType(MonsterType.SPELL);
                            packageToAdd.add(new Spell(c.getId(), username, AuthenticationService.generateAuthToken(), c.getElementType(), c.getMonsterType(), c.getDamage()));
                        } else {
                            getElementsAndMonster(c);
                            packageToAdd.add(new Monster(c.getId(), username, AuthenticationService.generateAuthToken(), c.getElementType(), c.getMonsterType(), c.getDamage()));
                        }
                    }
                    return databaseUser.addPackage(packageToAdd);

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

    private void getElementsAndMonster(RequestCardHeader c) {
        for (MonsterType type : MonsterType.values()) {
            if (c.getName().toUpperCase().contains(type.name())) {
                c.setMonsterType(type);
            }
        }
        getElementTyp(c);
        setRandomElementIfNull(c);
        setRandomMonsterIfNull(c);
    }

    private void getElementTyp(RequestCardHeader c) {
        for (Type type : Type.values()) {
            if (c.getName().toUpperCase().contains(type.name())) {
                c.setElementType(type);
            }
        }
        setRandomElementIfNull(c);
    }

    private void setRandomElementIfNull(RequestCardHeader c) {
        if (c.getElementType() == null) {
            c.setElementType(RandomService.getRandomType());
        }
    }

    private void setRandomMonsterIfNull(RequestCardHeader c) {
        if (c.getMonsterType() == null) {
            c.setMonsterType(RandomService.getRandomMonsterType());
        }
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