package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Stats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            List<Stats> stats = new ArrayList<>();
            OfflinePlayer[] players = Bukkit.getOfflinePlayers();
            for (OfflinePlayer o : players) {
                Player p = new Player(o.getName());
                Stats s = p.getStats();
                if (s != null) {
                    stats.add(s);
                }
            }

            stats.sort(Comparator.comparing(Stats::getFinalStats));
            Collections.reverse(stats);
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.translateAlternateColorCodes('&', "&6----- CryptoMarket ----- Top Players -----\n"));
            for (int i = 0; i < stats.size() && i < 3; i++) {
                sb.append(ChatColor.translateAlternateColorCodes('&',
                        String.format("#%d. &5%s&f - Stats: &%s%d&f (Withdrawn: &a%d&f : Deposited: &c%d&f)\n",
                                i + 1,
                                stats.get(i).getPlayerName(),
                                stats.get(i).getFinalStats() < 0 ? "c" : "a",
                                stats.get(i).getFinalStats(),
                                stats.get(i).getWithdrawnDiamonds(),
                                stats.get(i).getDepositedDiamonds())));
            }
            sb.append(ChatColor.translateAlternateColorCodes('&', "&6---------------------------------------"));
            commandSender.sendMessage(sb.toString().split("\n"));
        } catch (Exception e) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("CouldNotGetTopList"));
        }
        return true;
    }
}
