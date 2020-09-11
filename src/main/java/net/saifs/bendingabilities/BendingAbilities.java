package net.saifs.bendingabilities;

import org.bukkit.plugin.java.JavaPlugin;

public final class BendingAbilities extends JavaPlugin {
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
