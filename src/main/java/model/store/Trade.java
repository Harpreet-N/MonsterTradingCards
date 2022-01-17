package model.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Trade {
    @JsonProperty("Id")
    private String id;
    private String owner;

    @JsonProperty("CardToTrade")
    private String cardToTrade;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("MinimumDamage")
    private double minDamage;

    Trade() {}
}
