package me.waterman1001.SpleefSVG.modules;

import java.util.*;

import me.waterman1001.SpleefSVG.Main;
import org.bukkit.entity.Player;

public class Game {
	
	private Player[] players;
	private List<Player> spectators;
	private ArrayList<UUID> playersQuitDuringGame;
	private HashMap<UUID, Integer> playerToAntiCampingTimer;
	private ArrayList<UUID> playersWithBowHitCooldown;
	private GameMap map;
	private boolean startedCountdown;
	private boolean startedGame;
	private boolean isBeingDeleted;
	private GameSteps gs;
	
	public Game(GameMap map) {
		this.players = new Player[Main.getVars().getMaxPlayersInGame()];
		this.spectators = new LinkedList<Player>();
		this.playersQuitDuringGame = new ArrayList<UUID>();
		this.playerToAntiCampingTimer = new HashMap<UUID, Integer>();
		this.playersWithBowHitCooldown = new ArrayList<UUID>();
		this.map = map;
		
		this.startedCountdown = false;
		this.startedGame = false;

		this.isBeingDeleted = false;
		
		this.gs = new GameSteps(this);
	}

	public Player[] getPlayers() {
		return players;
	}

	public void setPlayers(Player[] players) {
		this.players = players;
	}

	public List<Player> getSpectators() {
		return spectators;
	}

	public ArrayList<UUID> getPlayersQuitDuringGame() {
		return playersQuitDuringGame;
	}

	public HashMap<UUID, Integer> getPlayerToAntiCampingTimer() {
		return playerToAntiCampingTimer;
	}

	public void setSpectators(List<Player> spectators) {
		this.spectators = spectators;
	}

	public GameMap getMap() {
		return map;
	}

	public void setPlayerToAntiCampingTimer(HashMap<UUID, Integer> playerToAntiCampingTimer) {
		this.playerToAntiCampingTimer = playerToAntiCampingTimer;
	}

	public ArrayList<UUID> getPlayersWithBowHitCooldown() {
		return this.playersWithBowHitCooldown;
	}

	public void setPlayersWithBowHitCooldown(ArrayList<UUID> playersWithBowHitCooldown) {
		this.playersWithBowHitCooldown = playersWithBowHitCooldown;
	}

	public void setMap(GameMap map) {
		this.map = map;
	}
	public boolean startedCountdown() { 
		return startedCountdown;
	}
	
	public void setStartedCountdown(boolean choice) {
		this.startedCountdown = choice;
	}
	
	public boolean startedGame() { 
		return startedGame;
	}
	
	public void setStartedGame(boolean choice) {
		this.startedGame = choice;
	}
	
	public int getAvailableSpots() {
		int amount = 0;
		for(int i = 0; i < players.length; i++) {
			if(players[i] == null) amount++;
		}
		
		return amount;
	}

	public boolean isBeingDeleted() {
		return isBeingDeleted;
	}

	public void setBeingDeleted(boolean isBeingDeleted) {
		this.isBeingDeleted = isBeingDeleted;
	}

	public GameSteps getGs() {
		return gs;
	}

	public void setGs(GameSteps gs) {
		this.gs = gs;
	}
}
