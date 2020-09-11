package net.saifs.bendingabilities;

import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.milkbowl.vault.permission.Permission;
import net.saifs.bendingabilities.data.BADataFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
    public static final String PLAYER_DATA_SUBFOLDER = "player-data/";

    public boolean hasAbilityAccess(Player player, Ability ability) {
        return player.isOp() || player.hasPermission("bending.ability." + ability.getName());
    }

    public List<Ability> getAllowedAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();
        for (Ability ability : CoreAbility.getAbilities()) {
            if (hasAbilityAccess(player, ability)) {
                abilities.add(ability);
            }
        }
        return abilities;
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
