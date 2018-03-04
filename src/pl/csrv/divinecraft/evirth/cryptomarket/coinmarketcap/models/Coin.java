package pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Coin {
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

    public Double getPrice_usd() {
        return this.price_usd;
    }

    public Double getPrice_btc() {
        return this.price_btc;
    }

    public Double getDaily_volume_usd() {
        return this.daily_volume_usd;
    }

    public Double getMarket_cap_usd() {
        return this.market_cap_usd;
    }

    public Double getAvailable_supply() {
        return this.available_supply;
    }

    public Double getTotal_supply() {
        return this.total_supply;
    }

    public Double getMax_supply() {
        return this.max_supply;
    }

    public Double getPercent_change_1h() {
        return this.percent_change_1h;
    }

    public Double getPercent_change_24h() {
        return this.percent_change_24h;
    }

    public Double getPercent_change_7d() {
        return this.percent_change_7d;
    }

    public Timestamp getLast_updated() {
        return this.last_updated;
    }
}
