package pl.csrv.divinecraft.evirth.cryptomarket.models;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.Bukkit;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.helpers.XmlSerializationHelper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
public class PlayerAccount {
    private String player;
    private Map<String, Double> balance;
    private List<Transaction> transactions;

    public PlayerAccount() { }

    public PlayerAccount(String playerName, Map<String, Double> balance, List<Transaction> transactions) {
        this.player = playerName;
        this.balance = balance;
        this.transactions = transactions;
    }

    public PlayerAccount(String playerName) {
        this.player = playerName;
        this.balance = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    public PlayerAccount(PlayerAccount account) {
        this(account.player, account.balance, account.transactions);
    }

    public String printBalance() {
        StringBuilder sb = new StringBuilder();
        double b = 0.0;
        for(Map.Entry<String, Double> m : this.balance.entrySet()) {
            double usd = CoinMarketCap.ticker(m.getKey()).get().getPriceUSD() * m.getValue();
            sb.append(String.format("%s - %.8f ($%.2f - %d Diamonds)\n", m.getKey(), m.getValue(), usd, (int) Math.floor(usd / CryptoMarket.config.price)));
            b += usd;
        }
        sb.append("------------------------------");
        sb.insert(0, String.format("Your balance corresponds to the equivalent of $%.2f (%d Diamond(s))\n", b, (int) Math.floor(b / CryptoMarket.config.price)));
        return sb.toString();
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Map<String, Double> getBalance() {
        return balance;
    }

    public void setBalance(Map<String, Double> balance) {
        this.balance = balance;
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
            for (Map.Entry<String, Double> entry : this.balance.entrySet()) {
                b += CoinMarketCap.ticker(entry.getKey()).get().getPriceUSD() * entry.getValue();
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
