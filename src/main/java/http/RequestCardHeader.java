package http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import model.Monster;
import model.helper.MonsterType;
import model.helper.Type;

@Data
@JsonIgnoreProperties
public class RequestCardHeader {

    @JsonProperty("Id")
    public String id;

    @JsonProperty("Name")
    public String name;

    public MonsterType monstertype;

    public Type elementtype;

    @JsonProperty("Damage")
    public double damage;
}

