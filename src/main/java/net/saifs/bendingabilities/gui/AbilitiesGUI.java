package net.saifs.bendingabilities.gui;

import com.projectkorra.projectkorra.ability.Ability;
import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.PlayerManager;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilitiesGUI implements Listener {
    private final int size;
    private final Map<String, Integer> pages;

    public AbilitiesGUI(int size) {
        this.size = size;
        pages = new HashMap<>();
        // TODO: register listener
        BendingAbilities.getInstance().getServer().getPluginManager()
                .registerEvents(this, BendingAbilities.getInstance());
    }

    // TODO: delete later this is a test
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().isSneaking()) {
            open(e.getPlayer());
        }
    }

    public void open(Player player) {
        Inventory inv = construct(player, 0);
        player.openInventory(inv);
    }

    // REMINDER: must index page from 0
    public Inventory construct(Player player, int page) {
        String title = "Abilities GUI - Page (" + (page + 1) + "/" + getMaxNumberOfPages(player) + ")";
        Inventory inventory = Bukkit.createInventory(null, size, title);
        PlayerManager pm = BendingAbilities.getPlayerManager();
        List<Ability> combined = pm.getCombinedList(player);
        if (page != 0)
            inventory.setItem(size - 9, getPageArrow(false));
        if (page != getMaxNumberOfPages(player) - 1)
            inventory.setItem(size - 1, getPageArrow(true));
        for (int slot = 0; slot < size; slot++) {
            if (slot == size - 9 && page != 0) {
                continue;
            }
            if (page != getMaxNumberOfPages(player) - 1 && slot == size - 1) {
                continue;
            }
            Ability ability = calculateAbility(combined, page, slot);
            if (ability == null) break;
            ItemStack itemStack = getItemStackFromAbility(player, ability);
            inventory.setItem(slot, itemStack);
        }
        pages.put(title, page);
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (!pages.containsKey(title)) {
            return;
        }
        int page = pages.get(title);
        int slot = e.getSlot();
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        if (slot == size - 9 && page != 0) {
            // back arrow
            player.openInventory(construct(player, page - 1));
            return;
        }
        if (slot == size - 1 && page != getMaxNumberOfPages(player) - 1) {
            // forward arrow
            player.openInventory(construct(player, page + 1));
            return;
        }
        PlayerManager pm = BendingAbilities.getPlayerManager();
        List<Ability> combined = pm.getCombinedList(player);
        Ability ability = calculateAbility(combined, page, slot);
        if (ability == null) return;
        BAMethods.send(player, "&cYou clicked: &a" + ability.getName());
        int status = getAbilityStatus(player, ability);
        if (status == 1) {
            player.sendMessage("uh you may want to buy " + ability.getName());
            player.closeInventory();
        }
    }

    private Ability calculateAbility(List<Ability> combined, int page, int slot) {
        int offset = 2 * page;
        if (slot < size - 9 && page != 0) {
            offset++;
        }
        int i = ((page * size) + slot) - offset;
        if (i >= combined.size() || i < 0) return null;
        return combined.get(i);
    }

    /*
    0 = allowed
    1 = buyable
    2 = hidden
     */
    // TODO: all these surrogate functions lmao kill me haha
    public int getAbilityStatus(Player player, Ability ability) {
        PlayerManager pm = BendingAbilities.getPlayerManager();
        List<Ability> allowed = pm.getAllowedAbilities(player);
        List<Ability> buyable = pm.getBuyableAbilities(player);
        if (allowed.contains(ability)) return 0;
        if (buyable.contains(ability)) return 1;
        return 2;
    }

    public ItemStack getItemStackFromAbility(Player player, Ability ability) {
        int status = getAbilityStatus(player, ability);
        ItemStack itemStack = new ItemStack(Material.GRAY_WOOL, 1);
        if (status == 0) {
            itemStack = new ItemStack(Material.GREEN_WOOL, 1);
        }
        if (status == 1) {
            itemStack = new ItemStack(Material.RED_WOOL, 1);
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        meta.setDisplayName(BAMethods.colour("&c" + ability.getName()));
        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public ItemStack getPageArrow(boolean forward) {
        ItemStack i = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return i;
        meta.setDisplayName(BAMethods.colour(forward ? "&a&lFORWARD" : "&c&lBACK"));
        i.setItemMeta(meta);
        return i;
    }

    public int getMaxNumberOfPages(Player player) {
        int size = BendingAbilities.getPlayerManager().getCombinedList(player).size();
        int pages = (int) Math.ceil(size / (double) this.size);
        size += 2 * (pages - 1);
        return (int) Math.ceil(size / (double) this.size);
    }

}
