package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

import java.util.Arrays;

public class StatsCommand implements ICommand {

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

            boolean extendedPersmission = commandSender.hasPermission(Permissions.CRYPTOMARKET_ADMIN) || commandSender.hasPermission(Permissions.CRYPTOMARKET_STATS_OTHERS);
            if (strings.length == 2 && !extendedPersmission) {
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
                commandSender.sendMessage(p.checkStats());
            } else {
                p = new Player(commandSender.getName());
                p.printStats();
            }
        } catch (Exception e) {
            commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotGetPlayerStats"), strings[1]));
        }
        return true;
    }
}
