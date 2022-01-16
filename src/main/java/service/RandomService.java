package service;

import lombok.AllArgsConstructor;
import model.CardModel;
import model.Monster;
import model.helper.MonsterType;
import model.helper.Type;
import org.apache.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class RandomService {

    private static final Logger logger = Logger.getLogger(RandomService.class);

    public CardModel getRandomCardFromDeck(List<CardModel> deck) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            return deck.get(rand.nextInt(deck.size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }

    public void setRandomDamage(CardModel cardModelFirst, CardModel cardModelSecond) {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            cardModelFirst.setDamage(rand.nextInt(100));
            cardModelSecond.setDamage(rand.nextInt(100));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
    }

    public Type getRandomType() {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            List<Type> typeList = new ArrayList<>();
            Collections.addAll(typeList, Type.values());
            return typeList.get(rand.nextInt(typeList.size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }

    public MonsterType getRandomMonsterType() {
        try {
            Random rand = SecureRandom.getInstanceStrong();
            List<MonsterType> typeList = new ArrayList<>();
            Collections.addAll(typeList, MonsterType.values());
            return typeList.get(rand.nextInt(typeList.size()));
        } catch (NoSuchAlgorithmException e) {
            logger.info("Something went wrong");
        }
        return null;
    }
}
