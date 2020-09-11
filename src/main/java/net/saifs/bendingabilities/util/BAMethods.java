package net.saifs.bendingabilities.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class BAMethods {
    public static String colour(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void send(CommandSender sender, String s) {
        sender.sendMessage(colour(s));
    }
}
