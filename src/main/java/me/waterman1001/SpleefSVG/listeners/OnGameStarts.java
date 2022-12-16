package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.GameStartEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class OnGameStarts implements Listener {

	@EventHandler
	public void onStart(GameStartEvent e) {
		e.getGame().setStartedCountdown(false);
		e.getGame().setStartedGame(true);
		ItemStack item = null;
		
		try { item = new ItemStack(Material.valueOf(Main.getInstance().getConfig().getString("SpleefItem"))); }
		catch (Exception ex) { Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "SpleefItem in config is not valid!"); return; }

		for(Player pl : e.getGame().getPlayers()) {
			if(pl == null) continue;
			pl.teleport(e.getGame().getMap().getSpawn());
			pl.getInventory().setItem(0, item);
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
	}
}
