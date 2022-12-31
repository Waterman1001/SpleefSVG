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
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockEvents implements Listener {

	private final ArrayList<Material> breakable_blocks = new ArrayList<>(
			Arrays.asList(
					Material.SNOW_BLOCK,
					Material.SNOW,
					Material.PACKED_ICE,
					Material.CUT_SANDSTONE,
					Material.BROWN_MUSHROOM_BLOCK,
					Material.COAL_BLOCK,
					Material.REDSTONE_BLOCK,
					Material.LAPIS_BLOCK,
					Material.SMOOTH_QUARTZ,
					Material.CHISELED_QUARTZ_BLOCK,
					Material.SPONGE,
					Material.DIAMOND_BLOCK,
					Material.END_STONE,
					Material.OBSIDIAN,
					Material.OCHRE_FROGLIGHT,
					Material.VERDANT_FROGLIGHT,
					Material.PEARLESCENT_FROGLIGHT,
					Material.MOSS_BLOCK,
					Material.TNT)
	);

	@EventHandler
	public void onStartBreaking(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Game game = GameManager.getInstance().getPlayerGame(p);

		if (game == null) {
			game = GameManager.getInstance().getPlayerGameSpectator(p);

			if (game == null) return;
		}

		if (p.getInventory().getItemInMainHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))
				&& p.getInventory().getItemInOffHand().getType() != Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))
				&& p.getInventory().getItemInMainHand().getType() != Material.BOW
				&& p.getInventory().getItemInOffHand().getType() != Material.BOW) {
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
				Block clickedBlock = e.getClickedBlock();
				if(clickedBlock != null) {
					if(clickedBlock.getY() < game.getMap().getmaxY()) {
						Material clickedBlockType = clickedBlock.getType();
						if (breakable_blocks.contains(clickedBlockType) || Tag.LOGS.isTagged(clickedBlockType)
								|| Tag.LEAVES.isTagged(clickedBlockType) || Tag.TERRACOTTA.isTagged(clickedBlockType)
								|| Tag.WOOL.isTagged(clickedBlockType) || Tag.PLANKS.isTagged(clickedBlockType)) {
							e.getClickedBlock().getDrops().clear();
							event.getBlock().getDrops().clear();
							e.getClickedBlock().setType(Material.AIR);
							game.getPlayerToAntiCampingTimer().put(p.getUniqueId(), Main.getVars().getAntiCampingTime()); // Give all anticamping time back again after a block destroy.
						}
					}
				}
				e.setCancelled(true);
			}
		} else {
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) {
				e.setCancelled(true);
			} else if(game.getMap().getGameType() == GameType.SPLEGG) {
				p.launchProjectile(Egg.class, p.getLocation().getDirection().multiply(1.05));
			}
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
	public void onSpleggEggOrBowHit(ProjectileHitEvent e) {
		Projectile projectile = e.getEntity();
		if(!projectile.getLocation().getWorld().getName().equalsIgnoreCase("Minigames")) return;

		if (projectile.getShooter() instanceof Player p) {
			Game game = GameManager.getInstance().getPlayerGame(p);
			if (game == null) return;

			if ((game.getMap().getGameType() == GameType.SPLEGG && e.getEntity().getType() == EntityType.EGG) ||
					(game.getMap().getGameType() == GameType.BOWSPLEEF && e.getEntity().getType() == EntityType.ARROW)) {

				PlayerSpleefBlockEvent event = new PlayerSpleefBlockEvent(p, game, e.getHitBlock());
				Bukkit.getPluginManager().callEvent(event);

				if(!event.isCancelled()) {
					Block hitBlock = e.getHitBlock();
					if(hitBlock == null) return;
					if (hitBlock.getY() <= game.getMap().getmaxY()) {
						Material hitBlockType = hitBlock.getType();
						if (breakable_blocks.contains(hitBlockType) || Tag.LOGS.isTagged(hitBlockType)
								|| Tag.LEAVES.isTagged(hitBlockType) || Tag.TERRACOTTA.isTagged(hitBlockType)
								|| Tag.WOOL.isTagged(hitBlockType) || Tag.PLANKS.isTagged(hitBlockType)) {
							hitBlock.getDrops().clear();
							hitBlock.setType(Material.AIR);
							e.getEntity().remove();
							game.getPlayerToAntiCampingTimer().put(p.getUniqueId(), Main.getVars().getAntiCampingTime()); // Give all anticamping time back again after a block destroy.
						}
					}
					e.setCancelled(true);
				}
			}
		}
	}
}
