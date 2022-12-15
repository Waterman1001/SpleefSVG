package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.PlayerSpleefBlockEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockEvents implements Listener {
	
	@EventHandler
	public void onStartBreaking(PlayerInteractEvent e) {
		Player p = e.getPlayer();
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

		if(game.getMap().getGameType() == GameType.SPLEEF) {

			if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;

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
		} else if(game.getMap().getGameType() == GameType.SPLEGG) {
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;

			PlayerSpleefBlockEvent event = new PlayerSpleefBlockEvent(p, game, e.getClickedBlock());
			Bukkit.getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				p.launchProjectile(Egg.class, p.getLocation().getDirection().multiply(2));
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

	@EventHandler
	public void onSpleggEggHit(ProjectileHitEvent e) {
		if(e.getEntity().getType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if(egg.getLocation().getWorld().getName().equalsIgnoreCase("Minigames")) {
				if (egg.getShooter() instanceof Player p) {
					Game game = GameManager.getInstance().getPlayerGame(p);
					if (game == null) return;

					if (game.getMap().getGameType() == GameType.SPLEGG) {
						Block hitBlock = e.getHitBlock();
						Material hitBlockType = hitBlock.getType();
						if (hitBlockType == Material.SNOW_BLOCK || hitBlockType == Material.SNOW
								|| hitBlockType == Material.PACKED_ICE || hitBlockType == Material.CUT_SANDSTONE
								|| hitBlockType == Material.BROWN_MUSHROOM_BLOCK || Tag.WOOL.isTagged(hitBlockType)
								|| Tag.LOGS.isTagged(hitBlockType) || Tag.TERRACOTTA.isTagged(hitBlockType)) {
							hitBlock.getDrops().clear();
							hitBlock.setType(Material.AIR);
						}
						e.setCancelled(true);
					}
				}
			}
		}
	}
}
