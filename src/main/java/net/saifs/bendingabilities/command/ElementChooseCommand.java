package net.saifs.bendingabilities.command;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ElementChooseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bendingabilities.elementchoose")){
            BAMethods.send(sender, "&cYou do not have permission for that!");
            return true;
        }
        if (args.length == 0) {
            BAMethods.send(sender, "&cUsage - /elementchoose <player>");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            BAMethods.send(sender, "&cThat player was not found!");
            return true;
        }
        BendingPlayer bendingPlayer = BendingPlayer.getBendingPlayer(player);
        if (bendingPlayer.getElements().size() > 0) {
            BAMethods.send(player, "&cYou've already picked an element!");
            return true;
        }

        BendingAbilities.getInstance().getElementalChooseGUI().open(player);
        return true;
    }
}
