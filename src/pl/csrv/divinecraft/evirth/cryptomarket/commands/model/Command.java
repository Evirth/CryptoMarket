package pl.csrv.divinecraft.evirth.cryptomarket.commands.model;

public class Command {
    public String name;
    public String description;
    public String usage;

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
        this.usage = "/cryptomarket " + this.name;
    }

    public Command(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }
}
