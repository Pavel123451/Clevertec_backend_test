package ru.clevertec.models;

public class DiscountCard {
    private Long id;
    private int number;
    private short discountPercentage;

    public DiscountCard() {
    }

    public DiscountCard(Long id, int number, short discountPercentage) {
        this.id = id;
        this.number = number;
        this.discountPercentage = discountPercentage;
    }

    public Long getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public short getDiscountPercentage() {
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
