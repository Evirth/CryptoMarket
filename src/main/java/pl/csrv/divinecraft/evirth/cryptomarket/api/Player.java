package main.java.pl.csrv.divinecraft.evirth.cryptomarket.api;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.enums.TransactionType;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.helpers.CoinHelper;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.models.Coin;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.models.PlayerAccount;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.models.Transaction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.CoinMarketCap;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.coinmarketcap.models.CoinMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.helpers.PrintHelper;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.helpers.XmlSerializationHelper;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Player {
    private PlayerAccount account;
    private org.bukkit.entity.Player player;
    private String name;

    /**
     * Creates an instance of a Player.
     *
     * @param name Player's name.
     */
    public Player(String name) {
        this.name = name;
        this.player = Bukkit.getServer().getPlayer(name);
        this.account = new PlayerAccount(name);
        this.initialize();
    }

    /**
     * Withdraws a certain amount of crypto from the player's balance.
     *
     * @param amount Amount of crypto to withdraw (in Diamonds).
     * @param crypto Name of crypto.
     */
    public void withdraw(int amount, String crypto) {
        try {
            CoinMarket coin = CoinMarketCap.ticker(crypto);
            if (coin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
                return;
            }

            if (!this.account.getBalance().containsKey(coin.getName())) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), crypto));
                return;
            }

            int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(coin.getName()).getAmount());
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
                this.account.getTransactions().add(0, t);

                this.update();
            } else {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), coin.getName()));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    /**
     * Withdraws all crypto from the player's balance.
     */
    public void withdrawAll() {
        try {
            if (this.account.getBalance().isEmpty()) {
                this.player.sendMessage(CryptoMarket.resourceManager.getResource("EmptyBalance"));
                return;
            }

            int d = 0;
            Map<String, Coin> b = new TreeMap<>(this.account.getBalance());
            for (Map.Entry<String, Coin> m : b.entrySet()) {
                CoinMarket coin = CoinMarketCap.ticker(m.getValue().getId());
                int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), m.getValue().getAmount());
                if (diamondsFromBalance == 0) {
                    continue;
                }

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
                this.account.getTransactions().add(0, t);
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

    /**
     * Deposits some amount of Diamonds as crypto to the player's balance.
     *
     * @param amountOfDiamonds Amount of Diamonds to deposit.
     * @param crypto           Name of crypto.
     */
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
            this.account.getTransactions().add(0, t);

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    /**
     * Exchanges some amount of crypto to another one.
     *
     * @param fromCrypto Name of old crypto.
     * @param amount     Amount of old crypto.
     * @param toCrypto   Name of new crypto.
     */
    public void exchange(String fromCrypto, String amount, String toCrypto) {
        try {
            CoinMarket fromCoin = CoinMarketCap.ticker(fromCrypto);
            if (fromCoin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), toCrypto));
                return;
            }

            if (!this.account.getBalance().containsKey(fromCoin.getName())) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), fromCoin.getName()));
                return;
            }

            CoinMarket toCoin = CoinMarketCap.ticker(toCrypto);
            if (toCoin == null) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), toCrypto));
                return;
            }


            Coin fromC = this.account.getBalance().get(fromCoin.getName());
            double amountOfCrypto;
            int amountOfDiamonds;

            if (amount.equalsIgnoreCase("all")) {
                amountOfCrypto = this.account.getBalance().get(fromC.getName()).getAmount();
                amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(fromCoin.getPriceUSD(), amountOfCrypto);
            } else if (amount.endsWith("D") || amount.endsWith("d")) {
                amountOfDiamonds = Math.abs(Integer.parseInt(amount.substring(0, amount.length() - 1))); // Remove 'D' or 'd' char and parse to int
                amountOfCrypto = CoinHelper.calculateAmountOfCryptoFromDiamonds(amountOfDiamonds, fromCoin.getPriceUSD());
            } else {
                amountOfCrypto = Double.parseDouble(amount);
                amountOfDiamonds = CoinHelper.calculateAmountOfDiamondsFromCoins(fromCoin.getPriceUSD(), amountOfCrypto);
            }

            if (fromC.getAmount() < amountOfCrypto) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), fromC.getName()));
                return;
            }


            this.changeBalance(fromCoin, -amountOfCrypto);
            double amountOfNewCoin = CoinHelper.calculateAmountOfNewCrypto(fromCoin.getPriceUSD(), amountOfCrypto - (amountOfCrypto * CryptoMarket.config.getFee()), toCoin.getPriceUSD());
            this.changeBalance(toCoin, amountOfNewCoin);
            this.printBalance();
            this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("Exchange"), amountOfCrypto, fromCoin.getSymbol(), fromCoin.getPriceUSD() * amountOfCrypto, amountOfDiamonds, amountOfNewCoin, toCoin.getSymbol()));

            Transaction t = new Transaction(
                    this.name,
                    new Date(),
                    fromCoin.getName(),
                    toCoin.getName(),
                    TransactionType.EXCHANGE,
                    amountOfCrypto,
                    amountOfDiamonds,
                    fromCoin.getPriceUSD(),
                    null,
                    null,
                    amountOfNewCoin,
                    toCoin.getPriceUSD(),
                    CryptoMarket.config.getFee() * 100);
            this.account.getTransactions().add(0, t);

            this.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    /**
     * Transfers some amount of crypto from the first Player to the second one.
     *
     * @param crypto   Name of crypto.
     * @param amount   Amount of crypto.
     * @param toPlayer The second Player's name.
     */
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

            double amountOfNewCoin = amountOfCrypto - (amountOfCrypto * CryptoMarket.config.getFee());
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
                    CryptoMarket.config.getFee() * 100);
            this.account.getTransactions().add(0, t);
            p2.account.getTransactions().add(0, t);

            this.update();
            p2.update();
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    /**
     * Sends a message to the Player which presents his statistics.
     */
    public void printStats() {
        this.player.sendMessage(this.checkStats());
    }

    /**
     * Gets a message which presents Player's statistics.
     *
     * @return The Player's statistics.
     */
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

    /**
     * Gets a message which presents Player's balance.
     *
     * @return The Player's balance.
     */
    public String[] checkBalance() {
        return this.account.printBalance();
    }

    /**
     * Sends a a message to the Player which presents his balance.
     */
    public void printBalance() {
        this.player.sendMessage(CryptoMarket.resourceManager.getResource("CalculatingThePrices"));
        this.player.sendMessage(this.checkBalance());
    }

    /**
     * Gets a message which presents Player's transaction history.
     *
     * @return The Player's transaction history.
     */
    public String[] checkHistory() {
        return this.account.printHistory();
    }

    /**
     * Sends a message to the Player which presents his transaction history.
     *
     * @param page The page of transaction history book.
     */
    public void printHistory(int page) {
        this.player.sendMessage(PrintHelper.getPage(this.checkHistory(), page));
    }

    // region AdminCommands

    /**
     * Adds balance to the Player's account.
     *
     * @param crypto       Name of crypto.
     * @param amount       Amount of crypto (if ends with 'D'/'d' means it's in Diamonds).
     * @param executorName The command executor name.
     * @throws IllegalArgumentException
     */
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

        OfflinePlayer o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f -> f.getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
        if (o != null && o.isOnline()) {
            this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("AdminSentYouCrypto"), amountOfCrypto, coin.getSymbol(), amountOfCrypto * coin.getPriceUSD(), amountOfDiamonds));
        }

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
        this.account.getTransactions().add(0, t);

        this.update();
    }

    /**
     * Removes balance from the Player's account.
     *
     * @param crypto       Name of crypto.
     * @param amount       Amount of crypto (if ends with 'D'/'d' means it's in Diamonds).
     * @param executorName The command executor name.
     * @throws IllegalArgumentException
     */
    public void removeBalance(String crypto, String amount, String executorName) throws IllegalArgumentException {
        CoinMarket coin = CoinMarketCap.ticker(crypto);
        if (coin == null) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("CouldNotFindCoin"), crypto));
        }

        if (!this.account.getBalance().containsKey(coin.getName())) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("PlayerDoesntHaveCoin"), this.name, coin.getName()));
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

        int diamondsFromBalance = CoinHelper.calculateAmountOfDiamondsFromCoins(coin.getPriceUSD(), this.account.getBalance().get(coin.getName()).getAmount());
        if (diamondsFromBalance < amountOfDiamonds) {
            throw new IllegalArgumentException(String.format(CryptoMarket.resourceManager.getResource("PlayerDoesntHaveThatManyCoins"), this.name, coin.getName()));
        }

        this.changeBalance(coin, -amountOfCrypto);

        OfflinePlayer o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f -> f.getName().equalsIgnoreCase(this.name)).findFirst().orElse(null);
        if (o != null && o.isOnline()) {
            this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("AdminTookYourCrypto"), amountOfCrypto, coin.getSymbol(), amountOfCrypto * coin.getPriceUSD(), amountOfDiamonds));
        }

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
        this.account.getTransactions().add(0, t);

        this.update();
    }

    // endregion AdminCommands

    /**
     * Changes the Player's balance.
     *
     * @param coin           The coin.
     * @param amountOfCrypto Amount of crypto.
     * @throws IllegalArgumentException
     */
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
