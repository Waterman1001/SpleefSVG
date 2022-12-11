package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class StatusEvents implements Listener {

	@EventHandler
	public void hunger(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player)e.getEntity();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void health(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) return;
		
		Player p = (Player)e.getEntity();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		
		e.setCancelled(true);
	}
}
