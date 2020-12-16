package net.saifs.bendingabilities.command;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.CoreAbility;
import net.saifs.bendingabilities.BendingAbilities;
import net.saifs.bendingabilities.util.BAMethods;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ListAbilitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bendingabilities.list")) {
            BAMethods.send(sender, "&cYou do not have permission to do that!");
            return true;
        }

        boolean fileout = false;

        if (args.length > 0 && args[0].equalsIgnoreCase("fileout")) {
            fileout = true;
        }
        StringBuilder list = new StringBuilder();

        if (fileout) {
            List<Element> elements = Arrays.asList(Element.FIRE, Element.WATER, Element.EARTH, Element.AIR);
            try {
                FileWriter writer = new FileWriter(BendingAbilities.getInstance().getDataFolder() + "/abilities.txt");
                for (Element element : elements) {
                    for (Ability ability : getAbilities(element)) {
                        String elementName;
                        if (Element.FIRE.equals(element)) {
                            elementName = "fire";
                        } else if (Element.WATER.equals(element)) {
                            elementName = "water";
                        } else if (Element.AIR.equals(element)) {
                            elementName = "air";
                        } else if (Element.EARTH.equals(element)) {
                            elementName = "earth";
                        } else {
                            continue;
                        }

                        writer.append(ability.getName()).append(" ").append(elementName).append("\n");
                    }
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < CoreAbility.getAbilities().size(); i++) {
            CoreAbility coreAbility = CoreAbility.getAbilities().get(i);
            list.append("&c").append(coreAbility.getName());
            if (i != CoreAbility.getAbilities().size() - 1) {
                list.append("&7, ");
            }
            continue;
        }


        BAMethods.send(sender, list.toString());
        return true;
    }

    private List<Ability> getAbilities(Element element) {
        List<Ability> list = CoreAbility.getAbilities().stream().filter(ability -> hasAbility(ability, element))
                .collect(Collectors.toList());
        Map<String, Ability> map = new HashMap<>();
        for (Ability ability : list) {
            if (!map.containsKey(ability.getName())) {
                map.put(ability.getName(), ability);
            }
        }
        return new ArrayList<>(map.values());
    }

    private boolean hasAbility(Ability ability, Element element) {
        if (!ability.isEnabled()) return false;
        if (ability.getElement() instanceof Element.SubElement)
            return ability.getElement() == element;
        return ability.getElement() == element;
    }
}
