package main.java.cryptomarket.coinmarketcap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Global {
    @JsonProperty("total_market_cap_usd")
    private Double total_market_cap_usd;

    @JsonProperty("total_24h_volume_usd")
    private Double total_24h_volume_usd;

    @JsonProperty("bitcoin_percentage_of_market_cap")
    private Double bitcoin_percentage_of_market_cap;

    @JsonProperty("active_currencies")
    private Integer active_currencies;

    @JsonProperty("active_assets")
    private Integer active_assets;

    @JsonProperty("active_markets")
    private Integer active_markets;

    @JsonProperty("last_updated")
    private Timestamp last_updated;
}
