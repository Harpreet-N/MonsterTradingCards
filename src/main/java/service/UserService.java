package service;

import model.UserModel;
import repository.UserDtoRepository;

public class UserService {

    private UserDtoRepository userDtoRepository;

    public UserService(UserDtoRepository userDtoRepository) {
        this.userDtoRepository = userDtoRepository;
    }

    public void createNewUser(UserModel userModel) {
        this.userDtoRepository.createUser(userModel);
    }


    // void addWin(String username);

    public void addLoss(String username) {
        UserModel userData = this.userDtoRepository.getUserData(username);
        userData.setLooses(userData.getLooses()+1);
        userData.setElo(userData.getElo()-5);
        this.userDtoRepository.editUser(userData);
    }

    public void addWin(String username) {
        UserModel userData = this.userDtoRepository.getUserData(username);
        userData.setLooses(userData.getLooses()+1);
        userData.setElo(userData.getElo()+5);
        this.userDtoRepository.editUser(userData);
    }
}
