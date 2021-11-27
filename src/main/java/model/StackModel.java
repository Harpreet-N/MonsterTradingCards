package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StackModel {

    @Getter
    private final List<CardModel> cardModelList;

    @Getter
    @Setter
    private UserModel userModel;

    public StackModel() {
        this.cardModelList = new ArrayList<>();
    }

    public void addCard(CardModel cardModel) {
        cardModelList.add(cardModel);
    }
}
