package main.java.ru.clevertec.models;

public class DiscountCard {
    private int id;
    private String number;
    private int discountPercentage;

    public DiscountCard(int id, String number, int discountPercentage) {
        this.id = id;
        this.number = number;
        this.discountPercentage = discountPercentage;
    }

    public DiscountCard(String[] parts) {
        this(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]));
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    @Override
    public String toString() {
        return "DiscountCard{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", discountPercentage=" + discountPercentage +
                '}';
    }
}
