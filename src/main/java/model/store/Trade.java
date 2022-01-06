package model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
public class Trade {
    @JsonProperty("Id")
    private String uuid;
    @Setter
    private String offerer;
    @JsonProperty("CardToTrade")
    private String cardToTrade;
    @JsonProperty("MinimumDamage")
    private double minDamage;
    @JsonProperty("WantsMonster")
    private boolean wantsMonster;
    @JsonProperty("WantsSpell")
    private boolean wantsSpell;

    Trade() {}
}
