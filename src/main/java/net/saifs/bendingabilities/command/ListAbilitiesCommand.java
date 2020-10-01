package net.saifs.bendingabilities.command;

import com.projectkorra.projectkorra.ability.CoreAbility;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListAbilitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bendingabilities.list")) {
            BAMethods.send(sender, "&cYou do not have permission to do that!");
            return true;
        }
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < CoreAbility.getAbilities().size(); i++) {
            CoreAbility coreAbility = CoreAbility.getAbilities().get(i);
            list.append("&c").append(coreAbility.getName());
            if (i != CoreAbility.getAbilities().size() - 1) {
                list.append("&7, ");
            }
        }
        BAMethods.send(sender, list.toString());
        return true;
    }
}
