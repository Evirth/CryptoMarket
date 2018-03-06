package main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.models;

public class Command {
    private String name;
    private String description;
    private String usage;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        this.usage = "/cm " + this.name;
    }

    public Command(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }
}
