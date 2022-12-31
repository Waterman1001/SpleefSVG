package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.GameStartEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.GameType;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class OnGameStarts implements Listener {

	// Gets the location of the next highest block underneath the player that is not air.
	private Location getNextHighestBlockLoc(Location playerLoc) {
		for (int y = playerLoc.getBlockY() - 3; y > 10; y--) { // Minus 3 to also accomodate for players that jump to circumvent anticamping teleport
			Block nextHighestBlock = playerLoc.getWorld().getBlockAt(playerLoc.getBlockX(), y, playerLoc.getBlockZ());
			if (nextHighestBlock.getType() != Material.AIR) {
				Location layerDownLocation = new Location(playerLoc.getWorld(), playerLoc.getX(), y + 2, playerLoc.getZ());
				return layerDownLocation;
			}
		}
		return playerLoc.subtract(0, 3, 0);
	}

	public ArrayList<Player> computeLowestPlayer(Game game) {
		// Find the list of players that are at the lowest Y level at the moment.
		// If there is only 1, anticamping will not be used for this player.
		// Players that have a Y level that is 1 higher than other players are NOT considered to be higher (jumping)
		// Therefore, absolute value to consider jumping players to still be at equal level.
		ArrayList<Player> lowest_players = new ArrayList<>();
		for(Player p : game.getPlayers()) { // This for-loop is necessary to make sure the first non-null player is added to the first index of the lowest_players array.
			if (p == null) continue;
			lowest_players.add(p); // Add the first player of the game as initialisation.
			break;
		}

		for (Player pl : game.getPlayers()) {
			if (pl == null) continue;
			if (lowest_players.contains(pl)) continue;
			if (pl.getLocation().getBlockY() == lowest_players.get(0).getLocation().getBlockY() ||
					Math.abs(pl.getLocation().getBlockY() - lowest_players.get(0).getLocation().getBlockY()) == 1) {
				lowest_players.add(pl);
			} else if (pl.getLocation().getBlockY() < lowest_players.get(0).getLocation().getBlockY()) {
				lowest_players.clear();
				lowest_players.add(pl);
			}
		}
		return lowest_players;
	}

	@EventHandler
	public void onStart(GameStartEvent e) {
		e.getGame().setStartedCountdown(false);
		e.getGame().setStartedGame(true);
		ItemStack item = null;

		if(e.getGame().getMap().getGameType() == GameType.BOWSPLEEF) {
			item = new ItemStack(Material.BOW);
			ItemMeta itemmeta = item.getItemMeta();
			if (itemmeta != null) {
				itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SvestiSpleef Bow");
				itemmeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
				itemmeta.setUnbreakable(true);
			}
			item.setItemMeta(itemmeta);
		} else {
			try {
				item = new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem")));
				ItemMeta itemmeta = item.getItemMeta();
				if (itemmeta != null) {
					itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SvestiSpleef Shovel");
				}
				item.setItemMeta(itemmeta);
			} catch (Exception ex) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SpleefItem in config is not valid!");
				return;
			}
		}

		for (Player pl : e.getGame().getPlayers()) {
			if (pl == null) continue;
			// pl.teleport(e.getGame().getMap().getSpawn());
			pl.getInventory().setItem(0, item);
			if(e.getGame().getMap().getGameType() == GameType.BOWSPLEEF) {
				pl.getInventory().setItem(9, new ItemStack(Material.ARROW));
			}
			e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), Main.getVars().getAntiCampingTime()); // Initialize each player with the maximum anticamping time.
		}

		GameManager.getInstance().getThreads().put(e.getGame(), Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			int i = Main.getVars().getGameTime() + 1;

			@Override
			public void run() {
				i--;
				if (i < 60 && i % 10 == 0 || i <= 5)
					e.getGame().getGs().broadcastGameMessage(Messages.getInstance().gameEndsIn(i));
				if (i <= 1) {
					e.getGame().getGs().broadcastGameMessage(Messages.getInstance().endingGame());
					e.getGame().getGs().finishGame(true);
					return;
				}
			}
		}, 0L, 20L));

		// AntiCamping System
		if (e.getGame().getMap().getAntiCamping()) { // Only if anticamping is enabled for this map we let the countdown clock run. Each player does get anticamping time in the hashmap, but it is simply never counted down.
			// AntiCamping Timer ticking each second for each player.
			GameManager.getInstance().getAntiCampingTimers().put(e.getGame(), Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
				@Override
				public void run() {
					ArrayList<Player> lowest_players = computeLowestPlayer(e.getGame());

					for (Player pl : e.getGame().getPlayers()) {
						if (pl == null) continue;
						if(lowest_players.size() == 1) {
							if(lowest_players.contains(pl)) {
								// Als de speler zijn anticamping is gedisabled omdat hij de laagste persoon is, zet hem terug op 12 seconden.
								e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), Main.getVars().getAntiCampingTime());
								continue;
							}
						}
						int currentTime = e.getGame().getPlayerToAntiCampingTimer().get(pl.getUniqueId());
						int newTime = currentTime - 1;
						e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), newTime); // Subtract 1 from all players anticamping timers.

						if (newTime <= 5 && newTime > 0) {
							pl.sendMessage(Messages.prefix + ChatColor.RED + "AntiCamping: " + ChatColor.YELLOW + "Destroy a block within " + ChatColor.AQUA + newTime + ChatColor.YELLOW + " seconds!");
						} else if (newTime == 0) {
							Location newLocation = getNextHighestBlockLoc(pl.getLocation());
							SpleefPlayerUtils.teleport(pl, newLocation);
							pl.sendMessage(Messages.prefix + ChatColor.RED + "AntiCamping: " + ChatColor.YELLOW + "You have been teleported down one layer!");
							e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), Main.getVars().getAntiCampingTime()); // Give all anticamping time back again as the player was now teleported down.
						}
					}
				}
			}, 0L, 20L));
		}
	}
}
