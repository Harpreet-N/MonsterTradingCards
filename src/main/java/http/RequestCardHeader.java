package http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class RequestCardHeader {

    @JsonProperty("Id")
    public String id;

    @JsonProperty("Name")
    public String name;

    public String monstertype;

    public String elementtype;

    @JsonProperty("Damage")
    public double damage;
}

