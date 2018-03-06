package main.java.pl.csrv.divinecraft.evirth.cryptomarket.models;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;

import java.util.Date;

public class Transaction {
    private String executorName;
    private String toPlayer;
    private String fromPlayer;
    private Date transactionDate;
    private String fromCurrency;
    private String toCurrency;
    private TransactionType type;
    private double amountOfCrypto;
    private Double amountOfNewCrypto;
    private int amountOfDiamonds;
    private double cryptoPriceInUSD;
    private Double newCryptoPriceInUSD;
    private Double tax;

    public Transaction() { }

    public Transaction(String executorName, Date transactionDate, String fromCurrency, String toCurrency, TransactionType type, double amountOfCrypto, int amountOfDiamonds, double cryptoPriceInUSD, String fromPlayer, String toPlayer, Double amountOfNewCrypto, Double newCryptoPriceInUSD, Double tax) {
        this.executorName = executorName;
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.transactionDate = transactionDate;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.type = type;
        this.amountOfCrypto = amountOfCrypto;
        this.amountOfNewCrypto = amountOfNewCrypto;
        this.amountOfDiamonds = amountOfDiamonds;
        this.cryptoPriceInUSD = cryptoPriceInUSD;
        this.newCryptoPriceInUSD = newCryptoPriceInUSD;
        this.tax = tax;
    }

    public String getExecutorName() {
        return this.executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public String getToPlayer() {
        return this.toPlayer;
    }

    public void setToPlayer(String toPlayer) {
        this.toPlayer = toPlayer;
    }

    public String getFromPlayer() {
        return this.fromPlayer;
    }

    public void setFromPlayer(String fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public Date getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getFromCurrency() {
        return this.fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return this.toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public TransactionType getType() {
        return this.type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmountOfCrypto() {
        return this.amountOfCrypto;
    }

    public void setAmountOfCrypto(double amount) {
        this.amountOfCrypto = amount;
    }

    public int getAmountOfDiamonds() {
        return this.amountOfDiamonds;
    }

    public void setAmountOfDiamonds(int amountOfDiamonds) {
        this.amountOfDiamonds = amountOfDiamonds;
    }

    public double getCryptoPriceInUSD() {
        return this.cryptoPriceInUSD;
    }

    public void setCryptoPriceInUSD(double cryptoPriceInUSD) {
        this.cryptoPriceInUSD = cryptoPriceInUSD;
    }

    public Double getAmountOfNewCrypto() {
        return this.amountOfNewCrypto;
    }

    public void setAmountOfNewCrypto(Double amountOfNewCrypto) {
        this.amountOfNewCrypto = amountOfNewCrypto;
    }

    public Double getNewCryptoPriceInUSD() {
        return this.newCryptoPriceInUSD;
    }

    public void setNewCryptoPriceInUSD(Double newCryptoPriceInUSD) {
        this.newCryptoPriceInUSD = newCryptoPriceInUSD;
    }

    public Double getTax() {
        return this.tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }
}
