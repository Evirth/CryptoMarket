package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutorImpl;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.helpers.PrintHelper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.Permissions;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.models.Command;

public class HelpCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        try {
            if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
                commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
                return true;
            }

            if (strings == null) {
                return false;
            }

            int page = 1;
            boolean isSecondParameterPageNumber;
            if (strings.length > 1) {
                isSecondParameterPageNumber = strings[1].startsWith("-")
                        ? StringUtils.isNumeric(strings[1].substring(1))
                        : StringUtils.isNumeric(strings[1]);

                page = isSecondParameterPageNumber
                        ? Integer.parseInt(strings[1])
                        : 1;
            } else {
                isSecondParameterPageNumber = true;
            }

            StringBuilder sb = new StringBuilder();

            if (isSecondParameterPageNumber) {
                String helpMsg = String.format(CryptoMarket.resourceManager.getResource("HelpHeader") + "\n", CryptoMarket.config.getPrice(), CryptoMarket.config.getTax() * 100);
                sb.append(ChatColor.translateAlternateColorCodes('&', "&6----- CryptoMarket ----- Help (##/##) -----\n"));
                sb.append(helpMsg);
                for (Command c : CommandExecutorImpl.availableCommands) {
                    sb.append(String.format("/cm %s - %s\n", c.getName(), c.getDescription()));
                }
                commandSender.sendMessage(PrintHelper.getPage(sb.toString().split("\n"), page));
            } else {
                boolean found = false;
                for (Command c : CommandExecutorImpl.availableCommands) {
                    if (strings[1].equals(c.getName())) {
                        sb.append(String.format("%s\n%s: %s\n", c.getDescription(), CryptoMarket.resourceManager.getResource("Usage"), c.getUsage()));
                        found = true;
                        break;
                    }
                }
                if (found) {
                    commandSender.sendMessage(sb.toString().split("\n"));
                } else {
                    commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("CommandDoesNotExists"), strings[1]));
                }
            }
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(e.getMessage());
        } catch (Exception e) {
            commandSender.sendMessage(String.format(CryptoMarket.resourceManager.getResource("IncorrectUseOfCommand"), strings[0]));
        }
        return true;
    }
}
