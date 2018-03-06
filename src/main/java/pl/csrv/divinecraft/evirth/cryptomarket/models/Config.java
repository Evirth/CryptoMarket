package main.java.pl.csrv.divinecraft.evirth.cryptomarket.models;

public class Config {
    private int price;
    private String lang;
    private double fee;

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public double getFee() {
        return this.fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
