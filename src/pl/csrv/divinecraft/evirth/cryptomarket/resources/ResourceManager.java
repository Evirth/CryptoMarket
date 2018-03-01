package pl.csrv.divinecraft.evirth.cryptomarket.resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;

public class ResourceManager {
    private Plugin plugin;
    private File resourceFile;
    private FileConfiguration resource;
    private final String resourcePath;

    public ResourceManager(Plugin plugin) {
        this.plugin = plugin;
        String p = Paths.get(CryptoMarket.pluginDir, "Resources", String.format("messages_%s.yml", CryptoMarket.config.getLang())).toString();
        List<String> lang = new ArrayList<>(Arrays.asList("en"));   // Maybe for more languages support in the future
        if (!lang.contains(CryptoMarket.config.getLang()) && !new File(p).exists()) {
            this.plugin.getLogger().warning(String.format("Cannot find 'messages_%s.yml'. Loading 'messages_en.yml'", CryptoMarket.config.getLang()));
            CryptoMarket.config.setLang("en");
        }
        String selectedFile = String.format("messages_%s.yml", CryptoMarket.config.getLang());
        this.resourcePath = Paths.get(CryptoMarket.pluginDir, "Resources").toString();
        this.resourceFile = new File(Paths.get(this.resourcePath, selectedFile).toString());
        try {
            for (String s : lang) {
                String file = String.format("messages_%s.yml", s);
                if (!new File(Paths.get(this.resourcePath, file).toString()).exists())
                    this.exportResource(file);
            }
        } catch (Exception e) {
            this.plugin.getLogger().warning("Cannot generate default resource messages.");
        }
        this.reload();
        this.saveDefault();
    }

    public void reload() {
        this.resource = YamlConfiguration.loadConfiguration(this.resourceFile);

        File f = new File(this.resourceFile.getPath());
        if (f.exists()) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(f);
            this.resource.setDefaults(defaultConfig);
        }
    }

    public void saveDefault() {
        if (!this.resourceFile.exists()) {
            this.plugin.saveResource(this.resourceFile.getPath(), false);
        }
    }

    public String getResource(String name) {
        return this.getResource(name, true);
    }

    public String getResource(String name, boolean color) {
        String resource = this.resource.getString(name);
        if (resource == null) {
            this.plugin.getLogger().warning("Missing resource: " + name);
            resource = "[missing resource]";
        }

        if (color) {
            resource = ChatColor.translateAlternateColorCodes('&', resource);
        }
        return resource;
    }

    public void exportResource(String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = ResourceManager.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(Paths.get(this.resourcePath, resourceName).toString());
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
}