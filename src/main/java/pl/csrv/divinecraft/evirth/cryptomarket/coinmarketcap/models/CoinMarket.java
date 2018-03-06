package main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class CoinMarket {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("rank")
    private Integer rank;

    @JsonProperty("price_usd")
    private Double price_usd;

    @JsonProperty("price_btc")
    private Double price_btc;

    @JsonProperty("24h_volume_usd")
    private Double daily_volume_usd;

    @JsonProperty("market_cap_usd")
    private Double market_cap_usd;

    @JsonProperty("available_supply")
    private Double available_supply;

    @JsonProperty("total_supply")
    private Double total_supply;

    @JsonProperty("max_supply")
    private Double max_supply;

    @JsonProperty("percent_change_1h")
    private Double percent_change_1h;

    @JsonProperty("percent_change_24h")
    private Double percent_change_24h;

    @JsonProperty("percent_change_7d")
    private Double percent_change_7d;

    @JsonProperty("last_updated")
    private Timestamp last_updated;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public int getRank() {
        return this.rank;
    }

    public Double getPriceUSD() {
        return this.price_usd;
    }

    public Double getPriceBTC() {
        return this.price_btc;
    }

    public Double getDailyVolumeUSD() {
        return this.daily_volume_usd;
    }

    public Double getMarketCapUSD() {
        return this.market_cap_usd;
    }

    public Double getAvailableSupply() {
        return this.available_supply;
    }

    public Double getTotalSupply() {
        return this.total_supply;
    }

    public Double getMaxSupply() {
        return this.max_supply;
    }

    public Double getPercentChange1h() {
        return this.percent_change_1h;
    }

    public Double getPercentChange24h() {
        return this.percent_change_24h;
    }

    public Double getPercentChange7d() {
        return this.percent_change_7d;
    }

    public Timestamp getLastUpdated() {
        return this.last_updated;
    }
}
