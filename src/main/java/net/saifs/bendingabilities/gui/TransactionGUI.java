package net.saifs.bendingabilities.gui;

import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// TODO: construct middle bit of inventory
public class TransactionGUI implements Listener {
    private static final String PREFIX = "Ability Purchase - ";

    private final List<Integer> confirmSlots;
    private final List<Integer> cancelSlots;

    public TransactionGUI() {
        BendingAbilities.getInstance().getServer().getPluginManager().registerEvents(this, BendingAbilities.getInstance());
        confirmSlots = getConfirmSlots();
        cancelSlots = getCancelSlots();
    }

    public static List<Integer> getConfirmSlots() {
        List<Integer> list = new LinkedList<>();
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(15);
        list.add(16);
        list.add(17);
        list.add(24);
        list.add(25);
        list.add(26);
        return list;
    }

    public static List<Integer> getCancelSlots() {
        List<Integer> list = new LinkedList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(9);
        list.add(10);
        list.add(11);
        list.add(18);
        list.add(19);
        list.add(20);
        return list;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().startsWith(PREFIX) || e.getClickedInventory() == null) return;
        Ability ability = CoreAbility.getAbility(e.getView().getTitle().substring(PREFIX.length()));
        if (ability != null && Arrays.equals(e.getClickedInventory().getContents(), construct(ability).getContents())) {
            e.setCancelled(true);
            if (confirmSlots.contains(e.getSlot())) {
                BendingAbilities.getPlayerManager().purchase((Player) e.getWhoClicked(), ability);
                e.getWhoClicked().closeInventory();
            } else if (cancelSlots.contains(e.getSlot())) {
                e.getWhoClicked().closeInventory();
            }
        }
    }

    // 3 5 21 23
    public Inventory construct(Ability ability) {
        Inventory inv = Bukkit.createInventory(null, 27, PREFIX + ability.getName());

        ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
        ItemMeta cancelMeta = cancel.getItemMeta();
        if (cancelMeta == null) return inv;
        cancelMeta.setDisplayName(BAMethods.colour("&c&lCANCEL"));
        cancel.setItemMeta(cancelMeta);

        ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta == null) return inv;
        confirmMeta.setDisplayName(BAMethods.colour("&a&lCONFIRM"));
        confirm.setItemMeta(confirmMeta);

        for (int i : cancelSlots) {
            inv.setItem(i, cancel);
        }
        for (int i : confirmSlots) {
            inv.setItem(i, confirm);
        }

        ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta blackGlassMeta = blackGlass.getItemMeta();
        if (blackGlassMeta == null) return inv;
        blackGlassMeta.setDisplayName("");
        blackGlass.setItemMeta(blackGlassMeta);

        ItemStack grayGlass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta grayGlassMeta = grayGlass.getItemMeta();
        if (grayGlassMeta == null) return inv;
        grayGlassMeta.setDisplayName("");
        grayGlass.setItemMeta(blackGlassMeta);

        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        ItemMeta whiteGlassMeta = whiteGlass.getItemMeta();
        if (whiteGlassMeta == null) return inv;
        whiteGlassMeta.setDisplayName("");
        whiteGlass.setItemMeta(whiteGlassMeta);

        ItemStack abilityStar = new ItemStack(Material.NETHER_STAR, 1);
        ItemMeta abilityStarMeta = abilityStar.getItemMeta();
        if (abilityStarMeta == null) return inv;
        abilityStarMeta.setDisplayName(BAMethods.colour("&c" + ability.getName()));
        abilityStarMeta.setLore(Collections.singletonList(BAMethods.colour("&c" + BendingAbilities.getInstance().getPrice(ability) + " experience levels.")));
        abilityStar.setItemMeta(abilityStarMeta);

        inv.setItem(3, blackGlass);
        inv.setItem(5, blackGlass);
        inv.setItem(21, blackGlass);
        inv.setItem(23, blackGlass);

        inv.setItem(12, grayGlass);
        inv.setItem(14, grayGlass);

        inv.setItem(4, whiteGlass);
        inv.setItem(22, whiteGlass);

        inv.setItem(13, abilityStar);

        return inv;
    }

}
