package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.LoseReason;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DistanceEvents implements Listener {

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if((int)e.getFrom().getX() == (int)e.getTo().getX()
				&& (int)e.getFrom().getY() == (int)e.getTo().getY()
				&& (int)e.getFrom().getZ() == (int)e.getTo().getZ()) return;

		Player p = e.getPlayer();
		
		boolean isAPlayer = true;
		Game game = GameManager.getInstance().getPlayerGame(p);
		
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			isAPlayer = false;
		}
		
		if(game == null) return;
		
		Location map = game.getMap().getSpawn();
		Location player = p.getLocation();
		
		if(player.distance(map) > Main.getVars().getMaxDistanceFromMap()) {
			e.setCancelled(true);
			e.setTo(e.getFrom());
			
			if(isAPlayer) {
				game.getGs().removePlayer(p, LoseReason.TELEPORTING);
			}
		}
	}
	
}
