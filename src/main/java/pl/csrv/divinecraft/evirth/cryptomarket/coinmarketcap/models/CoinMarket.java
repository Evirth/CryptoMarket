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
    private Double priceUSD;

    @JsonProperty("price_btc")
    private Double priceBTC;

    @JsonProperty("24h_volume_usd")
    private Double dailyVolumeUSD;

    @JsonProperty("market_cap_usd")
    private Double marketCapUSD;

    @JsonProperty("available_supply")
    private Double availableSupply;

    @JsonProperty("total_supply")
    private Double totalSupply;

    @JsonProperty("max_supply")
    private Double maxSupply;

    @JsonProperty("percent_change_1h")
    private Double percentChange1h;

    @JsonProperty("percent_change_24h")
    private Double percentChange24h;

    @JsonProperty("percent_change_7d")
    private Double percentChange7d;

    @JsonProperty("last_updated")
    private Timestamp lastUpdated;

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
        return this.priceUSD;
    }

    public Double getPriceBTC() {
        return this.priceBTC;
    }

    public Double getDailyVolumeUSD() {
        return this.dailyVolumeUSD;
    }

    public Double getMarketCapUSD() {
        return this.marketCapUSD;
    }

    public Double getAvailableSupply() {
        return this.availableSupply;
    }

    public Double getTotalSupply() {
        return this.totalSupply;
    }

    public Double getMaxSupply() {
        return this.maxSupply;
    }

    public Double getPercentChange1h() {
        return this.percentChange1h;
    }

    public Double getPercentChange24h() {
        return this.percentChange24h;
    }

    public Double getPercentChange7d() {
        return this.percentChange7d;
    }

    public Timestamp getLastUpdated() {
        return this.lastUpdated;
    }
}
