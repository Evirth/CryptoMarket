package pl.csrv.divinecraft.evirth.cryptomarket;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;
import pl.csrv.divinecraft.evirth.cryptomarket.helpers.XmlSerializationHelper;
import pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Transaction;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
                this.player.sendMessage(String.format("You don't have any %s coin.", crypto));
                return;
            }

            int diamondsFromBalance = PlayerAccount.calculateDiamondPriceOfCoins(crypto, this.account.getBalance().get(crypto));
            if (diamondsFromBalance >= amount) {
                double diamondsAsCrypto = amount * CryptoMarket.config.price / CoinMarketCap.ticker(crypto).get().getPriceUSD();
                double actualBalance = this.account.getBalance().get(crypto);
                Map<String, Double> m = new HashMap<>(this.account.getBalance());
                m.put(crypto, actualBalance - diamondsAsCrypto);
                this.account.setBalance(m);

                List<Transaction> t = new ArrayList<>(this.account.getTransactions());
                t.add(new Transaction(this.name, new Date(), crypto, "Diamonds", TransactionType.WITHDRAWAL, diamondsAsCrypto, amount));
                this.account.setTransactions(t);

                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                diamonds.setAmount(amount);
                this.player.getInventory().addItem(diamonds);

                checkBalance();
                Update();

            } else {
                this.player.sendMessage(String.format("You don't have that much %s coins.", crypto));
            }
        } catch (Exception e) {
            this.player.sendMessage("The payment can not be completed at this time. " + e.getMessage());
        }
    }

    public void deposit(int amount, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            if (coin == null) {
                this.player.sendMessage(String.format("Could not find coin: %s", crypto));
                return;
            }
            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(amount);
            if (this.player.getInventory().contains(Material.DIAMOND, amount)) {
                double amountOfCrypto = amount * CryptoMarket.config.price / coin.getPriceUSD();
                double at = amountOfCrypto;
                if (this.account.getBalance().containsKey(crypto)) {
                    amountOfCrypto += this.account.getBalance().get(crypto);
                }

                Map<String, Double> hm = new HashMap<>(this.account.getBalance());
                hm.put(crypto, amountOfCrypto);
                this.account.setBalance(hm);

                List<Transaction> t = new ArrayList<>(this.account.getTransactions());
                t.add(new Transaction(this.name, new Date(), "Diamonds", crypto, TransactionType.DEPOSIT, at, amount));
                this.account.setTransactions(t);

                this.player.getInventory().removeItem(diamonds);
                checkBalance();
                Update();
            }
            else {
                this.player.sendMessage("You don't have that amount of Diamonds.");
            }
        } catch (Exception e) {
            this.player.sendMessage("The payment can not be completed at this time.");
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
        File file = new File(String.format("./plugins/CryptoMarket/Players/%s.xml", this.name));
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
