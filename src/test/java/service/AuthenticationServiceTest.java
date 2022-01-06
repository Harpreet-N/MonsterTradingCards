package service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AuthenticationServiceTest {

    @Test
    void passwordIsEqualTest() {
        String password = "Secret";
        String random = "Hallo";
        String hash = AuthenticationService.hashPassword(password);

        assertThat(AuthenticationService.passwordIsEqual(password, hash)).isTrue();
        assertThat(AuthenticationService.passwordIsEqual(random, hash)).isFalse();
    }

    @Test
    void generateAuthTokenTest() {
        assertThat(AuthenticationService.generateAuthToken()).hasSize(36);
    }
}
