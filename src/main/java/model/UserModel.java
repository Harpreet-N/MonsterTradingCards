package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public class UserModel {

    // PK for DB later
    @NonNull
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
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
