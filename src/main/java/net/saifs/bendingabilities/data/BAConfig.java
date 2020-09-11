package net.saifs.bendingabilities.data;

import com.google.common.base.Charsets;
import net.saifs.bendingabilities.BendingAbilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BAConfig {
    private static final BendingAbilities PLUGIN = BendingAbilities.getInstance();

    private String name;
    private File file;
    private FileConfiguration config;

    public BAConfig(String name) {
        if (!name.toLowerCase().endsWith(".yml")) {
            name += ".yml";
        }
        this.name = name;
        load();
    }

    private void load() {
        load(PLUGIN.getResource(name));
    }

    private void load(InputStream stream) {
        try {
            file = new File(PLUGIN.getDataFolder() + "/" + name);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(file);
            if (stream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, Charsets.UTF_8)));
                config.options().copyDefaults(true);
            }
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
