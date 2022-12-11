package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.listeners.custom.GameEndEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnGameEnds implements Listener {

	@EventHandler
	public void onEnd(GameEndEvent e) {
		for(Player pl : e.getGame().getSpectators())
			SpleefPlayerUtils.respawnPlayer(pl);
		GameManager.getInstance().reloadGame(e.getGame());
	}
}
