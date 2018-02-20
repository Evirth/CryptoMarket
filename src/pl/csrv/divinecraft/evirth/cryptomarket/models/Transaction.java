package pl.csrv.divinecraft.evirth.cryptomarket.models;

import pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;

import java.util.Date;

public class Transaction {
    private String playerName;
    private Date transactionDate;
    private String fromCurrency;
    private String toCurrency;
    private TransactionType type;
    private double amountOfCrypto;
    private int amountOfDiamonds;
    private double cryptoPriceInUSD;

    public Transaction() { }

    public Transaction(String playerName, Date transactionDate, String fromCurrency, String toCurrency, TransactionType type, double amountOfCrypto, int amountOfDiamonds, double cryptoPriceInUSD) {
        this.playerName = playerName;
        this.transactionDate = transactionDate;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.type = type;
        this.amountOfCrypto = amountOfCrypto;
        this.amountOfDiamonds = amountOfDiamonds;
        this.cryptoPriceInUSD = cryptoPriceInUSD;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmountOfCrypto() {
        return amountOfCrypto;
    }

    public void setAmountOfCrypto(double amount) {
        this.amountOfCrypto = amount;
    }

    public int getAmountOfDiamonds() {
        return amountOfDiamonds;
    }

    public void setAmountOfDiamonds(int amountOfDiamonds) {
        this.amountOfDiamonds = amountOfDiamonds;
    }

    public double getCryptoPriceInUSD() {
        return cryptoPriceInUSD;
    }

    public void setCryptoPriceInUSD(double cryptoPriceInUSD) {
        this.cryptoPriceInUSD = cryptoPriceInUSD;
    }
}
