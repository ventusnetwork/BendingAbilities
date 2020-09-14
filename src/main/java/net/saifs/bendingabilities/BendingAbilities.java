package net.saifs.bendingabilities;

import com.projectkorra.projectkorra.ability.Ability;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BendingAbilities extends JavaPlugin {
    // requirements: rewards
    public static Map<List<Ability>, List<Ability>> abilitiesMap = new HashMap<>();
    public static Map<Ability, Integer> prices = new HashMap<>();

    private static BendingAbilities instance;
    private static PlayerManager playerManager;

    public static BendingAbilities getInstance() {
        return instance;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        playerManager = new PlayerManager();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        playerManager = null;
    }

    public void reload() {
        this.getServer().getPluginManager().disablePlugin(this);
        this.getServer().getPluginManager().enablePlugin(this);
    }
}
