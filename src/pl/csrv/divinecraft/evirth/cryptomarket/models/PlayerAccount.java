package pl.csrv.divinecraft.evirth.cryptomarket.models;

import com.lucadev.coinmarketcap.CoinMarketCap;
import org.bukkit.Bukkit;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@XmlRootElement
public class PlayerAccount {
    private String player;
    private Map<String, Coin> balance;
    private List<Transaction> transactions;

    public PlayerAccount() { }

    public PlayerAccount(String playerName, Map<String, Coin> balance, List<Transaction> transactions) {
        this.player = playerName;
        this.balance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.balance.putAll(balance);
        this.transactions = transactions;
    }

    public PlayerAccount(String playerName) {
        this.player = playerName;
        this.balance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.transactions = new ArrayList<>();
    }

    public PlayerAccount(PlayerAccount account) {
        this(account.player, account.balance, account.transactions);
    }

    public String printBalance() {
        StringBuilder sb = new StringBuilder();
        double b = 0.0;
        for(Map.Entry<String, Coin> m : this.balance.entrySet()) {
            double usd = CoinMarketCap.ticker(m.getKey()).get().getPriceUSD() * m.getValue().getAmount();
            sb.append(String.format("#%d. %s (%s) - %.8f ($%.2f - %d %s)\n", m.getValue().getRank(), m.getKey(), m.getValue().getSymbol(), m.getValue().getAmount(), usd, (int) Math.floor(usd / CryptoMarket.config.price), CryptoMarket.resourceManager.getResource("DiamondOrS")));
            b += usd;
        }
        sb.append("------------------------------");
        sb.insert(0, String.format(CryptoMarket.resourceManager.getResource("YourBalance") + "\n", b, (int) Math.floor(b / CryptoMarket.config.price)));
        return sb.toString();
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Map<String, Coin> getBalance() {
        return balance;
    }

    public void setBalance(Map<String, Coin> balance) {
        this.balance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.balance.putAll(balance);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double getBalanceInUSD() {
        double b = 0.0;
        try {
            for (Map.Entry<String, Coin> entry : this.balance.entrySet()) {
                b += CoinMarketCap.ticker(entry.getKey()).get().getPriceUSD() * entry.getValue().getAmount();
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
        return b;
    }

    public static double calculateUSDPriceOfCoins(String crypto, double amount) {
        return CoinMarketCap.ticker(crypto).get().getPriceUSD() * amount;
    }

    public static int calculateDiamondPriceOfCoins(String crypto, double amount) {
        return (int) Math.floor(CoinMarketCap.ticker(crypto).get().getPriceUSD() * amount / CryptoMarket.config.price);
    }
}
