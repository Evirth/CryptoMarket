package main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Global {
    @JsonProperty("total_market_cap_usd")
    private Double totalMarketCapUSD;

    @JsonProperty("total_24h_volume_usd")
    private Double total24hVolumeUSD;

    @JsonProperty("bitcoin_percentage_of_market_cap")
    private Double bitcoinPercentageOfMarketCap;

    @JsonProperty("active_currencies")
    private Integer activeCurrencies;

    @JsonProperty("active_assets")
    private Integer activeAssets;

    @JsonProperty("active_markets")
    private Integer activeMarkets;

    @JsonProperty("last_updated")
    private Timestamp lastUpdated;

    public Double getTotalMarketCapUSD() {
        return this.totalMarketCapUSD;
    }

    public Double getTotal24hVolumeUSD() {
        return this.total24hVolumeUSD;
    }

    public Double getBitcoinPercentageOfMarketCap() {
        return this.bitcoinPercentageOfMarketCap;
    }

    public Integer getActiveCurrencies() {
        return this.activeCurrencies;
    }

    public Integer getActiveAssets() {
        return this.activeAssets;
    }

    public Integer getActiveMarkets() {
        return this.activeMarkets;
    }

    public Timestamp getLastUpdated() {
        return this.lastUpdated;
    }
}
