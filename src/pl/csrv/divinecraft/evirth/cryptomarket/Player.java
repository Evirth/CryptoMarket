package pl.csrv.divinecraft.evirth.cryptomarket;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;
import pl.csrv.divinecraft.evirth.cryptomarket.helpers.XmlSerializationHelper;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Coin;
import pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Transaction;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class Player {
    private PlayerAccount account;
    private org.bukkit.entity.Player player;
    private String name;

    public Player(String name) {
        this.name = name;
        this.player = Bukkit.getServer().getPlayer(name);
        this.account = new PlayerAccount(name);
        Initialize();
    }

    public void withdraw(int amount, String crypto) {
        try {
            if (!this.account.getBalance().containsKey(crypto)) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), crypto));
                return;
            }

            Coin coin = this.account.getBalance().get(crypto);
            int diamondsFromBalance = PlayerAccount.calculateDiamondPriceOfCoins(coin.getName(), coin.getAmount());
            if (diamondsFromBalance >= amount) {
                double coinPrice = CoinMarketCap.ticker(coin.getName()).get().getPriceUSD();
                double diamondsAsCrypto = amount * CryptoMarket.config.price / coinPrice;
                double actualBalance = coin.getAmount();

                Map<String, Coin> m = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                m.putAll(this.account.getBalance());
                double resultBalance = actualBalance - diamondsAsCrypto;
                if (resultBalance != 0) {
                    coin.setAmount(resultBalance);
                    m.put(coin.getName(), coin);
                } else {
                    m.remove(coin.getName());
                }
                this.account.setBalance(m);

                List<Transaction> t = new ArrayList<>(this.account.getTransactions());
                t.add(new Transaction(this.name, new Date(), coin.getName(), CryptoMarket.resourceManager.getResource("Diamonds"), TransactionType.WITHDRAWAL, diamondsAsCrypto, amount, coinPrice));
                this.account.setTransactions(t);

                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                diamonds.setAmount(amount);
                this.player.getInventory().addItem(diamonds);

                checkBalance();
                Update();

            } else {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), this.account.getBalance().get(crypto).getName()));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted") + " " + e.getMessage());
        }
    }

    public void deposit(int amount, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }
            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(amount);
            if (this.player.getInventory().contains(Material.DIAMOND, amount)) {
                double amountOfCrypto = amount * CryptoMarket.config.price / coin.getPriceUSD();
                double at = amountOfCrypto;
                if (this.account.getBalance().containsKey(coin.getName())) {
                    amountOfCrypto += this.account.getBalance().get(coin.getName()).getAmount();
                }

                Coin c;
                Map<String, Coin> hm = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
                hm.putAll(this.account.getBalance());
                if (hm.containsKey(coin.getName())) {
                    c = this.account.getBalance().get(coin.getName());
                    c.setAmount(amountOfCrypto);
                } else {
                    c = new Coin(coin.getName(), coin.getSymbol(), amountOfCrypto);
                }
                hm.put(coin.getName(), c);
                this.account.setBalance(hm);

                List<Transaction> t = new ArrayList<>(this.account.getTransactions());
                t.add(new Transaction(this.name, new Date(), CryptoMarket.resourceManager.getResource("Diamonds"), coin.getName(), TransactionType.DEPOSIT, at, amount, coin.getPriceUSD()));
                this.account.setTransactions(t);

                this.player.getInventory().removeItem(diamonds);
                checkBalance();
                Update();
            } else {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("DontHaveThatAmountOfDiamonds"));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void exchange(String fromCrypto, double amount, String toCrypto) {

    }

    public void transfer(String crypto, double amount, String toPlayer) {

    }

    public void checkBalance() {
        this.player.sendMessage(this.account.printBalance().split("\n"));
    }

    private void Initialize() {
        File file = new File(Paths.get(CryptoMarket.pluginDir, "Players", String.format("%s.xml", this.name)).toString());
        if (file.exists()) {
            try {
                PlayerAccount acc = (PlayerAccount) XmlSerializationHelper.XmlDeserialize(this.name);
                this.account = new PlayerAccount(acc);
            } catch (Exception e) {
                Bukkit.getLogger().warning(e.getMessage());
            }
        } else {
            this.account = new PlayerAccount(this.name);
        }
    }

    private void Update() {
        try {
            XmlSerializationHelper.XmlSerialize(this.name, this.account);
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }
}
