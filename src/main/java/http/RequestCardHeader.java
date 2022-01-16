package http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import model.helper.MonsterType;
import model.helper.Type;

@Data
@JsonIgnoreProperties
@AllArgsConstructor
public class RequestCardHeader {

    @JsonProperty("Id")
    public String id;

    @JsonProperty("Name")
    public String name;

    public MonsterType monsterType;

    public Type elementType;

    @JsonProperty("Damage")
    public double damage;
}

