package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
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
import util.Authentication;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestHandler extends Thread {
    private final Database db;
    private DatabaseUser databaseUser;
    private final ObjectMapper objMapper = new ObjectMapper();
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private static final Logger logger = Logger.getLogger(RequestHandler.class);
    private final BattleLogic battleLogic;

    private ResponseHandler rph;

    public RequestHandler(Database db, Socket clientSocket, BattleLogic battleLogic) throws IOException {
        this.db = db;
        this.socket = clientSocket;
        this.battleLogic = battleLogic;
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
                    if (databaseUser.getAllCards(username).isEmpty()) {
                        return false;
                    }

                    return rph.showAllCards(username);

                case "deck":
                    if (r.getGetParameter().containsKey("format")) {
                        return rph.showDeckPlain(username);
                    } else {
                        return rph.showDeck(username);
                    }

                case "users":
                    String userToEdit = r.getUrlParameter().get(1);

                    if (userToEdit.equals(username)) {
                        return rph.getUserData(userToEdit);
                    }

                    return false;

                case "stats":
                    return rph.getStats(username);

                case "score":
                    return rph.getScoreboard();

                case "tradings":
                    return rph.getTradingDeals();

                default:
                    return false;
            }
        } else {
            // POST, PUT, DELETE ...
            switch (operation) {
                case "packages":
                    List<RequestCardHeader> pkgCards = objMapper.readValue(r.getBody(), new TypeReference<>() {
                    });
                    List<CardModel> packageToAdd = new ArrayList<>();

                    for (RequestCardHeader c : pkgCards) {
                        if (c.getMonstertype().equals("Spell")) {
                            packageToAdd.add(new Spell(c.getId(), username, Authentication.generateAuthToken(), Type.valueOf(c.getElementtype().toUpperCase()), MonsterType.valueOf(c.getMonstertype().toUpperCase()), c.getDamage()));
                        } else {
                            packageToAdd.add(new Monster(c.getId(), username, Authentication.generateAuthToken(), Type.valueOf(c.getElementtype().toUpperCase()), MonsterType.valueOf(c.getMonstertype().toUpperCase()), c.getDamage()));
                        }
                    }
                    return databaseUser.addPackage(packageToAdd);

                case "transactions":
                    if (r.getUrlParameter().get(1).equals("packages")) {
                        return databaseUser.buyPackage(username);
                    }
                    break;

                case "deck":
                    List<String> deckCards = Arrays.asList(r.getBody().replaceAll("[\\[\\] \" ]", "" ).split("\\s*,\\s*"));
                    return databaseUser.configureDeck(username, deckCards);

                case "users":
                    String userToEdit = r.getUrlParameter().get(1);
                    UserModel u = objMapper.readValue(r.getBody(), UserModel.class);

                    if (userToEdit.equals(r.getUsername())) {
                        return databaseUser.editUserData(userToEdit, u.getUsername());
                    }

                    return false;

                case "battles":
                    logger.info("rein");
                    battleLogic.queueFighter(rph, username);
                    return true;

                case "tradings":
                    if (r.getMethod().equals("POST")) {
                        if (r.getUrlParameter().size() == 1) {
                            Trade t = objMapper.readValue(r.getBody(), Trade.class);

                            return databaseUser.pushTradingDeal(username, t.getUuid(), t.getCardToTrade(), t.getMinDamage(), t.isWantsMonster(), t.isWantsSpell());
                        } else {
                            String tradeUUID = r.getUrlParameter().get(1);
                            return databaseUser.acceptTradingDeal(username, tradeUUID, r.getBody().replace("\"", ""));
                        }
                    } else {
                        String tradeUUID = r.getUrlParameter().get(1);
                        return databaseUser.deleteTradeByUser(username, tradeUUID);
                    }

                default:
                    return false;
            }

        }

        return true;
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

