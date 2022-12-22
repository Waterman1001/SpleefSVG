package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.PlayerWinEvent;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPlayerWin implements Listener {

	@EventHandler
	public void onWin(PlayerWinEvent e) {
		//e.getGame().getGs().broadcastGameMessage(Messages.getInstance().wonTheGame(e.getPlayer()));
		Bukkit.getServer().broadcastMessage(Messages.getInstance().wonTheGame(e.getPlayer(), e.getGame().getMap().getName()));
		SpleefPlayerUtils.clearInventory(e.getPlayer());
		SpleefPlayerUtils.teleport(e.getPlayer(), e.getGame().getMap().getWinLoc());
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + e.getPlayer().getName() + " " + Main.getVars().getReward());
		e.getGame().getGs().finishGame(false);
	}
	
}
