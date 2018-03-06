package main.java.cryptomarket.commands.player;

import main.java.cryptomarket.CryptoMarket;
import org.bukkit.command.CommandSender;
import main.java.cryptomarket.commands.ICommand;
import main.java.cryptomarket.commands.Permissions;

public class GlobalCommand implements ICommand {

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        if (!commandSender.hasPermission(Permissions.CRYPTOMARKET_PLAYER)) {
            commandSender.sendMessage(CryptoMarket.resourceManager.getResource("MissingPermission"));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        try {
            sb.append("TODO: Market cap etc.");
        } catch (Exception e) {
            sb.append(CryptoMarket.resourceManager.getResource("CouldNotGetInfoAboutCrypto"));
        }
        commandSender.sendMessage(sb.toString().split("\n"));
        return true;
    }
}
