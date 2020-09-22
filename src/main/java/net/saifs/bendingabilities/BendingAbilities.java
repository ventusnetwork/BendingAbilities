package net.saifs.bendingabilities;

import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.saifs.bendingabilities.command.AbilityTreeCommand;
import net.saifs.bendingabilities.command.BAReloadCommand;
import net.saifs.bendingabilities.command.ElementChooseCommand;
import net.saifs.bendingabilities.data.BAConfig;
import net.saifs.bendingabilities.gui.AbilitiesGUI;
import net.saifs.bendingabilities.gui.ElementChooseGUI;
import net.saifs.bendingabilities.gui.TransactionGUI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class BendingAbilities extends JavaPlugin {
    // requirements: rewards
    public static Map<List<Ability>, List<Ability>> abilityTree = new HashMap<>();
    public static Map<Ability, Integer> prices = new HashMap<>();

    private static BendingAbilities instance;
    private static PlayerManager playerManager;

    private static AbilitiesGUI abilitiesGUI;
    private TransactionGUI transactionGUI;
    private ElementChooseGUI elementalChooseGUI;

    private int defaultPrice;
    private BAConfig config;

    public static BendingAbilities getInstance() {
        return instance;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static AbilitiesGUI getAbilitiesGUI() {
        return abilitiesGUI;
    }

    public TransactionGUI getTransactionGUI() {
        return transactionGUI;
    }

    public List<Ability> getRequirements(Ability ability) {
        for (List<Ability> key : abilityTree.keySet()) {
            List<Ability> value = abilityTree.get(key);
            if (value.contains(ability)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        playerManager = new PlayerManager();
        abilitiesGUI = new AbilitiesGUI(9);
        transactionGUI = new TransactionGUI();
        elementalChooseGUI = new ElementChooseGUI();

        loadConfig();
        loadAbilityTree();
        loadPrices();
        loadCommands();
    }

    public int getPrice(Ability ability) {
        if (prices.containsKey(ability)) {
            return prices.get(ability);
        }
        return defaultPrice;
    }

    public ElementChooseGUI getElementalChooseGUI() {
        return elementalChooseGUI;
    }

    private void loadConfig() {
        config = new BAConfig("config.yml");
    }

    private void loadCommands() {
        Objects.requireNonNull(getCommand("bareload")).setExecutor(new BAReloadCommand());
        Objects.requireNonNull(getCommand("abilitytree")).setExecutor(new AbilityTreeCommand());
        Objects.requireNonNull(getCommand("elementchoose")).setExecutor(new ElementChooseCommand());
    }

    private void loadPrices() {
        ConfigurationSection section = config.getConfig().getConfigurationSection("prices");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("default")) {
                defaultPrice = section.getInt("default");
                continue;
            }
            Ability ability = CoreAbility.getAbility(key);
            if (ability != null) {
                prices.put(ability, section.getInt(key));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAbilityTree() {
        List<?> list = config.getConfig().getList("ability-tree");
        if (list == null) return;
        for (Object obj : list) {
            if (obj instanceof LinkedHashMap) {
                LinkedHashMap<String, ?> map = (LinkedHashMap<String, ?>) obj;
                if (!map.containsKey("requirements") || !map.containsKey("rewards")) {
                    continue;
                }
                List<Ability> requirements = new ArrayList<>();
                List<Ability> rewards = new ArrayList<>();
                for (String s : (List<String>) map.get("requirements")) {
                    Ability ability = CoreAbility.getAbility(s);
                    if (ability != null) {
                        requirements.add(ability);
                    }
                }
                for (String s : (List<String>) map.get("rewards")) {
                    Ability ability = CoreAbility.getAbility(s);
                    if (ability != null) {
                        rewards.add(ability);
                    }
                }
                abilityTree.put(requirements, rewards);
            }
        }
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

