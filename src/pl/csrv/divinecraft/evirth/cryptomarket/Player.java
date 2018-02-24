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
import java.util.*;

public class Player {
    private PlayerAccount account;
    private org.bukkit.entity.Player player;
    private String name;

    public Player(String name) {
        this.name = name;
        this.player = Bukkit.getServer().getPlayer(name);
        this.account = new PlayerAccount(name);
        this.initialize();
    }

    public void withdraw(int amount, String crypto) {
        try {
            if (!this.account.getBalance().containsKey(crypto)) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), crypto));
                return;
            }

            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            int diamondsFromBalance = PlayerAccount.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(crypto).getAmount());
            if (diamondsFromBalance >= amount) {
                double amountOfCrypto = PlayerAccount.calculateAmountOfCryptoFromDiamonds(amount, coin.getPriceUSD());
                this.changeBalance(coin, -amountOfCrypto);
                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                diamonds.setAmount(amount);
                this.player.getInventory().addItem(diamonds);
                this.checkBalance();

                Transaction t = new Transaction(
                        this.name,
                        new Date(),
                        coin.getName(),
                        CryptoMarket.resourceManager.getResource("Diamonds"),
                        TransactionType.WITHDRAWAL,
                        amountOfCrypto,
                        amount,
                        coin.getPriceUSD(),
                        null,
                        null);
                this.account.addTransaction(t);

                this.update();
            } else {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), coin.getName()));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void deposit(int amountOfDiamonds, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }

            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(amountOfDiamonds);

            if (this.player.getInventory().contains(Material.DIAMOND, amountOfDiamonds)) {
                double amountOfCrypto = amountOfDiamonds * CryptoMarket.config.price / coin.getPriceUSD();
                this.changeBalance(coin, amountOfCrypto);
                this.player.getInventory().removeItem(diamonds);
                this.checkBalance();

                Transaction t = new Transaction(
                        this.name,
                        new Date(),
                        CryptoMarket.resourceManager.getResource("Diamonds"),
                        coin.getName(),
                        TransactionType.DEPOSIT,
                        amountOfCrypto,
                        amountOfDiamonds,
                        coin.getPriceUSD(),
                        null,
                        null);
                this.account.addTransaction(t);

                this.update();
            } else {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("DontHaveThatAmountOfDiamonds"));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void exchange(String fromCrypto, double amount, String toCrypto) {

    }

    public void transfer(String crypto, String amount, String toPlayer) {
        try {
            Player p2 = new Player(toPlayer);
            if (p2.player == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), toPlayer));
                return;
            }

            if (p2.name.equals(this.name)) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
                return;
            }

            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }

            if (!this.account.getBalance().containsKey(coin.getName())) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), coin.getName()));
                return;
            }

            int amountOfDiamonds;
            double amountOfCrypto;

            if (amount.endsWith("D") || amount.endsWith("d")) {
                amountOfDiamonds = Math.abs(Integer.parseInt(amount.substring(0, amount.length() - 1))); // Remove 'D' or 'd' char and parse to int
                amountOfCrypto = PlayerAccount.calculateAmountOfCryptoFromDiamonds(amountOfDiamonds, coin.getPriceUSD());
            } else {
                amountOfCrypto = Double.parseDouble(amount);
                amountOfDiamonds = PlayerAccount.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
            }

            double balance = this.account.getBalance().get(coin.getName()).getAmount();
            if (balance < amountOfCrypto) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), coin.getName()));
                return;
            }

            this.changeBalance(coin, -amountOfCrypto);
            this.checkBalance();
            p2.changeBalance(coin, amountOfCrypto);
            p2.checkBalance();
            p2.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("GotTransfer"), this.name, amountOfCrypto, amountOfDiamonds, coin.getName()));

            Transaction t = new Transaction(
                    this.name,
                    new Date(),
                    coin.getName(),
                    coin.getName(),
                    TransactionType.TRANSFER,
                    amountOfCrypto,
                    amountOfDiamonds,
                    coin.getPriceUSD(),
                    this.name,
                    p2.name
            );
            this.account.addTransaction(t);
            p2.account.addTransaction(t);

            this.update();
            p2.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void checkBalance() {
        this.player.sendMessage(this.account.printBalance().split("\n"));
    }

    private void changeBalance(CoinMarket coin, double amountOfCrypto) {
        try {
            if (amountOfCrypto == 0) {
                return;
            }

            double resultBalance = amountOfCrypto;
            boolean containsCoin = this.account.getBalance().containsKey(coin.getName());
            if (containsCoin) {
                resultBalance += this.account.getBalance().get(coin.getName()).getAmount();
            }

            if (resultBalance > 0) {
                Coin c = new Coin(coin.getName(), coin.getSymbol(), resultBalance);
                this.account.putBalance(c);
            } else {
                if (containsCoin) {
                    this.account.removeBalance(coin.getName());
                }
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    private void initialize() {
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

    private void update() {
        try {
            XmlSerializationHelper.XmlSerialize(this.name, this.account);
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getMessage());
        }
    }
}
