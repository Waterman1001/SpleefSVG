package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.GameMap;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

public class SnowMeltEvent implements Listener {

	@EventHandler
	public void melt(BlockFadeEvent e) {
		if(e.getBlock().getType() == Material.SNOW_BLOCK) {
			for(GameMap map : GameManager.getInstance().getMaps()) {
				if(e.getBlock().getLocation().distance(map.getSpawn()) < Main.getVars().getMaxDistanceFromMap())
					e.setCancelled(true);
			}
		}
	}
}
	