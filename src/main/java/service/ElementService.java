package service;

import model.helper.Type;

public class ElementService {

    public static boolean elementTypeIsEffective(Type firstType, Type secondType) {
        if (firstType.equals(Type.FIRE) && secondType.equals(Type.WATER)) {
            return false;
        } else if (firstType.equals(Type.WATER) && secondType.equals(Type.FIRE)) {
            return true;
        } else if (firstType.equals(Type.NORMAL) && secondType.equals(Type.FIRE)) {
            return false;
        } else if (firstType.equals(Type.FIRE) && secondType.equals(Type.NORMAL)) {
            return true;
        } else if (firstType.equals(Type.NORMAL) && secondType.equals(Type.WATER)) {
            return true;
        } else if (firstType.equals(Type.WATER) && secondType.equals(Type.NORMAL)) {
            return false;
        }
        return false;
    }
}
