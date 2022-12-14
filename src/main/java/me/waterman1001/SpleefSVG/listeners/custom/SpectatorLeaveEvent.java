package me.waterman1001.SpleefSVG.listeners.custom;

import me.waterman1001.SpleefSVG.modules.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpectatorLeaveEvent extends Event {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	
	private Player p;
	private Game game;

    public SpectatorLeaveEvent(Player p, Game game) {
        this.p = p;
        this.game = game;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
    	return this.p;
    }
    
    public Game getGame() {
    	return this.game;
    }
}