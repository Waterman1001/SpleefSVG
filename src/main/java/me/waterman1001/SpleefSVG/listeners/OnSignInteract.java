package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnSignInteract implements Listener {

    @EventHandler
    public void onPlayerSignInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getState() instanceof Sign sign) {
            Player p = e.getPlayer();
            if(sign.getLine(0).equals("§8[§6SpleefSVG§8]")) {
                if(sign.getLine(1).equals("§8[§bJoin§8]")) {
                    if (!sign.getLine(2).isEmpty()) {
                        String map_name = sign.getLine(2);

                        if(!p.hasPermission("spleef.sign.join")) {
                            p.sendMessage(Messages.noPermissions);
                            return;
                        }

                        for (Game game : GameManager.getInstance().getAvailableGame()) {
                            if (game.getMap().getName().equalsIgnoreCase(ChatColor.stripColor(map_name))) {

                                p.sendMessage(GameManager.getInstance().joinPlayer(p, game));
                                return;
                            }
                        }
                        p.sendMessage(ChatColor.RED + "Map " + map_name + ChatColor.RED + " is not available at the moment.");
                    }
                } else if(sign.getLine(1).equals("§8[§cLeave§8]")) {
                    if(sign.getLine(2).isEmpty() && sign.getLine(3).isEmpty()) {

                        if(!p.hasPermission("spleef.sign.quit")) {
                            p.sendMessage(Messages.noPermissions);
                            return;
                        }

                        p.sendMessage(GameManager.getInstance().quitPlayer(p));
                    }
                }
            }
        }
    }
}
