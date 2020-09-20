package net.saifs.bendingabilities.command;

import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityTreeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bendingabilities.abilitytree")) {
            BAMethods.send(sender, "&cYou do not have permission to do that!");
            return true;
        }
        if(!(sender instanceof Player)) {
            BAMethods.send(sender, "&cYou must be a player to do that!");
            return true;
        }
        BAMethods.send(sender, "&7Opening...");
        BendingAbilities.getAbilitiesGUI().open((Player) sender);
        return true;
    }
}
