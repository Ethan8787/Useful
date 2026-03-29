package dev.ethan.useful.models;

public class PriceData {
    private final double buy;
    private final double sell;

    public PriceData(double buy, double sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public double getBuy() { return buy; }
    public double getSell() { return sell; }
}