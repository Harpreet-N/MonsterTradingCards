package model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    @Getter
    private final UserModel userModel;

    @Getter
    private final List<CardModel> cardModelList;

    public Deck(UserModel userModel) {
        this.userModel = userModel;
        this.cardModelList = new ArrayList<>(4);
    }
}
