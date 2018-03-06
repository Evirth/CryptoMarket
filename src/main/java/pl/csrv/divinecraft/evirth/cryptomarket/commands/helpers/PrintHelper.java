package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.helpers;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import org.bukkit.ChatColor;

public final class PrintHelper {
    public static String[] getPage(String[] messages, int page, int linesOnOnePage) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        int maxPages = (int) Math.ceil((double) messages.length / linesOnOnePage);
        if (maxPages == 0) {
            maxPages = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > maxPages) {
            throw new IllegalArgumentException(CryptoMarket.resourceManager.getResource("PageNotFound"));
        }

        messages[0] = messages[0].replace("(##/##)", String.format("(%d/%d)", page, maxPages));
        sb.append(messages[0]).append("\n");
        int start = linesOnOnePage * (page - 1) == 0
                ? 1
                : linesOnOnePage * (page - 1);

        for (int i = start; i < messages.length && i < start + linesOnOnePage; i++) {
            sb.append(messages[i]).append("\n");
        }
        sb.append(ChatColor.translateAlternateColorCodes('&', "&6----------------------------------------"));
        return sb.toString().split("\n");
    }

    public static String[] getPage(String[] messages, int page) {
        return getPage(messages, page, 7);
    }
}
