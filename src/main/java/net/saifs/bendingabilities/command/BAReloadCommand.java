package net.saifs.bendingabilities.command;

import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BAReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bendingabilities.reload")) {
            BAMethods.send(sender, "&cYou do not have permission to do that! You require the node: &4bendingabilities.reload");
            return true;
        }
        BendingAbilities.getInstance().reload();
        BAMethods.send(sender, "&aSuccessfully reloaded Bending Abilities plugin!");
        return true;
    }
}
