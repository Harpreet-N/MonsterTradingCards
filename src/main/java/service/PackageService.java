package service;

import http.RequestCardHeader;
import model.CardModel;
import model.Monster;
import model.Spell;
import model.helper.MonsterType;
import model.helper.Type;

import java.util.ArrayList;
import java.util.List;

public class PackageService {

    private RandomService randomService = new RandomService();

    public List<CardModel> createPackage(String username, List<RequestCardHeader> pkgCards) {
        List<CardModel> packageToAdd = new ArrayList<>();
        for (RequestCardHeader c : pkgCards) {
            if (c.getName().contains("Spell")) {
                getElementTyp(c);
                c.setMonsterType(MonsterType.SPELL);
                packageToAdd.add(new Spell(c.getId(), username, AuthenticationService.generateAuthToken(), c.getElementType(), c.getMonsterType(), c.getDamage()));
            } else {
                getElementsAndMonster(c);
                packageToAdd.add(new Monster(c.getId(), username, AuthenticationService.generateAuthToken(), c.getElementType(), c.getMonsterType(), c.getDamage()));
            }
        }
        return packageToAdd;
    }

    public void getElementsAndMonster(RequestCardHeader c) {
        for (MonsterType type : MonsterType.values()) {
            if (c.getName().toUpperCase().contains(type.name())) {
                c.setMonsterType(type);
            }
        }
        getElementTyp(c);
        setRandomElementIfNull(c);
        setRandomMonsterIfNull(c);
    }

    public void getElementTyp(RequestCardHeader c) {
        for (Type type : Type.values()) {
            if (c.getName().toUpperCase().contains(type.name())) {
                c.setElementType(type);
            }
        }
        setRandomElementIfNull(c);
    }

    private void setRandomElementIfNull(RequestCardHeader c) {
        if (c.getElementType() == null) {
            c.setElementType(randomService.getRandomType());
        }
    }

    private void setRandomMonsterIfNull(RequestCardHeader c) {
        if (c.getMonsterType() == null) {
            c.setMonsterType(randomService.getRandomMonsterType());
        }
    }

}
