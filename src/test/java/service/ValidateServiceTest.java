package service;

import model.helper.MonsterType;
import model.helper.Type;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidateServiceTest {

    private ValidateService validateService = new ValidateService();

    @ParameterizedTest(name = "Monster Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithMonsterTypeAndExpected")
    void validateMonsterTypeTest(MonsterType monsterType, MonsterType monsterType1, int expectedDamage) {
        assertThat(validateService.validateMonsterType(monsterType, monsterType1, 10))
                .isEqualTo(expectedDamage);
    }

    public static Stream<Arguments> provideCardWithMonsterTypeAndExpected() {
        return Stream.of(
                Arguments.of(MonsterType.GOBLIN, MonsterType.DRAGON,0),
                Arguments.of(MonsterType.ORK,MonsterType.WIZZARD,0),
                Arguments.of(MonsterType.FIREELVES, MonsterType.DRAGON,0),
                Arguments.of(MonsterType.DRAGON, MonsterType.FIREELVES,0),
                Arguments.of(MonsterType.DRAGON, MonsterType.DRAGON,10)
        );
    }


    @ParameterizedTest(name = "Type Test {0} should return {1} {2}")
    @MethodSource("provideCardWithTypeAndExpected")
    void validateTypeTest(Type type, Type type1, MonsterType monsterType, int expectedDamage) {
        assertThat(validateService.validateType(type, type1, monsterType))
                .isEqualTo(expectedDamage);
    }

    public static Stream<Arguments> provideCardWithTypeAndExpected() {
        return Stream.of(
                Arguments.of(Type.WATER, Type.FIRE, MonsterType.DRAGON,2),
                Arguments.of(Type.FIRE, Type.WATER, MonsterType.DRAGON,3),
                Arguments.of(Type.NORMAL, Type.WATER, MonsterType.DRAGON,2),
                Arguments.of(Type.FIRE, Type.NORMAL, MonsterType.DRAGON,2),
                Arguments.of(Type.WATER, Type.NORMAL, MonsterType.KNIGHT,0),
                Arguments.of(Type.WATER, Type.WATER, MonsterType.FIREELVES,0)
        );
    }
}
