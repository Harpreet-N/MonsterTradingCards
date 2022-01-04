package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    @JsonProperty("Password")
    private String password;

    /**
     * Every user has 20 coins
     */
    @Getter
    @Setter
    private int balance;

    @Getter
    @Setter
    private StackModel stackModel;

    @Getter
    @Setter
    private Deck deck;

}
