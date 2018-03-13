package main.java.pl.csrv.divinecraft.evirth.cryptomarket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.commands.CommandExecutorImpl;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.models.Config;
import main.java.pl.csrv.divinecraft.evirth.cryptomarket.resources.ResourceManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

public class CryptoMarket extends JavaPlugin {
    public static Config config;
    public static ResourceManager resourceManager;
    public static String pluginDir;

    @Override
    public void onEnable() {
        this.checkVersion();
        pluginDir = Paths.get("plugins", CryptoMarket.class.getSimpleName()).toString();
        this.createFolders();
        this.initializeConfig();
        resourceManager = new ResourceManager(this);
        this.getCommand("cryptomarket").setExecutor(new CommandExecutorImpl());
        this.getLogger().info("CryptoMarket enabled.");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("CryptoMarket disabled.");
    }

    private void initializeConfig() {
        this.saveDefaultConfig();
        FileConfiguration fc = this.getConfig();

        config = new Config();
        config.setPrice(fc.getInt("price"));
        config.setLang(fc.getString("lang"));
        config.setFee(fc.getDouble("fee") / 100);
    }

    private void createFolders() {
        File data = new File(Paths.get(pluginDir, "Players").toString());
        if (!data.exists()) {
            data.mkdirs();
        }

        File resources = new File(Paths.get(pluginDir, "Resources").toString());
        if (!resources.exists()) {
            resources.mkdirs();
        }
    }

    private void checkVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/evirth/cryptomarket/releases/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            br.lines().forEach(sb::append);
            GitHubRepoVersion g = new ObjectMapper().readValue(sb.toString(), new TypeReference<GitHubRepoVersion>() {});
            String version = this.getDescription().getVersion();
            if (!g.getVersion().equals(version)) {
                this.getLogger().info(String.format("New version (%s) is available: https://github.com/Evirth/CryptoMarket/releases", g.getVersion()));
            }
        } catch (Exception e) {
            this.getLogger().warning(e.getMessage());
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubRepoVersion {
    @JsonProperty("tag_name")
    private String version;

    public String getVersion() {
        if (this.version.startsWith("v")) {
            return this.version.substring(1);
        }
        return this.version;
    }
}
