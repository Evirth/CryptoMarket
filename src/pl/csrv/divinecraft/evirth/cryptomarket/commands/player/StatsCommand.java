package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

import java.util.Arrays;

public class StatsCommand implements ICommand {
    private String permission = "cryptomarket.player";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(this.permission)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (!(commandSender instanceof HumanEntity)&& strings.length != 2) {
                commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
                return true;
            }

            boolean isAdmin = commandSender.hasPermission("cryptomarket.admin");
            if (strings.length == 2 && !isAdmin) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length != 1 && !isAdmin) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
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
