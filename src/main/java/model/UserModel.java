package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class UserModel {

    // PK for DB later
    @NonNull
    @Getter
    @Setter
    @JsonProperty("Username")
    private String username;

    @Getter
    @Setter
    @JsonProperty("Name")
    private String name;

    @Getter
    @Setter
    @JsonProperty("Password")
    private String password;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    @JsonProperty("Bio")
    private String bio;

    @Getter
    @Setter
    @JsonProperty("Image")
    private String image;

    /**
     * Every user has 20 coins
     */
    @Getter
    @Setter
    @JsonProperty("Balance")
    private int balance;

    @Getter
    @Setter
    @JsonProperty("StackModel")
    private List<StackModel> stackModel;

    @Getter
    @Setter
    @JsonProperty("Deck")
    private List<Deck> deck;

    @Getter
    @Setter
    @JsonProperty("Elo")
    private int elo;

    @Getter
    @Setter
    @JsonProperty("Wins")
    private int wins;

    @Getter
    @Setter
    @JsonProperty("Looses")
    private int looses;

    // To inital the model
    UserModel() {
    }

}
