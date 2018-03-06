package main.java.cryptomarket.api;

import main.java.cryptomarket.enums.TransactionType;
import main.java.cryptomarket.helpers.CoinHelper;
import main.java.cryptomarket.models.Coin;
import main.java.cryptomarket.models.PlayerAccount;
import main.java.cryptomarket.models.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import main.java.cryptomarket.CryptoMarket;
import main.java.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.cryptomarket.coinmarketcap.models.CoinMarket;
import main.java.cryptomarket.commands.helpers.PrintHelper;
import main.java.cryptomarket.helpers.XmlSerializationHelper;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

            CoinMarket coin = CoinMarketCap.ticker(crypto);
            int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(crypto).getAmount());
            if (diamondsFromBalance >= amount) {
                double amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amount, coin.getPriceUSD());
                this.changeBalance(coin, -amountOfCrypto);
                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                diamonds.setAmount(amount);
                this.player.getInventory().addItem(diamonds);
                this.printBalance();

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
        try {
            if (this.account.getBalance().isEmpty()) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("EmptyBalance"));
                return;
            }

            int d = 0;
            for (Map.Entry<String, Coin> m : this.account.getBalance().entrySet()) {
                CoinMarket coin = CoinMarketCap.ticker(m.getValue().getId());
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
                        null,
                        null);
                this.account.getTransactions().add(t);
            }

            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(d);
            this.player.getInventory().addItem(diamonds);
            this.printBalance();
            this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("WithdrawAll"), d));

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void deposit(int amountOfDiamonds, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto);
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }

            if (!this.player.getInventory().contains(Material.DIAMOND, amountOfDiamonds)) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("DontHaveThatAmountOfDiamonds"));
                return;
            }

            double amountOfCrypto = amountOfDiamonds * CryptoMarket.config.getPrice() / coin.getPriceUSD();
            this.changeBalance(coin, amountOfCrypto);
            ItemStack diamonds = new ItemStack(Material.DIAMOND);
            diamonds.setAmount(amountOfDiamonds);
            this.player.getInventory().removeItem(diamonds);
            this.printBalance();

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
            if (amount == Double.POSITIVE_INFINITY) {
                amount = fromC.getAmount();
            }

            if (fromC.getAmount() < amount) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), fromC.getName()));
                return;
            }

            CoinMarket toCoin = CoinMarketCap.ticker(toCrypto);
            if (toCoin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), toCrypto));
                return;
            }

            CoinMarket fromCoin = CoinMarketCap.ticker(fromC.getId());
            int amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(fromCoin.getPriceUSD(), amount);
            this.changeBalance(fromCoin, -amount);
            double amountOfNewCoin = CoinHelper.calculateAmountOfNewCrypto(fromCoin.getPriceUSD(), amount - (amount * CryptoMarket.config.getTax()), toCoin.getPriceUSD());
            this.changeBalance(toCoin, amountOfNewCoin);
            this.printBalance();
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
                    toCoin.getPriceUSD(),
                    CryptoMarket.config.getTax() * 100);
            this.account.getTransactions().add(t);

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void transfer(String crypto, String amount, String toPlayer) {
        try {
            OfflinePlayer o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f -> f.getName().equalsIgnoreCase(toPlayer)).findFirst().orElse(null);
            if (o == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), toPlayer));
                return;
            }

            Player p2 = new Player(o.getName());
            if (p2.name.equals(this.name)) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotCompleteThisTransaction"));
                return;
            }

            CoinMarket coin = CoinMarketCap.ticker(crypto);
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

            if (amount.equalsIgnoreCase("all")) {
                amountOfCrypto = this.account.getBalance().get(coin.getName()).getAmount();
                amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
            } else if (amount.endsWith("D") || amount.endsWith("d")) {
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
            this.printBalance();

            double amountOfNewCoin = amountOfCrypto - (amountOfCrypto * CryptoMarket.config.getTax());
            int amountOfNewDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfNewCoin);
            p2.changeBalance(coin, amountOfNewCoin);
            if (o.isOnline()) {
                p2.printBalance();
                p2.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("GotTransfer"), this.name, amountOfNewCoin, coin.getSymbol(), coin.getPriceUSD() * amountOfNewCoin, amountOfNewDiamonds));
            }

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
                    amountOfNewCoin,
                    coin.getPriceUSD(),
                    CryptoMarket.config.getTax() * 100);
            this.account.getTransactions().add(t);
            p2.account.getTransactions().add(t);

            this.update();
            p2.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void printStats() {
        this.player.sendMessage(this.checkStats());
    }

    public String[] checkStats() {
        if (this.account.getBalance().isEmpty()) {
            return CryptoMarket.resourceManager.getResource("EmptyBalance").split("\n");
        }

        List<Transaction> withdrawals = this.account.getTransactions().stream().filter(f -> f.getType() == TransactionType.WITHDRAWAL).collect(Collectors.toList());
        List<Transaction> deposits = this.account.getTransactions().stream().filter(f -> f.getType() == TransactionType.DEPOSIT).collect(Collectors.toList());

        int wDiamonds = 0;
        int dDiamonds = 0;
        for (Transaction t : deposits) {
            dDiamonds += t.getAmountOfDiamonds();
        }
        for (Transaction t : withdrawals) {
            wDiamonds += t.getAmountOfDiamonds();
        }

        String s = ChatColor.translateAlternateColorCodes('&', "&a%d&f ");
        int result = wDiamonds - dDiamonds;
        if (result < 0) {
            s = ChatColor.translateAlternateColorCodes('&', "&c%d&f ");
        }
        return String.format(CryptoMarket.resourceManager.getResource("Stats") + s + CryptoMarket.resourceManager.getResource("DiamondOrS"), dDiamonds, wDiamonds, result).split("\n");
    }

    public String[] checkBalance() {
        return this.account.printBalance();
    }

    public void printBalance() {
        this.player.sendMessage(CryptoMarket.resourceManager.getResource("CalculatingThePrices"));
        this.player.sendMessage(this.checkBalance());
    }

    public String[] checkHistory() {
        return this.account.printHistory();
    }

    public void printHistory(int page) {
        this.player.sendMessage(PrintHelper.getPage(this.checkHistory(), page));
    }

    // region AdminCommands

    public void addBalance(String crypto, String amount, String executorName) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto);
        if (coin == null) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
        }

        int amountOfDiamonds;
        double amountOfCrypto;

        if (amount.endsWith("D") || amount.endsWith("d")) {
            amountOfDiamonds = Math.abs(Integer.parseInt(amount.substring(0, amount.length() - 1))); // Remove 'D' or 'd' char and parse to int
            amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amountOfDiamonds, coin.getPriceUSD());
        } else {
            amountOfCrypto = Double.parseDouble(amount.replace(",", "."));
            amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
        }

        this.changeBalance(coin, amountOfCrypto);
        this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("AdminSentYouCrypto"), amountOfCrypto, coin.getSymbol(), amountOfCrypto * coin.getPriceUSD(), amountOfDiamonds));

        Transaction t = new Transaction(
                executorName,
                new Date(),
                null,
                coin.getName(),
                TransactionType.ADMIN_ADD,
                amountOfCrypto,
                amountOfDiamonds,
                coin.getPriceUSD(),
                null,
                null,
                null,
                null,
                null);
        this.account.getTransactions().add(t);

        this.update();
    }

    public void removeBalance(String crypto, String amount, String executorName) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto);
        if (coin == null) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
        }

        if (!this.account.getBalance().containsKey(coin.getName())) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("PlayerDoesntHaveCoin"), this.name, crypto));
        }

        int amountOfDiamonds;
        double amountOfCrypto;

        if (amount.equalsIgnoreCase("all")) {
            amountOfCrypto = this.account.getBalance().get(coin.getName()).getAmount();
            amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
        } else if (amount.endsWith("D") || amount.endsWith("d")) {
            amountOfDiamonds = Math.abs(Integer.parseInt(amount.substring(0, amount.length() - 1))); // Remove 'D' or 'd' char and parse to int
            amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amountOfDiamonds, coin.getPriceUSD());
        } else {
            amountOfCrypto = Double.parseDouble(amount.replace(",", "."));
            amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), amountOfCrypto);
        }

        int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(crypto).getAmount());
        if (diamondsFromBalance < amountOfDiamonds) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("PlayerDoesntHaveThatManyCoins"), this.name, coin.getName()));
        }

        this.changeBalance(coin, -amountOfCrypto);
        this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("AdminTookYourCrypto"), amountOfCrypto, coin.getSymbol(), amountOfCrypto * coin.getPriceUSD(), amountOfDiamonds));

        Transaction t = new Transaction(
                executorName,
                new Date(),
                coin.getName(),
                null,
                TransactionType.ADMIN_REMOVE,
                amountOfCrypto,
                amountOfDiamonds,
                coin.getPriceUSD(),
                null,
                null,
                null,
                null,
                null);
        this.account.getTransactions().add(t);

        this.update();
    }

    // endregion AdminCommands

    private void changeBalance(CoinMarket coin, double amountOfCrypto) throws IllegalArgumentException {
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
            throw new IllegalArgumentException(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
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
