package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.listeners.custom.GameEndEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGameEnds implements Listener {

	@EventHandler
	public void onEnd(GameEndEvent e) {
		/*
		for(Player pl : e.getGame().getSpectators())
			SpleefPlayerUtils.respawnPlayer(pl);
		 */
		if(!e.getGame().isBeingDeleted()) {
			GameManager.getInstance().reloadGame(e.getGame());
		}
	}
}
