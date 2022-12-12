package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.listeners.custom.PlayerWinEvent;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPlayerWin implements Listener {

	@EventHandler
	public void onWin(PlayerWinEvent e) {
		e.getGame().getGs().broadcastGameMessage(Messages.getInstance().wonTheGame(e.getPlayer()));
		SpleefPlayerUtils.clearInventory(e.getPlayer());
		SpleefPlayerUtils.teleport(e.getPlayer(), e.getGame().getMap().getWinLoc());
		e.getGame().getGs().finishGame();
	}
	
}
