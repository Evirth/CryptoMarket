package pl.csrv.divinecraft.evirth.cryptomarket.models;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public String[] printBalance() {
        StringBuilder sb = new StringBuilder();
        double b = 0.0;
        for (Map.Entry<String, Coin> m : this.balance.entrySet()) {
            CoinMarket coin = CoinMarketCap.ticker(m.getKey().replace(" ", "-")).get();
            double usd = coin.getPriceUSD() * m.getValue().getAmount();
            sb.append(String.format("#%d. %s (%s) - %.8f ($%.2f - %d %s)\n", coin.getRank(), coin.getName(), coin.getSymbol(), m.getValue().getAmount(), usd, (int) Math.floor(usd / CryptoMarket.config.getPrice()), CryptoMarket.resourceManager.getResource("DiamondOrS")));
            b += usd;
        }
        sb.append("------------------------------");
        sb.insert(0, String.format(CryptoMarket.resourceManager.getResource("PlayerBalance") + "\n", this.player, b, (int) Math.floor(b / CryptoMarket.config.getPrice())));
        return sb.toString().split("\n");
    }

    public String[] printHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.translateAlternateColorCodes('&', "&6----- CryptoMarket ----- History (##/##) -----\n"));

        for (Transaction t : this.getTransactions()) {
            String msg = null;
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(t.getTransactionDate());
            switch (t.getType()) {
                case WITHDRAWAL:
                    msg = String.format("[&aW&f][%s] %.8f %s (&b%d&f %s&f - $%.2f)", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD());
                    break;
                case DEPOSIT:
                    msg = String.format("[&cD&f][%s] %.8f %s (&b%d&f %s&f - $%.2f)", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD());
                    break;
                case TRANSFER:
                    if (this.player.equals(t.getToPlayer())) {
                        msg = String.format("[&7T&f][%s] %.8f %s (&b%d&f %s&f - $%.2f) <- %s", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD(), t.getExecutorName());
                    } else {
                        msg = String.format("[&7T&f][%s] %.8f %s (&b%d&f %s&f - $%.2f) -> %s", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD(), t.getToPlayer());
                    }
                    break;
                case EXCHANGE:
                    msg = String.format("[&6E&f][%s] %.8f %s (&b%d&f %s&f - $%.2f) -> %.8f %s", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD(), t.getAmountOfNewCrypto(), t.getToCurrency());
                    break;
                case ADMIN_ADD:
                    msg = String.format("[&2+&f][%s] %.8f %s (&b%d&f %s&f - $%.2f) by %s", date, t.getAmountOfCrypto(), t.getToCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD(), t.getExecutorName());
                    break;
                case ADMIN_REMOVE:
                    msg = String.format("[&4-&f][%s] %.8f %s (&b%d&f %s&f - $%.2f) by %s", date, t.getAmountOfCrypto(), t.getFromCurrency(), t.getAmountOfDiamonds(), CryptoMarket.resourceManager.getResource("DiamondOrS"), t.getAmountOfCrypto() * t.getCryptoPriceInUSD(), t.getExecutorName());
                    break;
            }
            sb.append(ChatColor.translateAlternateColorCodes('&', msg + "\n"));
        }

        return sb.toString().split("\n");
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Map<String, Coin> getBalance() {
        return this.balance;
    }

    public void setBalance(Map<String, Coin> balance) {
        this.balance = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.balance.putAll(balance);
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
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
}
