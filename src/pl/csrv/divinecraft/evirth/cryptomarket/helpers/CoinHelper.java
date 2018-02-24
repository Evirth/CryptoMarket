package pl.csrv.divinecraft.evirth.cryptomarket.helpers;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;

public class CoinHelper {
    public static double calculateUSDPriceOfCoins(String crypto, double amount) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto).get();
        if (coin != null) {
            return coin.getPriceUSD() * amount;
        }
        throw new IllegalArgumentException("Crypto not found.");
    }

    public static int calculateAmountOfDiamondsFromCoins(String crypto, double amountOfCrypto) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto).get();
        if (coin != null) {
            return (int) Math.floor(coin.getPriceUSD() * amountOfCrypto / CryptoMarket.config.price);
        }
        throw new IllegalArgumentException("Crypto not found.");
    }

    public static int calculateAmountOfDiamondsFromCoins(double cryptoPriceInUSD, double amountOfCrypto) {
        return (int) Math.floor(cryptoPriceInUSD * amountOfCrypto / CryptoMarket.config.price);
    }

    public static double calculateAmountOfCryptoFromDiamonds(String crypto, int diamonds) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto).get();
        if (coin != null) {
            return diamonds * CryptoMarket.config.price / coin.getPriceUSD();
        }
        throw new IllegalArgumentException("Crypto not found.");
    }

    public static double calculateAmountOfCryptoFromDiamonds(int diamonds, double cryptoPriceInUSD) {
        return diamonds * CryptoMarket.config.price / cryptoPriceInUSD;
    }

    public static double calculateAmountOfNewCrypto(String oldCrypto, double amountOfOldCrypto, String newCrypto) throws IllegalArgumentException {
        CoinMarket oldCoin = CoinMarketCap.ticker(oldCrypto).get();
        CoinMarket newCoin = CoinMarketCap.ticker(newCrypto).get();
        if (oldCoin != null && newCoin != null) {
            return oldCoin.getPriceUSD() * amountOfOldCrypto / newCoin.getPriceUSD();
        }
        throw new IllegalArgumentException("Crypto not found.");
    }

    public static double calculateAmountOfNewCrypto(double oldCryptoPriceInUSD, double amountOfOldCrypto, double newCryptoPriceInUSD) {
        return oldCryptoPriceInUSD * amountOfOldCrypto / newCryptoPriceInUSD;
    }
}
