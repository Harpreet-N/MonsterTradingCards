package model.store;

import lombok.Data;
import model.CardModel;

import java.util.List;

@Data
public class Package {
    private final String UUID;
    private final int cost = 5;
    private List<CardModel> packageCards;
}
