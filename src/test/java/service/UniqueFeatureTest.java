package service;

import model.UserModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UniqueFeatureTest {


    private UniqueFeatureTestService uniqueFeatureTestService = new UniqueFeatureTestService();

    @BeforeEach
    void createUser(){
    }

    @ParameterizedTest(name = "Test Elo Result {0} should return {1} {2}")
    @MethodSource("provideCardWithMonsterTypeAndExpected")
    void getElementsAndMonsterTest(UserModel firstUser, UserModel secondUser, int round, int eloFirst, int eloSecond) {
        uniqueFeatureTestService.uniqueFeature(round, firstUser, secondUser);
        assertThat(firstUser.getElo())
                .isEqualTo(eloFirst);

        assertThat(secondUser.getElo())
                .isEqualTo(eloSecond);
    }

    public static Stream<Arguments> provideCardWithMonsterTypeAndExpected() {
        return Stream.of(
                Arguments.of(new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 50, 50,50),
                        new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 10, 10,10),
                        5,70, 10),
                Arguments.of(new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 50, 50,50),
                        new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 10, 10,10),
                        99,60, 20),
                Arguments.of(new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 50, 50,50),
                        new UserModel("Dragon", "G", "Secret","token", "Bio","image", 10, null , null, 10, 10,10),
                        100,55, 15)
        );
    }
}
