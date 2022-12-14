package me.waterman1001.SpleefSVG.utils;

import me.waterman1001.SpleefSVG.Main;

public class GlobalVariables {

	public int MAX_PLAYERS_IN_GAME = 8;
	public int MIN_PLAYERS_IN_GAME = 2;
	public int COUNTDOWN_TIME = 20;
	public int GAME_TIME = 360;
	public int ANTICAMPING_TIME = 12;
	public int WIN_TIME = 10;
	public int MAX_DISTANCE_FROM_MAP = 60;
	public int REWARD = 40;

	public GlobalVariables() {
		if(Main.getInstance().getConfig() == null)
			Main.getInstance().saveDefaultConfig();
		
		if(Main.getInstance().getConfig().contains("MaxPlayersInGame"))
			MAX_PLAYERS_IN_GAME = Main.getInstance().getConfig().getInt("MaxPlayersInGame");
		
		if(Main.getInstance().getConfig().contains("MinimumPlayersInGame"))
			MIN_PLAYERS_IN_GAME = Main.getInstance().getConfig().getInt("MinimumPlayersInGame");
		
		if(Main.getInstance().getConfig().contains("CountdownTime"))
			COUNTDOWN_TIME = Main.getInstance().getConfig().getInt("CountdownTime");
		
		if(Main.getInstance().getConfig().contains("GameTime"))
			GAME_TIME = Main.getInstance().getConfig().getInt("GameTime");

		if(Main.getInstance().getConfig().contains("AntiCampingTime"))
			ANTICAMPING_TIME = Main.getInstance().getConfig().getInt("AntiCampingTime");
		
		if(Main.getInstance().getConfig().contains("WinTime"))
			WIN_TIME = Main.getInstance().getConfig().getInt("WinTime");
		
		if(Main.getInstance().getConfig().contains("MaxDistanceFromMap"))
			MAX_DISTANCE_FROM_MAP = Main.getInstance().getConfig().getInt("MaxDistanceFromMap");

		if(Main.getInstance().getConfig().contains("Reward"))
			REWARD = Main.getInstance().getConfig().getInt("Reward");
	}
	
	public int getMaxPlayersInGame() {
		return this.MAX_PLAYERS_IN_GAME;
	}
	public int getMinPlayersInGame() {
		return this.MIN_PLAYERS_IN_GAME;
	}
	public int getCountdownTime() {
		return this.COUNTDOWN_TIME;
	}
	public int getGameTime() {
		return this.GAME_TIME;
	}

	public int getAntiCampingTime() {
		return this.ANTICAMPING_TIME;
	}

	public int getWinTime() {
		return this.WIN_TIME;
	}
	public int getMaxDistanceFromMap() {
		return this.MAX_DISTANCE_FROM_MAP;
	}
	public int getReward() {
		return this.REWARD;
	}
}
