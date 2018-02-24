package pl.csrv.divinecraft.evirth.cryptomarket.models;

import com.lucadev.coinmarketcap.CoinMarketCap;

public class Coin {
    private String name;
    private String symbol;
    private double amount;

    public Coin() { }

    public Coin(String name, String symbol, double amount) {
        this.name = name;
        this.symbol = symbol;
        this.amount = amount;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getRank() {
        return CoinMarketCap.ticker(this.getId()).get().getRank();
    }

    private String getId() {
        return this.name.replace(" ", "-");
    }
}
