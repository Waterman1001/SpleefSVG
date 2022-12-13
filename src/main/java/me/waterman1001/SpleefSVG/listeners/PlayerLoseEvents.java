package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.listeners.custom.PlayerLoseEvent;
import me.waterman1001.SpleefSVG.listeners.custom.SpectatorLeaveEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.LoseReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLoseEvents implements Listener {

	@EventHandler
	public void move(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) return;
		
		if(p.getLocation().getY() < game.getMap().getminY() && game.startedGame()) {
			PlayerLoseEvent event = new PlayerLoseEvent(p, game, LoseReason.FALL);
	        Bukkit.getPluginManager().callEvent(event);
	        if(event.isCancelled())
	        	return;
			game.getGs().removePlayer(p, LoseReason.FALL);
		}
	}
	
	@EventHandler
	public void quit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game != null) {
			PlayerLoseEvent event = new PlayerLoseEvent(p, game, LoseReason.QUIT_GAME);
	        Bukkit.getPluginManager().callEvent(event);
	        if(event.isCancelled())
	        	return;

			game.getPlayersQuitDuringGame().add(p.getUniqueId()); // Add the player to the players who left during the game array to tp them away on join.
			game.getGs().removePlayer(p, LoseReason.QUIT_GAME);
		}
		
		game = GameManager.getInstance().getPlayerGameSpectator(p);
		if(game != null) {
			SpectatorLeaveEvent event = new SpectatorLeaveEvent(p, game);
	        Bukkit.getPluginManager().callEvent(event);
			game.getGs().removeSpectator(p);
		}
	}
}
