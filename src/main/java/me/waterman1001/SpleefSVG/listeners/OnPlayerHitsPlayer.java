package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class OnPlayerHitsPlayer implements Listener {

    @EventHandler
    public void onPlayerHitsPlayer(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player p) {
            if(!p.getLocation().getWorld().getName().equalsIgnoreCase("Minigames")) return;

            Game game = GameManager.getInstance().getPlayerGame(p);
            if (game == null) return;

            if(e.getDamager() instanceof Player damager) {
                if (game.getMap().getGameType() != GameType.BOWSPLEEF) return;

                if (damager.getInventory().getItemInMainHand().getType() == Material.BOW) {

                    if(game.getPlayersWithBowHitCooldown().contains(p.getUniqueId())) return;

                    game.getPlayersWithBowHitCooldown().add(p.getUniqueId());
                    p.setVelocity(new Vector(0, 0.78, 0));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> game.getPlayersWithBowHitCooldown().remove(p.getUniqueId()), 3*20);
                }
            }
        }
    }
}
