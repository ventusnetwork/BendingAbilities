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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<Ability> abilities = new ArrayList<>();
        final List<Ability> bendingAbilitiesList = getPotentialAbilities(player).stream()
                .filter(coreAbility -> BendingAbilities.prices.containsKey(coreAbility)).collect(Collectors.toList());
        for (List<Ability> requiredList : BendingAbilities.abilityTree.keySet()) {
            boolean meetsRequirement = true;
            for (Ability required : requiredList) {
                if (!hasAbilityAccess(player, required)) {
                    meetsRequirement = false;
                }
            }
            if (meetsRequirement) {
                for (Ability reward : BendingAbilities.abilityTree.get(requiredList)) {
                    bendingAbilitiesList.remove(reward);
                    if (!hasAbilityAccess(player, reward)) {
                        abilities.add(reward);
                    }
                }
            }
        }
        bendingAbilitiesList.removeIf(ability -> hasAbilityAccess(player, ability));
        return BAMethods.combineLists(abilities, bendingAbilitiesList);
    }

    @SuppressWarnings("unchecked")
    public List<Ability> getCombinedList(Player player) {
        return BAMethods.combineLists(getAllowedAbilities(player), getBuyableAbilities(player), getUnavailableAbilities(player));
    }

    public void purchase(Player player, Ability ability) {
        // validate if players have enough
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
        List<Ability> abilities = new ArrayList<>();
        List<Ability> buyable = getBuyableAbilities(player);
        List<Ability> allowed = getAllowedAbilities(player);

        List<Ability> potential = getPotentialAbilities(player);
        for (Ability ability : potential) {
            if (BendingAbilities.getInstance().getRequirements(ability) != null && !allowed.contains(ability) && !buyable.contains(ability)) {
                abilities.add(ability);
            }
        }
        potential.removeIf(ability -> BendingAbilities.getInstance().getRequirements(ability) != null);
        for (Ability ability : potential) {
            if (!allowed.contains(ability) && !buyable.contains(ability)) {
                abilities.add(ability);
            }
        }

        return abilities;
    }

    public List<Ability> getPotentialAbilities(Player player) {
        List<Ability> list = CoreAbility.getAbilities().stream().filter(ability -> hasPotential(player, ability)).collect(Collectors.toList());
        Map<String, Ability> map = new HashMap<>();
        for (Ability ability : list) {
            if (!map.containsKey(ability.getName())) {
                map.put(ability.getName(), ability);
            }
        }
        return new ArrayList<>(map.values());
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

    public void setAbilityAccess(Player player, Ability ability, boolean value) {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return;
        Permission perm = rsp.getProvider();
        String node = "bending.ability." + ability.getName();
        if (value) {
            perm.playerAdd(null, player, node);
        } else {
            perm.playerRemove(null, player, node);
        }
    }
}
