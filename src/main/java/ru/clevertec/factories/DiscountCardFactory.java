package main.java.ru.clevertec.factories;

import java.util.Scanner;

import main.java.ru.clevertec.models.DiscountCard;

public class DiscountCardFactory {
    public static DiscountCard createDiscountCard(DiscountCardType type, String[] parts) {
        switch (type) {
            case STANDARD:
                return new DiscountCard(parts);
            default:
                throw new IllegalArgumentException("Unknown discount card type: " + type);
        }
    }

    public enum DiscountCardType {
        STANDARD
    }
}
