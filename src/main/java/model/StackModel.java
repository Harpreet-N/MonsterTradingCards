package model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class StackModel {

    @Getter
    @Setter
    private List<CardModel> cardModelList;
    private List<UserModel> userModelList;


    public StackModel() {
        this.cardModelList = new ArrayList<>();
        this.userModelList = new ArrayList<>();
    }
}
