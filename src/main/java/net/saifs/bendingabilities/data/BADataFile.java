package net.saifs.bendingabilities.data;

import com.google.common.base.Charsets;
import net.saifs.bendingabilities.BendingAbilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BADataFile {
    private static final BendingAbilities PLUGIN = BendingAbilities.getInstance();
    public static Map<String, BADataFile> dataFiles = new HashMap<>();
    private String name;
    private File file;
    private FileConfiguration config;

    public BADataFile(String name) {
        if (!name.toLowerCase().endsWith(".yml")) {
            name += ".yml";
        }
        this.name = name;
        dataFiles.put(name, this);

        load();
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

    private void load() {
        try {
            file = new File(PLUGIN.getDataFolder() + "/" + name);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(file);
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDefaults(InputStream stream) {
        if (stream != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, Charsets.UTF_8)));
            config.options().copyDefaults(true);
            save();
        }
    }

    public void setDefaults(String templatePath) {
        InputStream in = PLUGIN.getResource(templatePath);
        if (in != null) {
            setDefaults(in);
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
