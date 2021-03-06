package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.admin;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

import java.util.Arrays;

public class RemoveCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_ADMIN)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length != 4) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            OfflinePlayer o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f ->  f.getName().equalsIgnoreCase(strings[1])).findFirst().orElse(null);
            if (o == null) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), strings[1]));
                return true;
            }

            Player p = new Player(o.getName());
            p.removeBalance(strings[3], strings[2], commandSender.getName());
            commandSender.sendMessage(p.checkBalance());
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(e.getMessage());
        }
        catch (Exception e) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("PaymentCannotBeCompleted"));
        }
        return true;
    }
}
