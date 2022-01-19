package service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserNameAndTokenServiceTest {

    private UserNameAndTokenService userNameAndTokenService = new UserNameAndTokenService();

    @Test
    void getUserNameAndTokenEmptyTest() {
       String [] value = {"POST /users HTT...", "Host: localhost...", "User-Agent: cur...", "Accept: */*", "Content-Type: application/json", "Content-Length: 44"};
       String [] result = {"",""};

       assertEquals(Arrays.toString(userNameAndTokenService.getUserNameAndToken(value)), Arrays.toString(result));

    }

    @Test
    void getUserNameAndTokenTest() {
       String [] value = {"POST /packages ...", "Host: localhost...", "User-Agent: cur...", "Accept: */*", "Content-Type: application/json", "Authorization: Basic admin-mtcgToken", "Content-Length: 412"};
       String [] result = {"admin","mtcgToken"};
       assertEquals(Arrays.toString(userNameAndTokenService.getUserNameAndToken(value)), Arrays.toString(result));
    }
}
