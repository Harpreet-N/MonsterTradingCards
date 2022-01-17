package http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.DatabaseStore;
import database.DatabaseUser;
import logic.BattleLogic;
import model.UserModel;
import model.store.Trade;
import org.apache.log4j.Logger;
import service.PackageService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RequestTokenHandler {

    private static final Logger logger = Logger.getLogger(RequestTokenHandler.class);
    private final ObjectMapper objMapper = new ObjectMapper();

    private final BattleLogic battleLogic;
    private PackageService packageService = new PackageService();

    public RequestTokenHandler(BattleLogic battleLogic) {
        this.battleLogic = battleLogic;
    }


    public boolean requestWithToken(RequestHeader requestHeader, DatabaseUser databaseUser, ResponseHandler handler, DatabaseStore databaseStore) {
        String username = requestHeader.getUsername();
        if (requestHeader.getToken() != null && !databaseUser.isTokenEqual(username, requestHeader.getToken())) {
            logger.error("Wrong token");
            return false;
        }

        if (requestHeader.getMethod().equals("GET")) {
            switch (requestHeader.getUrl().get(0)) {
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
            return handleAllOtherRequests(requestHeader.getUrl().get(0), requestHeader, username, databaseUser, handler, databaseStore);
        }
    }

    private boolean handleAllOtherRequests(String method, RequestHeader requestHeader, String username, DatabaseUser databaseUser, ResponseHandler handler, DatabaseStore databaseStore) {
        try {
            switch (method) {
                case "deck":
                    List<String> deckCards = Arrays.asList(requestHeader.getBody().replaceAll("[\\[\\] \" ]", "").split("\\s*,\\s*"));
                    return databaseUser.configureDeck(username, deckCards);
                case "packages":
                    List<RequestCardHeader> pkgCards = objMapper.readValue(requestHeader.getBody(), new TypeReference<List<RequestCardHeader>>() {
                    });
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
                    handler.response("Staring Editing: " + username);
                    if (userToEdit.equals(requestHeader.getUsername())) {
                        return databaseUser.editUser(u);
                    }

                    return false;

                case "tradings":
                    if (requestHeader.getMethod().equals("POST")) {
                        Trade tradingCard = objMapper.readValue(requestHeader.getBody(), Trade.class);
                        handler.response("Staring Trading: " + username);

                        if (databaseStore.startTrade(username, tradingCard)) {
                            return databaseStore.changeOwnerOfCard(username, tradingCard.getCardToTrade());
                        }
                    } else {
                        String tradeUUID = requestHeader.getUrl().get(1);
                        handler.response("Delete Trade: " + tradeUUID);
                        return databaseStore.removeTrade(tradeUUID);
                    }
                    return false;
                case "battles":
                    battleLogic.getPlayerReady(handler, username);
                    return true;
                default:
                    return false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return true;
    }

    public boolean requestWithoutToken(RequestHeader header, DatabaseUser databaseUser, ResponseHandler handler) {
        UserModel userModel;
        String body = header.getBody();
        try {
            switch (header.getUrl().get(0)) {
                case "sessions":
                    userModel = objMapper.readValue(body, UserModel.class);
                    UserModel userIsLogged = databaseUser.loginUser(userModel.getUsername(), userModel.getPassword());
                    if (userIsLogged != null) {
                        handler.response("User is logged as: " + userModel.getUsername());
                    } else {
                        return false;
                    }
                    return true;
                case "users":
                    userModel = objMapper.readValue(body, UserModel.class);
                    if (databaseUser.createUser(userModel)) {
                        handler.response("User" + userModel.getUsername() + "is created");
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
