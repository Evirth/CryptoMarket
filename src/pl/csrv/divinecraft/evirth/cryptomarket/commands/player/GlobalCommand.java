package pl.csrv.divinecraft.evirth.cryptomarket.commands.player;

import org.bukkit.command.CommandSender;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import pl.csrv.divinecraft.evirth.cryptomarket.commands.ICommand;

public class GlobalCommand implements ICommand {
    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
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
