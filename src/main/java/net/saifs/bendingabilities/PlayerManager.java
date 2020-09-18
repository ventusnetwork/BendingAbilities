package net.saifs.bendingabilities;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.milkbowl.vault.permission.Permission;
import net.saifs.bendingabilities.data.BADataFile;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerManager {
    public static final String PLAYER_DATA_SUBFOLDER = "player-data/";

    public boolean hasAbilityAccess(Player player, Ability ability) {
        return player.hasPermission("bending.ability." + ability.getName());
    }

    public List<Ability> getAllowedAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();
        for (Ability ability : CoreAbility.getAbilities()) {
            if (hasAbilityAccess(player, ability)) {
                abilities.add(ability);
            }
        }
        return filter(player, abilities);
    }

    public List<Ability> getBuyableAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();
        for (List<Ability> requiredList : BendingAbilities.abilitiesMap.keySet()) {
            boolean meetsRequirement = true;
            for (Ability required : requiredList) {
                if (!hasAbilityAccess(player, required)) {
                    meetsRequirement = false;
                }
            }
            if (meetsRequirement) {
                for (Ability reward : BendingAbilities.abilitiesMap.get(requiredList)) {
                    if (!hasAbilityAccess(player, reward)) {
                        abilities.add(reward);
                    }
                }
            }
        }
        return filter(player, abilities);
    }

    @SuppressWarnings("unchecked")
    public List<Ability> getCombinedList(Player player) {
        return filter(player, BAMethods.combineLists(getAllowedAbilities(player), getBuyableAbilities(player), getUnavailableAbilities(player)));
    }

    public List<Ability> getUnavailableAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();
        List<Ability> buyable = getBuyableAbilities(player);
        List<Ability> allowed = getAllowedAbilities(player);

        for (Ability ability : CoreAbility.getAbilities()) {
            if (!allowed.contains(ability) && !buyable.contains(ability)) {
                abilities.add(ability);
            }
        }

        return filter(player, abilities);
    }

    public List<Ability> filter(Player player, List<Ability> list) {
        return list.stream().filter(ability -> hasPotential(player, ability)).collect(Collectors.toList());
    }

    private boolean hasPotential(Player player, Ability ability) {
        if (!ability.isEnabled()) return false;
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        if (bendingPlayer.hasElement(ability.getElement())) return true;
        if (ability.getElement() instanceof Element.SubElement) {
            return bendingPlayer.hasSubElement((Element.SubElement) ability.getElement());
        }
        return false;
    }

    public int getAbilityPoints(Player player) {
        BADataFile dataFile = getPlayerDataFile(player);
        return dataFile.getConfig().getInt("ability-points");
    }

    public BADataFile getPlayerDataFile(Player player) {
        String name = PLAYER_DATA_SUBFOLDER + player.getUniqueId().toString();
        BADataFile dataFile = BADataFile.dataFiles.containsKey(name) ? BADataFile.dataFiles.get(name) : new BADataFile(name);
        dataFile.setDefaults("player-data.yml");
        return dataFile;
    }

    public void setAbilityAccess(Player player, Ability ability, boolean value) {
        // TODO: VaultAPI, mutate permission ( "bending.ability." + ability.getName().toLowerCase() ) to `value`
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return;
        Permission perm = rsp.getProvider();
        String node = "bending.ability." + ability.getName();
        if (value) {
            perm.playerAdd(player, node);
        } else {
            perm.playerRemove(player, node);
        }
    }
}
