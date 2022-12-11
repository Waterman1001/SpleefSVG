package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PickupDropEvents implements Listener {
	
	@EventHandler
	public void onPickup(EntityPickupItemEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player) e.getEntity();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		
		e.setCancelled(true);
	}
}
