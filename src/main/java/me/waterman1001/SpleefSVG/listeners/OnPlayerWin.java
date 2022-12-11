package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.listeners.custom.PlayerWinEvent;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPlayerWin implements Listener {

	@EventHandler
	public void onWin(PlayerWinEvent e) {
		e.getGame().getGs().broadcastGameMessage(Messages.getInstance().wonTheGame(e.getPlayer()));
		e.getGame().getGs().finishGame();
	}
	
}
