package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.GameMap;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class OnMobSpawn implements Listener {

	@EventHandler
	public void mobSpawn(EntitySpawnEvent e) {
		if(e.getEntity() instanceof LivingEntity) {
			for(GameMap map : GameManager.getInstance().getMaps()) {
				if(e.getLocation().getWorld().getName().equals(map.getSpawn().getWorld().getName())) {
					if (e.getLocation().distance(map.getSpawn()) < Main.getVars().getMaxDistanceFromMap()) {
						e.setCancelled(true);
						e.getEntity().remove();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void itemSpawn(ItemSpawnEvent e) {
		if(e.getEntity().getType() == EntityType.SNOWBALL || e.getEntity().getItemStack().getType() == Material.SNOWBALL) {
			for(GameMap map : GameManager.getInstance().getMaps()) {
				if(e.getLocation().getWorld().getName().equals(map.getSpawn().getWorld().getName())) {
					if (e.getLocation().distance(map.getSpawn()) < Main.getVars().getMaxDistanceFromMap()) {
						e.getEntity().remove();
					}
				}
			}
		}
	}
}
