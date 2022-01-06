package model.battle;

import lombok.Data;
import model.CardModel;

@Data
public class Battle {
    private final CardModel winner;
    private final CardModel looser;
}
