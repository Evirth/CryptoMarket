package pl.csrv.divinecraft.evirth.cryptomarket.api;

import com.lucadev.coinmarketcap.CoinMarketCap;
import com.lucadev.coinmarketcap.model.CoinMarket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;
import pl.csrv.divinecraft.evirth.cryptomarket.helpers.CoinHelper;
import pl.csrv.divinecraft.evirth.cryptomarket.helpers.XmlSerializationHelper;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Coin;
import pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;
import pl.csrv.divinecraft.evirth.cryptomarket.models.Transaction;
import sun.security.krb5.internal.CredentialsUtil;

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
            int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(crypto).getAmount());
            if (diamondsFromBalance >= amount) {
                double amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amount, coin.getPriceUSD());
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
                        null,
                        null,
                        null);
                this.account.getTransactions().add(t);

                this.update();
            } else {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), coin.getName()));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void withdrawAll() {
        if (this.account.getBalance().isEmpty()) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("EmptyBalance"));
            return;
        }

        int d = 0;
        for (Map.Entry<String, Coin> m : this.account.getBalance().entrySet()) {
            CoinMarket coin = CoinMarketCap.ticker(m.getValue().getId()).get();
            int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), m.getValue().getAmount());
            double amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(diamondsFromBalance, coin.getPriceUSD());
            this.changeBalance(coin, -amountOfCrypto);
            d += diamondsFromBalance;

            Transaction t = new Transaction(
                    this.name,
                    new Date(),
                    coin.getName(),
                    CryptoMarket.resourceManager.getResource("Diamonds"),
                    TransactionType.WITHDRAWAL,
                    amountOfCrypto,
                    diamondsFromBalance,
                    coin.getPriceUSD(),
                    null,
                    null,
                    null,
                    null);
            this.account.getTransactions().add(t);
        }

        ItemStack diamonds = new ItemStack(Material.DIAMOND);
        diamonds.setAmount(d);
        this.player.getInventory().addItem(diamonds);
        this.checkBalance();
        this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("WithdrawAll"), d));

        this.update();
    }

    public void deposit(int amountOfDiamonds, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto).get();
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }

            if (!this.player.getInventory().contains(Material.DIAMOND, amountOfDiamonds)) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("DontHaveThatAmountOfDiamonds"));
                return;
            }

            double amountOfCrypto = amountOfDiamonds * CryptoMarket.config.price / coin.getPriceUSD();
            this.changeBalance(coin, amountOfCrypto);
            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(amountOfDiamonds);
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
                    null,
                    null,
                    null);
            this.account.getTransactions().add(t);

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void exchange(String fromCrypto, double amount, String toCrypto) {
        try {
            if (!this.account.getBalance().containsKey(fromCrypto)) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), fromCrypto));
                return;
            }

            Coin fromC = this.account.getBalance().get(fromCrypto);
            if (fromC.getAmount() < amount) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), fromC.getName()));
                return;
            }

            CoinMarket toCoin = CoinMarketCap.ticker(toCrypto).get();
            if (toCoin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), toCrypto));
                return;
            }

            CoinMarket fromCoin = CoinMarketCap.ticker(fromC.getId()).get();
            int amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(fromCoin.getPriceUSD(), amount);
            this.changeBalance(fromCoin, -amount);
            double amountOfNewCoin = CoinHelper.calculateAmountOfNewCrypto(fromCoin.getPriceUSD(), amount, toCoin.getPriceUSD());
            this.changeBalance(toCoin, amountOfNewCoin);
            this.checkBalance();
            this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("Exchange"), amount, fromCoin.getSymbol(), fromCoin.getPriceUSD() * amount, amountOfDiamonds, amountOfNewCoin, toCoin.getSymbol()));

            Transaction t = new Transaction(
                    this.name,
                    new Date(),
                    fromCoin.getName(),
                    toCoin.getName(),
                    TransactionType.EXCHANGE,
                    amount,
                    amountOfDiamonds,
                    fromCoin.getPriceUSD(),
                    null,
                    null,
                    amountOfNewCoin,
                    toCoin.getPriceUSD());
            this.account.getTransactions().add(t);

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
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
                amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amountOfDiamonds, coin.getPriceUSD());
            } else {
                amountOfCrypto = Double.parseDouble(amount);
                amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
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
                    p2.name,
                    null,
                    null);
            this.account.getTransactions().add(t);
            p2.account.getTransactions().add(t);

            this.update();
            p2.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void checkBalance() {
        this.player.sendMessage(CryptoMarket.resourceManager.getResource("CalculatingThePrices"));
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
                this.account.getBalance().put(c.getName(), c);
            } else {
                if (containsCoin) {
                    this.account.getBalance().remove(coin.getName());
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
