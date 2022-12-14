package me.waterman1001.SpleefSVG.listeners.custom;

import me.waterman1001.SpleefSVG.modules.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStartEvent extends Event implements Cancellable {
	
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private Game game;
    private boolean isCancelled;

    public GameStartEvent(Game game) {
        this.game = game;
        this.isCancelled = false;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public Game getGame() {
    	return this.game;
    }
}