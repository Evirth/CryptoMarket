package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.helpers;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PrintHelper {
    public static String[] getPage(String[] messages, int page, int linesOnOnePage) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();
        int maxPages = (int) Math.ceil((double) (messages.length - 1) / linesOnOnePage);
        if (maxPages == 0) {
            maxPages = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > maxPages) {
            throw new IllegalArgumentException(CryptoMarket.resourceManager.getResource("PageNotFound"));
        }

        sb.append(messages[0].replace("(##/##)", String.format("(%d/%d)", page, maxPages))).append("\n");
        List<String> list = new ArrayList<>(Arrays.asList(messages));
        list.remove(0);
        int start = linesOnOnePage * (page - 1);

        for (int i = start; i < list.size() && i < start + linesOnOnePage; i++) {
            sb.append(list.get(i)).append("\n");
        }
        sb.append(ChatColor.translateAlternateColorCodes('&', "&6----------------------------------------"));
        return sb.toString().split("\n");
    }

    public static String[] getPage(String[] messages, int page) {
        return getPage(messages, page, 5);
    }
}
