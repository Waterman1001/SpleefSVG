package me.waterman1001.SpleefSVG.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeEvents implements Listener {

    @EventHandler
    public void onJoinSignCreation(SignChangeEvent e) {
        Player p = e.getPlayer();
        if(p.hasPermission("spleef.signs")) {
            if (e.getLine(0).equalsIgnoreCase("[SpleefSVG]")){
                if(e.getLine(1).equalsIgnoreCase("Join")) {
                    if(!e.getLine(2).isEmpty()) {
                        e.setLine(0, "§8[§6SpleefSVG§8]");
                        e.setLine(1, "§8[§bJoin§8]");
                        e.setLine(2, ChatColor.GREEN + e.getLine(2));
                    }
                }
                if(e.getLine(1).equalsIgnoreCase("Leave")) {
                    if(e.getLine(2).isEmpty() && e.getLine(3).isEmpty()) {
                        e.setLine(0, "§8[§6SpleefSVG§8]");
                        e.setLine(1, "§8[§cLeave§8]");
                    }
                }
            }
        }
    }
}
