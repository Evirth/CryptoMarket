import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.CoinMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.Global;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class CoinMarketCapTest {

    @Test
    public void tickerTest() {
        List<CoinMarket> ticker = CoinMarketCap.ticker();
        Assert.assertNotNull(ticker);
        Assert.assertEquals(ticker.size(), 100);
    }

    @Test
    public void tickerLimitTest() {
        int limit = 10;
        List<CoinMarket> ticker = CoinMarketCap.ticker(limit);
        Assert.assertNotNull(ticker);
        Assert.assertEquals(ticker.size(), limit);
    }

    @Test
    public void tickerCoinNameTest() {
        CoinMarket bitcoin = CoinMarketCap.ticker("bitcoin");
        Assert.assertNotNull(bitcoin);
        CoinMarket ethereum = CoinMarketCap.ticker("ethereum");
        Assert.assertNotNull(ethereum);
        CoinMarket iota = CoinMarketCap.ticker("iota");
        Assert.assertNotNull(iota);
    }

    @Test
    public void globalTest() {
        Global global = CoinMarketCap.global();
        Assert.assertNotNull(global);
    }
}