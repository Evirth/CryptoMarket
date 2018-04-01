package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.models;

public class Stats {
    private int depositedDiamonds;
    private int withdrawnDiamonds;
    private int finalStats;
    private String playerName;

    public Stats(String playerName, int depositedDiamonds, int withdrawnDiamonds, int finalStats) {
        this.playerName = playerName;
        this.depositedDiamonds = depositedDiamonds;
        this.withdrawnDiamonds = withdrawnDiamonds;
        this.finalStats = finalStats;
    }

    public int getDepositedDiamonds() {
        return this.depositedDiamonds;
    }

    public int getWithdrawnDiamonds() {
        return this.withdrawnDiamonds;
    }

    public int getFinalStats() {
        return this.finalStats;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}