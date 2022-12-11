package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.PlayerSpleefBlockEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Game game = GameManager.getInstance().getPlayerGame(p);
		
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		
		if(p.getInventory().getItemInMainHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))
				&& p.getInventory().getItemInOffHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))) {
			e.setCancelled(true);
			return;
		}
		
		if(!game.startedGame()) {
			e.setCancelled(true);
			return;
		}
		
		PlayerSpleefBlockEvent event = new PlayerSpleefBlockEvent(p, game, e.getBlock());
        Bukkit.getPluginManager().callEvent(event);

		if(!event.isCancelled()) {
			if(e.getBlock().getType() == Material.SNOW_BLOCK || e.getBlock().getType() == Material.SNOW
				|| e.getBlock().getType() == Material.PACKED_ICE || Tag.WOOL.isTagged(e.getBlock().getType())) {
				e.getBlock().getDrops().clear();
				event.getBlock().getDrops().clear();
				e.getBlock().setType(Material.AIR);
				e.setCancelled(true);
			}
		} else {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Game game = GameManager.getInstance().getPlayerGame(p);
		if(game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);
			
			if(game == null) return;
		}
		e.setCancelled(true);
	}
}
