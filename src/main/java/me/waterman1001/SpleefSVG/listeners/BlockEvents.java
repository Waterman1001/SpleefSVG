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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockEvents implements Listener {
	
	@EventHandler
	public void onStartBreaking(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Game game = GameManager.getInstance().getPlayerGame(p);

		if (game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);

			if (game == null) return;
		}

		if (p.getInventory().getItemInMainHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))
				&& p.getInventory().getItemInOffHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))) {
			e.setCancelled(true);
			return;
		}

		if (!game.startedGame()) {
			e.setCancelled(true);
			return;
		}

		PlayerSpleefBlockEvent event = new PlayerSpleefBlockEvent(p, game, e.getClickedBlock());
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			Material clickedBlock = e.getClickedBlock().getType();
			if (clickedBlock == Material.SNOW_BLOCK || clickedBlock == Material.SNOW
					|| clickedBlock == Material.PACKED_ICE || clickedBlock == Material.CUT_SANDSTONE
					|| clickedBlock == Material.BROWN_MUSHROOM_BLOCK || Tag.WOOL.isTagged(clickedBlock)
					|| Tag.LOGS.isTagged(clickedBlock) || Tag.TERRACOTTA.isTagged(clickedBlock)) {
				e.getClickedBlock().getDrops().clear();
				event.getBlock().getDrops().clear();
				e.getClickedBlock().setType(Material.AIR);
			}
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
