package main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.CoinMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.Global;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CoinMarketCap {
    private static final String baseUrl = "https://api.coinmarketcap.com/v1/";

    public static List<CoinMarket> ticker() {
        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(baseUrl).path("ticker");
            String response = getResponse(uriBuilder.build().toURL());
            return new ObjectMapper().readValue(response, new TypeReference<List<CoinMarket>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public static List<CoinMarket> ticker(int limit) {
        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(baseUrl).path("ticker").queryParam("limit", limit);
            String response = getResponse(uriBuilder.build().toURL());
            return new ObjectMapper().readValue(response, new TypeReference<List<CoinMarket>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public static CoinMarket ticker(String coinName) {
        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(baseUrl).path("ticker").path(coinName);
            String response = getResponse(uriBuilder.build().toURL());
            List<CoinMarket> list = new ObjectMapper().readValue(response, new TypeReference<List<CoinMarket>>() {});
            return list.isEmpty() ? null : list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public static Global global() {
        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(baseUrl).path("global");
            String response = getResponse(uriBuilder.build().toURL());
            return new ObjectMapper().readValue(response, Global.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String getResponse(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        br.lines().forEach(sb::append);
        return sb.toString();
    }
}
