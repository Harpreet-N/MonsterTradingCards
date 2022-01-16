package service;

import http.RequestCardHeader;
import model.helper.MonsterType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PackageServiceTest {

    private PackageService packageService = new PackageService();


    @ParameterizedTest(name = "Random Monster Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithMonsterTypeAndExpected")
    void getElementsAndMonsterTest(RequestCardHeader cardHeader, MonsterType type) {
        packageService.getElementsAndMonster(cardHeader);
        assertThat(cardHeader.getMonsterType())
                .isEqualTo(type);
    }

    public static Stream<Arguments> provideCardWithMonsterTypeAndExpected() {
        return Stream.of(
                Arguments.of(new RequestCardHeader("845f0dc7","WaterGoblin", null, null, 10.0), MonsterType.GOBLIN),
                Arguments.of(new RequestCardHeader("845f0dc7","Dragon", null, null, 10.0), MonsterType.DRAGON),
                Arguments.of(new RequestCardHeader("845f0dc7","FireSpell", null, null, 10.0), MonsterType.SPELL)
        );
    }

    @ParameterizedTest(name = "Random Monster Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithMonsterTypeAndExpected")
    void getElementTypTest(RequestCardHeader cardHeader, MonsterType type) {
        packageService.getElementTyp(cardHeader);
        assertNotNull(cardHeader.getElementType());
    }


}
