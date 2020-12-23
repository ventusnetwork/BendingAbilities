package net.saifs.bendingabilities;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.milkbowl.vault.permission.Permission;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class PlayerManager {
    public static final String PLAYER_DATA_SUBFOLDER = "player-data/";

    public boolean hasAbilityAccess(Player player, Ability ability) {
        return player.hasPermission("bending.ability." + ability.getName());
    }

    public List<Ability> getAllowedAbilities(Player player) {
        List<Ability> abilities = new ArrayList<>();
        for (Ability ability : getPotentialAbilities(player)) {
            if (hasAbilityAccess(player, ability)) {
                abilities.add(ability);
            }
        }
        return abilities;
    }


    public List<Ability> getBuyableAbilities(Player player) {
        List<Ability> list = getPotentialAbilities(player);
        list.removeIf(ability -> BendingAbilities.getPrice(ability) < 0);
        list.removeIf(ability -> hasAbilityAccess(player, ability));
        for (List<Ability> requiredList : BendingAbilities.abilityTree.keySet()) {
            for (Ability required : requiredList) {
                if (!hasAbilityAccess(player, required)) {
                    for (Ability reward : BendingAbilities.abilityTree.get(requiredList)) {
                        list.removeIf(ability -> ability.getName().equals(reward.getName()));
                    }
                }
            }
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<Ability> getCombinedList(Player player) {
        return BAMethods.combineLists(getAllowedAbilities(player), getBuyableAbilities(player), getUnavailableAbilities(player));
    }

    public void purchase(Player player, Ability ability) {
        int price = BendingAbilities.getInstance().getPrice(ability);
        if (player.getLevel() < price) {
            BAMethods.send(player, "&cYou do not have enough experience levels for that!");
            return;
        }
        player.setLevel(player.getLevel() - price);
        setAbilityAccess(player, ability, true);
        BAMethods.send(player, "&aYou bought " + ability.getName() + " for " + price + " experience levels!");
    }

    public List<Ability> getUnavailableAbilities(Player player) {
        List<Ability> list = getPotentialAbilities(player);
        List<Ability> buyable = getBuyableAbilities(player);
        List<Ability> allowed = getAllowedAbilities(player);
        list.removeIf(ability -> buyable.contains(ability) || allowed.contains(ability));
        return list;
    }

    public List<Ability> getPotentialAbilities(Player player) {
        List<Ability> list = CoreAbility.getAbilities().stream().filter(ability -> hasPotential(player, ability))
                .collect(Collectors.toList());
        Map<String, Ability> map = new HashMap<>();
        for (Ability ability : list) {
            if (!map.containsKey(ability.getName())) {
                map.put(ability.getName(), ability);
            }
        }
        return map.values().stream().filter(ability -> BendingAbilities.prices.containsKey(ability.getName())).collect(Collectors.toList());
    }

    private boolean hasPotential(Player player, Ability ability) {
        if (!ability.isEnabled()) return false;
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        if (ability.getElement() instanceof Element.SubElement) {
            return bendingPlayer.hasSubElement((Element.SubElement) ability.getElement());
        }
        return bendingPlayer.hasElement(ability.getElement());
    }

    public void setPermissionNode(Player player, String node, boolean value) {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return;
        Permission perm = rsp.getProvider();
        if (value) {
            perm.playerAdd(null, player, node);
        } else {
            perm.playerRemove(null, player, node);
        }
    }

    public boolean abilityHasSubsets(Ability ability) {
        List<String> list = Arrays.asList("WaterArms", "WaterSpout");
        return list.contains(ability.getName());
    }

    public void setAbilityAccess(Player player, Ability ability, boolean value) {
        String node = "bending.ability." + ability.getName();
        setPermissionNode(player, node, value);
        if (abilityHasSubsets(ability)) {
            setPermissionNode(player, node + ".*", value);
        }
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
    }
}
