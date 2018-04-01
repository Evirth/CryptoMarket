package main.java.pl.csrv.divinecraft.evirth.cryptomarket.resources;

import main.java.pl.csrv.divinecraft.evirth.cryptomarket.CryptoMarket;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                File f = new File(Paths.get(this.resourcePath, file).toString());
                if (!f.exists()) {
                    this.exportResource(file);
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(ResourceManager.class.getResourceAsStream(file)));
                    long resourceFileLines = br.lines().count();
                    long currentFileLines = Files.lines(Paths.get(f.getPath())).count();

                    if (currentFileLines != resourceFileLines) {
                        File fbak = new File(Paths.get(this.resourcePath, file + ".old").toString());
                        if (f.renameTo(fbak)) {
                            this.exportResource(file);
                            this.plugin.getLogger().info(String.format("Resource file '%s' has been updated.", file));
                        }
                    }
                }
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