package service;

import model.UserModel;
import repository.UserDtoRepository;

public class UserService {

    public UserService() {
    }

    public void addLoss(UserModel userData) {
        userData.setLooses(userData.getLooses() + 1);
        userData.setElo(userData.getElo() - 5);
    }

    public void addWin(UserModel userData) {
        userData.setWins(userData.getWins() + 1);
        userData.setElo(userData.getElo() + 5);
    }

}
