import org.junit.jupiter.api.Test;
import pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.CoinMarketCap;
import pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.Coin;
import pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.Global;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoinMarketCapTest {

    @Test
    void tickerTest() {
        List<Coin> ticker = CoinMarketCap.ticker();
        assertNotNull(ticker);
        assertEquals(ticker.size(), 100);
    }

    @Test
    void tickerLimitTest() {
        int limit = 10;
        List<Coin> ticker = CoinMarketCap.ticker(limit);
        assertNotNull(ticker);
        assertEquals(ticker.size(), limit);
    }

    @Test
    void tickerCoinNameTest() {
        Coin bitcoin = CoinMarketCap.ticker("bitcoin");
        assertNotNull(bitcoin);
        Coin ethereum = CoinMarketCap.ticker("ethereum");
        assertNotNull(ethereum);
        Coin iota = CoinMarketCap.ticker("iota");
        assertNotNull(iota);
    }

    @Test
    void globalTest() {
        Global global = CoinMarketCap.global();
        assertNotNull(global);
    }
}