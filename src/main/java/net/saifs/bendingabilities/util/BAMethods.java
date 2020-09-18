package net.saifs.bendingabilities.util;

import com.projectkorra.projectkorra.ability.Ability;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BAMethods {
    public static String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(CommandSender sender, String s) {
        sender.sendMessage(colour(s));
    }

    public static <T> List<T> combineLists(List<T>... lists) {
        List<T> list = new ArrayList<>();
        for (List<T> l : lists) {
            list.addAll(l);
        }
        return list;
    }
}
