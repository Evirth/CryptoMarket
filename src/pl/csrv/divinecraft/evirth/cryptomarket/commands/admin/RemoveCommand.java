package pl.csrv.divinecraft.evirth.cryptomarket.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.api.Player;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class RemoveCommand implements ICommand {
    private String permission = "cryptomarket.admin";

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(this.permission)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings.length != 4) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
                return true;
            }

            if (Bukkit.getServer().getPlayer(strings[1]) == null) {
                commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("PlayerNotFound"), strings[1]));
                return true;
            }

            Player p = new Player(strings[1]);
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
