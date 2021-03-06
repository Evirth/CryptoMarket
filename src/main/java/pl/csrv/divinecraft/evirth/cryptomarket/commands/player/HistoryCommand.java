package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.helpers.PrintHelper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;

import java.util.Arrays;

public class HistoryCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length > 3) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            OfflinePlayer o = null;
            if (strings.length > 1) {
                o = Arrays.stream(Bukkit.getOfflinePlayers()).filter(f -> f.getName().equalsIgnoreCase(strings[1])).findFirst().orElse(null);
            }
            int page = 1;
            boolean isPageSecondParameter = false;
            if (strings.length == 3) {
                page = Integer.parseInt(strings[2]);
            } else if (strings.length == 2 && o == null) {
                page = Integer.parseInt(strings[1]);
                isPageSecondParameter = true;
            } else if (strings.length == 1) {
                isPageSecondParameter = true;
            }

            if (!(commandSender instanceof HumanEntity) && ((strings.length == 2 && isPageSecondParameter) || strings.length == 1)) {
                commandSender.sendMessage("[CryptoMarket] Only players can use this command.");
                return true;
            }

            boolean extendedPersmission = commandSender.hasPermission(Permissions.CRYPTOMARKET_ADMIN) || commandSender.hasPermission(Permissions.CRYPTOMARKET_HISTORY_OTHERS);
            if (strings.length > 1 && !extendedPersmission && o != null) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            Player p;
            if (!isPageSecondParameter) {
                if (o == null) {
                    commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), strings[1]));
                    return true;
                }
                p = new Player(o.getName());
                commandSender.sendMessage(PrintHelper.getPage(p.checkHistory(), page));
            } else {
                p = new Player(commandSender.getName());
                p.printHistory(page);
            }
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(e.getMessage());
        }
        catch (Exception e) {
            commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CouldNotGetPlayerHistory"), strings[1]));
        }
        return true;
    }
}