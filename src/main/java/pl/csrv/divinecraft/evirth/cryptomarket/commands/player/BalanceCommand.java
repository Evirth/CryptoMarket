package main.java.cryptomarket.commands.player;

import main.java.cryptomarket.CryptoMarket;
import main.java.cryptomarket.api.Player;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import main.java.cryptomarket.commands.ICommand;
import main.java.cryptomarket.commands.Permissions;

import java.util.Arrays;

public class BalanceCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length > 2) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            if (!(commandSender instanceof HumanEntity) && strings.length != 2) {
                commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
                return true;
            }

            boolean isAdmin = commandSender.hasPermission(Permissions.CRYPTOMARKET_ADMIN);
            if (strings.length == 2 && !isAdmin) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            Player p;
            if (strings.length == 2) {
                OfflinePlayer o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f -> f.getName().equalsIgnoreCase(strings[1])).findFirst().orElse(null);
                if (o == null) {
                    commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), strings[1]));
                    return true;
                }
                p = new Player(o.getName());
                commandSender.sendMessage(p.checkBalance());
            } else {
                p = new Player(commandSender.getName());
                p.printBalance();
            }
        } catch (Exception e) {
            commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotGetPlayerStats"), strings[1]));
        }
        return true;
    }
}
