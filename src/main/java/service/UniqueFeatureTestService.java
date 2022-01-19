package service;

import lombok.AllArgsConstructor;
import model.UserModel;

@AllArgsConstructor
public class UniqueFeatureTestService {

    public void uniqueFeature(int i, UserModel firstUser, UserModel secondUser) {
        if (i == 5) {
            firstUser.setElo(firstUser.getElo() + 20);
        } else if (i == 99) {
            firstUser.setElo(firstUser.getElo() + 10);
            secondUser.setElo(secondUser.getElo() + 10);
        } else if (i == 100) {
            firstUser.setElo(firstUser.getElo() + 5);
            secondUser.setElo(secondUser.getElo() + 5);
        }
    }
}
