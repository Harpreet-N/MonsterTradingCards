package model.store;

import lombok.Data;
import model.CardModel;

import java.util.List;

@Data
public class Package {
    private final String id;
    private final int balance = 5;
    private List<CardModel> packageCards;
}
