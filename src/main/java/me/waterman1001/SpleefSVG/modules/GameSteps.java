package me.waterman1001.SpleefSVG.modules;

import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.listeners.custom.GameEndEvent;
import me.waterman1001.SpleefSVG.listeners.custom.GameStartEvent;
import me.waterman1001.SpleefSVG.listeners.custom.PlayerWinEvent;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameSteps {

	private Game game;
	public GameSteps(Game game) {
		this.game = game;
	}
	
	public void broadcastGameMessage(String message) {
		for(Player p : this.game.getPlayers()) {
			if(p == null) continue;
			p.sendMessage(message);
		}
		for(Player p : this.game.getSpectators())
			p.sendMessage(message);
	}
	
	public int getPlayersInGame() {
		int amount = 0;
		for(int i = 0; i < this.game.getPlayers().length; i++) {
			if(this.game.getPlayers()[i] != null) amount++;
		}
		
		return amount;
	}
	
	public boolean addPlayer(Player p) {
		for(int i = 0; i < this.game.getPlayers().length; i++) {
			if(this.game.getPlayers()[i] == null) {
				this.game.getPlayers()[i] = p;

				if(!GameManager.getInstance().getPlayersInventory().containsKey(p.getUniqueId())) {
					ItemStack[] inv = p.getInventory().getContents();
					GameManager.getInstance().getPlayersInventory().put(p.getUniqueId(), inv);
				}
				
				if(!GameManager.getInstance().getPlayersArmor().containsKey(p.getUniqueId())) {
					ItemStack[] armor = p.getInventory().getArmorContents();
					GameManager.getInstance().getPlayersArmor().put(p.getUniqueId(), armor);
				}
				
				if(!GameManager.getInstance().getPlayersEXP().containsKey(p.getUniqueId())) {
					GameManager.getInstance().getPlayersEXP().put(p.getUniqueId(), SpleefPlayerUtils.getTotalExperience(p));
				}
				
				if(!GameManager.getInstance().getPlayersLocation().containsKey(p.getUniqueId())) {
					GameManager.getInstance().getPlayersLocation().put(p.getUniqueId(), p.getLocation());
				}
				
				SpleefPlayerUtils.teleport(p, this.game.getMap().getSpawn());
				SpleefPlayerUtils.clearInventory(p);
				SpleefPlayerUtils.fixPlayer(p);
				// SpleefPlayerUtils.hidePlayersThatArentInGame(p, this.game);
				// I do not use this because I would like players to see others that are also outside the game.
				
				broadcastGameMessage(Messages.getInstance().playerJoinedTheGame(p, getPlayersInGame()));
				tryToStartCountdown();
				return true;
			}
		}
		return false;
	}
	
	public boolean addSpectator(Player p) {
		this.game.getSpectators().add(p);
		
		if(!GameManager.getInstance().getPlayersInventory().containsKey(p.getUniqueId())) {
			ItemStack[] inv = p.getInventory().getContents();
			GameManager.getInstance().getPlayersInventory().put(p.getUniqueId(), inv);
		}
		
		if(!GameManager.getInstance().getPlayersArmor().containsKey(p.getUniqueId())) {
			ItemStack[] armor = p.getInventory().getArmorContents();
			GameManager.getInstance().getPlayersArmor().put(p.getUniqueId(), armor);
		}
		
		if(!GameManager.getInstance().getPlayersEXP().containsKey(p.getUniqueId())) {
			GameManager.getInstance().getPlayersEXP().put(p.getUniqueId(), SpleefPlayerUtils.getTotalExperience(p));
		}
		
		if(!GameManager.getInstance().getPlayersLocation().containsKey(p.getUniqueId())) {
			GameManager.getInstance().getPlayersLocation().put(p.getUniqueId(), p.getLocation());
		}
			
		SpleefPlayerUtils.teleport(p, this.game.getMap().getSpawn());
		SpleefPlayerUtils.clearInventory(p);
		SpleefPlayerUtils.fixSpectator(p);

		for(int i = 0; i < game.getPlayers().length; i++) {
			if(game.getPlayers()[i] != null) {
				game.getPlayers()[i].hidePlayer(Main.getInstance(), p);
			}
		}
		
		return true;
	}
	
	public boolean removePlayer(Player p, LoseReason reason) {
		for(int i = 0; i < this.game.getPlayers().length; i++) {
			if(this.game.getPlayers()[i] == null) continue;
			if(this.game.getPlayers()[i] == p) {
				
				this.game.getPlayers()[i] = null;
				SpleefPlayerUtils.showAllPlayers(p);
				
				if(reason == LoseReason.QUIT_GAME) {
					
				} else if(reason == LoseReason.TELEPORTING) {
					SpleefPlayerUtils.respawnPlayer(p);
				} else if(reason == LoseReason.LEAVE_MATCH) {
					SpleefPlayerUtils.respawnPlayer(p);
				} else if(reason == LoseReason.FALL) {
					SpleefPlayerUtils.clearInventory(p);
					SpleefPlayerUtils.teleport(p, game.getMap().getLoseLoc());
					// addSpectator(p); // In case we want players to be added as spectator.
					// However, for Svesti this is not needed at the moment.
				} else if(reason == LoseReason.GAMETIME) { // If the gametime was over then this is the lose reason and you can simply return as there are no winners.
					SpleefPlayerUtils.clearInventory(p);
					SpleefPlayerUtils.teleport(p, game.getMap().getLoseLoc());
					return false;
				} else if(reason == LoseReason.WIN) {
					SpleefPlayerUtils.clearInventory(p);
					SpleefPlayerUtils.teleport(p, game.getMap().getWinLoc());
					// addSpectator(p); // In case we want players to be added as spectator.
					// However, for Svesti this is not needed at the moment.
				}
			}
		}
		
		if(!this.game.startedGame()) {
			broadcastGameMessage(Messages.getInstance().playerLeftTheGame(p, getPlayersInGame()));
			tryToStopCountdown();
		} else {
			int amount = getPlayersInGame();
			if(amount != 0)
				broadcastGameMessage(Messages.getInstance().playerLostTheGame(p, amount));
			tryWin();
		}
		return false;
	}
	
	public boolean removeSpectator(Player p) {
		this.game.getSpectators().remove(p);
		
		SpleefPlayerUtils.showAllPlayers(p);
		
		if(GameManager.getInstance().getPlayersInventory().containsKey(p.getUniqueId())) {
			p.getInventory().setContents(GameManager.getInstance().getPlayersInventory().get(p.getUniqueId()));
			GameManager.getInstance().getPlayersInventory().remove(p.getUniqueId());
		}
		
		if(GameManager.getInstance().getPlayersArmor().containsKey(p.getUniqueId())) {
			p.getInventory().setArmorContents(GameManager.getInstance().getPlayersArmor().get(p.getUniqueId()));
			GameManager.getInstance().getPlayersArmor().remove(p.getUniqueId());
		}
		
		if(GameManager.getInstance().getPlayersEXP().containsKey(p.getUniqueId())) {
			SpleefPlayerUtils.setTotalExperience(p, GameManager.getInstance().getPlayersEXP().get(p.getUniqueId()));
			GameManager.getInstance().getPlayersEXP().remove(p.getUniqueId());
		}
		
		if (GameManager.getInstance().getPlayersLocation().containsKey(p.getUniqueId())) {
			p.teleport(GameManager.getInstance().getPlayersLocation().get(p.getUniqueId()));
		}
		
		p.setAllowFlight(false);
		p.setFlying(false);
		
		return true;
	}
	
	public void tryToStartCountdown() {
		if(getPlayersInGame() >= Main.getVars().getMinPlayersInGame()) {
			startCountdown();
		}
	}

	public void tryToStopCountdown() {
		if(getPlayersInGame() < Main.getVars().getMinPlayersInGame()) {
			stopCountdown();
		}
	}

	public void startCountdown() {
		if(!this.game.startedGame() && !this.game.startedCountdown()) {
			
			final Game myGame = this.game;
			this.game.setStartedCountdown(true);
			Bukkit.broadcastMessage(Messages.getInstance().gameStartsInForBroadcast(Main.getVars().getCountdownTime(), myGame.getMap().getName()));
            GameManager.getInstance().getThreads().put(this.game, Bukkit.getServer().getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
    			int i = Main.getVars().getCountdownTime()+1;

                @Override
                public void run() {
                    i--;
					//if(i%10 == 0)
					//	Bukkit.broadcastMessage(Messages.getInstance().gameStartsInForBroadcast(i, myGame.getMap().getName()));
                    if(i%10 == 0 || i <= 5)
						if(i != 0) {
							broadcastGameMessage(Messages.getInstance().gameStartsIn(i));
						}
					if(i == 4) {
						for (Player pl : myGame.getPlayers()) {
							if (pl == null) continue;
							pl.teleport(myGame.getMap().getSpawn());
						}
					}
                    if(i < 1) {
                    	GameManager.getInstance().getThreads().get(myGame).cancel();
                    	broadcastGameMessage(Messages.getInstance().startingGame());
                    	startGame();
                        return;
                    }
                }
            }, 0L, 20L));
		}
	}

	public void stopCountdown() {
		if(this.game.startedCountdown() && GameManager.getInstance().getThreads().get(this.game) != null) {
			GameManager.getInstance().getThreads().get(this.game).cancel();
			GameManager.getInstance().getThreads().remove(this.game);
			this.game.setStartedCountdown(false);
		}
	}

	public void stopAntiCampingTimer() {
		if(GameManager.getInstance().getAntiCampingTimers().get(this.game) != null) {
			GameManager.getInstance().getAntiCampingTimers().get(this.game).cancel();
			GameManager.getInstance().getAntiCampingTimers().remove(this.game);
		}
	}
	
	public void startGame() {
		GameStartEvent event = new GameStartEvent(game);
        Bukkit.getPluginManager().callEvent(event);
	}

	public void tryWin() {
		int amount = getPlayersInGame();
		if(amount == 1) {
			for(Player pl : this.game.getPlayers()) {
				if(pl == null) continue;

				PlayerWinEvent event = new PlayerWinEvent(pl, game);
		        Bukkit.getPluginManager().callEvent(event);
			}
		}
		
		if(amount < 1) {
			finishGame(false);
		}
	}

	// Added a boolean here to specify whether all players should be removed and teleported to the lose location or not by this function.
	// Usually this is not necessary, because it is then already being done by the removePlayer function that is being called before.
	// However, this is not being done when the timer ends at and the game ends undecided.
	public void finishGame(boolean gameTimeOver) {
		if(GameManager.getInstance().getThreads().containsKey(this.game) && GameManager.getInstance().getThreads().get(this.game) != null)
			GameManager.getInstance().getThreads().get(this.game).cancel();

		stopAntiCampingTimer(); // Stop the AntiCampingTimer ticking when the game ends.

		for(Player pl : this.game.getPlayers()) {
			if(pl != null) {
				SpleefPlayerUtils.clearInventory(pl);
				//addSpectator(pl); // In case we want players to be added as spectator.
				// However, for Svesti this is not needed at the moment.

				if(gameTimeOver) {
					removePlayer(pl, LoseReason.GAMETIME);
				}
			}
		}
		
		this.game.setPlayers(new Player[1]);

		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				endGame();
			}
		}, 10/*Main.getVars().getWinTime()*20*/);
	}

	public void endGame() {
		GameEndEvent event = new GameEndEvent(this.game);
        Bukkit.getPluginManager().callEvent(event);
	}
}
