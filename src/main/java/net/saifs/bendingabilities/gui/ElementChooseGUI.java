package net.saifs.bendingabilities.gui;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

// 1 fire
// 3 air
// 5 water
// 7 earth
public class ElementChooseGUI implements Listener {
    private Inventory inventory;

    public ElementChooseGUI() {
        this.inventory = constructInventory();
        BendingAbilities plugin = BendingAbilities.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private Inventory constructInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Choose Element");

        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta == null) return inventory;
        fillerMeta.setDisplayName("");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = getElement(getElementFromSlot(i));
            if (itemStack == null) {
                itemStack = filler;
            }
            inventory.setItem(i, itemStack);
        }
        return inventory;
    }

    private ItemStack getElement(Element element) {
        if (element == Element.FIRE) {
            ItemStack i = new ItemStack(Material.CAMPFIRE);
            ItemMeta meta = i.getItemMeta();
            if (meta == null) return null;
            meta.setDisplayName(BAMethods.colour("&c&lFIRE"));
            i.setItemMeta(meta);
            return i;
        }
        if (element == Element.AIR) {
            ItemStack i = new ItemStack(Material.FEATHER);
            ItemMeta meta = i.getItemMeta();
            if (meta == null) return null;
            meta.setDisplayName(BAMethods.colour("&f&lAIR"));
            i.setItemMeta(meta);
            return i;
        }
        if (element == Element.WATER) {
            ItemStack i = new ItemStack(Material.POTION, 1);
            PotionMeta meta = (PotionMeta) i.getItemMeta();
            if (meta == null) return null;
            meta.setBasePotionData(new PotionData(PotionType.WATER));
            meta.setDisplayName(BAMethods.colour("&b&lWATER"));
            i.setItemMeta(meta);
            return i;
        }
        if (element == Element.EARTH) {
            ItemStack i = new ItemStack(Material.DIRT, 1);
            ItemMeta meta = i.getItemMeta();
            if (meta == null) return null;
            meta.setDisplayName(BAMethods.colour("&6&lEARTH"));
            i.setItemMeta(meta);
            return i;
        }
        return null;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != this.inventory) {
            return;
        }
        click((Player) e.getWhoClicked(), getElementFromSlot(e.getSlot()));
    }

    private Element getElementFromSlot(int slot) {
        switch (slot) {
            case 10:
                return Element.FIRE;
            case 12:
                return Element.AIR;
            case 14:
                return Element.WATER;
            case 16:
                return Element.EARTH;
            default:
                return null;
        }
    }

    private void click(Player player, Element element) {
        if (element == null) return;
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        bendingPlayer.setElement(element);
        player.closeInventory();
        BAMethods.send(player, "&aSet your element to: " + element.getName());
    }
}
