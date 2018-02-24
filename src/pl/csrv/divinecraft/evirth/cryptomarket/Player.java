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

    private void changeBalance(CoinMarket coin, double amount) {
        try {
            if (amount == 0) {
                return;
            }

            double resultBalance = amount;
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

    public void withdraw(int amount, String crypto, Transaction customTransaction) {
        try {
            if (!this.account.getBalance().containsKey(crypto)) {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveCoin"), crypto));
                return;
            }

            Coin coin = this.account.getBalance().get(crypto);
            int diamondsFromBalance = PlayerAccount.calculateDiamondsAmountFromCoins(coin.getName(), coin.getAmount());
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

                if (customTransaction != null) {
                    customTransaction = new Transaction(
                            customTransaction.getPlayerName() != null ? customTransaction.getPlayerName() : this.name,
                            customTransaction.getTransactionDate() != null ? customTransaction.getTransactionDate() : new Date(),
                            customTransaction.getToCurrency() != null ? customTransaction.getToCurrency() : coin.getName(),
                            customTransaction.getFromCurrency() != null ? customTransaction.getFromCurrency() : CryptoMarket.resourceManager.getResource("Diamonds"),
                            customTransaction.getType() != null ? customTransaction.getType() : TransactionType.WITHDRAWAL,
                            customTransaction.getAmountOfCrypto() != 0.0 ? customTransaction.getAmountOfCrypto() : diamondsAsCrypto,
                            customTransaction.getAmountOfDiamonds() != 0 ? customTransaction.getAmountOfDiamonds() : amount,
                            customTransaction.getCryptoPriceInUSD() != 0.0 ? customTransaction.getCryptoPriceInUSD() : coinPrice,
                            customTransaction.getFromPlayer(),
                            customTransaction.getToPlayer());
                } else {
                    customTransaction = new Transaction(
                            this.name,
                            new Date(),
                            coin.getName(),
                            CryptoMarket.resourceManager.getResource("Diamonds"),
                            TransactionType.WITHDRAWAL,
                            diamondsAsCrypto,
                            amount,
                            coinPrice,
                            null,
                            null);
                }
                addTransaction(customTransaction);

                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                diamonds.setAmount(amount);
                this.player.getInventory().addItem(diamonds);

                checkBalance();
                update();

            } else {
                this.player.sendMessage(String.format(CryptoMarket.resourceManager.getResource("DontHaveThatManyCoins"), coin.getName()));
            }
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    private void addBalance(Coin coin, double amount) {
        try {
            Coin c;
            Map<String, Coin> hm = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            hm.putAll(this.account.getBalance());
            if (hm.containsKey(coin.getName())) {
                c = this.account.getBalance().get(coin.getName());
                c.setAmount(amount);
            } else {
                c = new Coin(coin.getName(), coin.getSymbol(), amount);
            }
            hm.put(coin.getName(), c);
            this.account.setBalance(hm);
        } catch (Exception e) {
            this.player.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
    }

    public void deposit(int amount, String crypto, Transaction customTransaction) {
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

                if (customTransaction != null) {
                    customTransaction = new Transaction(
                            customTransaction.getPlayerName() != null ? customTransaction.getPlayerName() : this.name,
                            customTransaction.getTransactionDate() != null ? customTransaction.getTransactionDate() : new Date(),
                            customTransaction.getFromCurrency() != null ? customTransaction.getFromCurrency() : CryptoMarket.resourceManager.getResource("Diamonds"),
                            customTransaction.getToCurrency() != null ? customTransaction.getToCurrency() : coin.getName(),
                            customTransaction.getType() != null ? customTransaction.getType() : TransactionType.DEPOSIT,
                            customTransaction.getAmountOfCrypto() != 0.0 ? customTransaction.getAmountOfCrypto() : at,
                            customTransaction.getAmountOfDiamonds() != 0 ? customTransaction.getAmountOfDiamonds() : amount,
                            customTransaction.getCryptoPriceInUSD() != 0.0 ? customTransaction.getCryptoPriceInUSD() : coin.getPriceUSD(),
                            customTransaction.getFromPlayer(),
                            customTransaction.getToPlayer());
                } else {
                    customTransaction = new Transaction(
                            this.name,
                            new Date(),
                            CryptoMarket.resourceManager.getResource("Diamonds"),
                            coin.getName(),
                            TransactionType.DEPOSIT,
                            at,
                            amount,
                            coin.getPriceUSD(),
                            null,
                            null);
                }
                addTransaction(customTransaction);

                this.player.getInventory().removeItem(diamonds);
                checkBalance();
                update();
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
                amountOfCrypto = PlayerAccount.calculateCryptosAmountFromDiamonds(coin.getName(), amountOfDiamonds);
            } else {
                amountOfCrypto = Double.parseDouble(amount);
                amountOfDiamonds = PlayerAccount.calculateDiamondsAmountFromCoins(coin.getName(), amountOfCrypto);
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

    private void addTransaction(Transaction transaction) {
        List<Transaction> t = new ArrayList<>(this.account.getTransactions());
        t.add(transaction);
        this.account.setTransactions(t);
    }

    private void removeBalance() {

    }
}
