package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.GameStartEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OnGameStarts implements Listener {

	// Gets the location of the next highest block underneath the player that is not air.
	private Location getNextHighestBlockLoc(Location playerLoc) {
		for(int y = playerLoc.getBlockY() - 3; y > 10; y--) { // Minus 3 to also accomodate for players that jump to circumvent anticamping
			Block nextHighestBlock = playerLoc.getWorld().getBlockAt(playerLoc.getBlockX(), y, playerLoc.getBlockZ());
			if(nextHighestBlock.getType() != Material.AIR) {
				Location layerDownLocation = new Location(playerLoc.getWorld(), playerLoc.getBlockX(), y + 2, playerLoc.getBlockZ());
				return layerDownLocation;
			}
		}
		return playerLoc.subtract(0, 3, 0);
	}

	@EventHandler
	public void onStart(GameStartEvent e) {
		e.getGame().setStartedCountdown(false);
		e.getGame().setStartedGame(true);
		ItemStack item = null;
		
		try {
			item = new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem")));
			ItemMeta itemmeta = item.getItemMeta();
			if (itemmeta != null) {
				itemmeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SvestiSpleef Shovel");
			}
			item.setItemMeta(itemmeta);
		}
		catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SpleefItem in config is not valid!");
			return;
		}

		for(Player pl : e.getGame().getPlayers()) {
			if(pl == null) continue;
			pl.teleport(e.getGame().getMap().getSpawn());
			pl.getInventory().setItem(0, item);
			e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), Main.getVars().getAntiCampingTime()); // Initialize each player with the maximum anticamping time.
		}

        GameManager.getInstance().getThreads().put(e.getGame(), Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			int i = Main.getVars().getGameTime()+1;

            @Override
            public void run() {
                i--;
                if(i < 60 && i%10 == 0 || i <= 5)
                	e.getGame().getGs().broadcastGameMessage(Messages.getInstance().gameEndsIn(i));
                if(i <= 1) {
                	e.getGame().getGs().broadcastGameMessage(Messages.getInstance().endingGame());
                	e.getGame().getGs().finishGame(true);
                    return;
                }
            }
        }, 0L, 20L));

		// AntiCamping Timer ticking each second for each player.
		GameManager.getInstance().getAntiCampingTimers().put(e.getGame(), Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				for(Player pl : e.getGame().getPlayers()) {
					if(pl == null) continue;
					int currentTime = e.getGame().getPlayerToAntiCampingTimer().get(pl.getUniqueId());
					int newTime = currentTime - 1;
					e.getGame().getPlayerToAntiCampingTimer().put(pl.getUniqueId(), newTime); // Subtract 1 from all players anticamping timers.

					if(newTime <= 5 && newTime > 0) {
						pl.sendMessage(Messages.prefix + ChatColor.RED + "AntiCamping: " + ChatColor.YELLOW + "Destroy a block within " + ChatColor.AQUA + newTime + ChatColor.YELLOW + " seconds!");
					} else if(newTime == 0) {
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
