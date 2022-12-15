package me.waterman1001.SpleefSVG.managers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.modules.GameMap;
import me.waterman1001.SpleefSVG.modules.GameType;
import me.waterman1001.SpleefSVG.modules.LoseReason;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class GameManager {

	private static GameManager instance = new GameManager();
	public static GameManager getInstance() { return instance; }
	
	private Map<Game, BukkitTask> threads;
	private List<GameMap> maps;
	private List<Game> games;
	private Map<UUID, ItemStack[]> playersInventory;
	private Map<UUID, ItemStack[]> playersArmor;
	private Map<UUID, Integer> playersEXP;
	private Map<UUID, Location> playersLocation;
	
	private GameManager() {
		this.threads = new HashMap<Game, BukkitTask>();
		this.maps = new LinkedList<GameMap>();
		this.games = new LinkedList<Game>();
		this.setPlayersInventory(new HashMap<UUID, ItemStack[]>());
		this.setPlayersArmor(new HashMap<UUID, ItemStack[]>());
		this.setPlayersEXP(new HashMap<UUID, Integer>());
		this.setPlayersLocation(new HashMap<UUID, Location>());
		setup();
	}
	
	public void setup() {
		maps = StorageManager.getInstance().getStorage().getMaps();
		for(GameMap map : maps) {
			Game game = new Game(map);
			games.add(game);
		}
	}
	
	public List<Game> getGames() {
		return this.games;
	}
	
	public List<GameMap> getMaps() {
		return this.maps;
	}
	
	public Map<Game, BukkitTask> getThreads() {
		return threads;
	}

	public Map<UUID, ItemStack[]> getPlayersInventory() {
		return playersInventory;
	}

	public void setPlayersInventory(Map<UUID, ItemStack[]> playersInventory) {
		this.playersInventory = playersInventory;
	}

	public Map<UUID, ItemStack[]> getPlayersArmor() {
		return playersArmor;
	}

	public void setPlayersArmor(Map<UUID, ItemStack[]> playersArmor) {
		this.playersArmor = playersArmor;
	}
	
	public Map<UUID, Integer> getPlayersEXP() {
		return playersEXP;
	}

	public void setPlayersEXP(Map<UUID, Integer> playersEXP) {
		this.playersEXP = playersEXP;
	}
	
	public Map<UUID, Location> getPlayersLocation() {
		return playersLocation;
	}

	public void setPlayersLocation(Map<UUID, Location> playersLocation) {
		this.playersLocation = playersLocation;
	}
	
	/**
	 * @return         List of {@link me.waterman1001.SpleefSVG.modules.Game}s
	 */
	public List<Game> getAvailableGame() {
		List<Game> available = new LinkedList<Game>();
		
		for(Game game : games) {
			if(!game.startedGame())
				available.add(game);
		}
		return available;
	}
	
	/**
	 * @return         String containing all map names
	 */
	public String mapsList() {
		String list = ChatColor.AQUA + "Maps: ";
		for(GameMap map : maps) {
			list += map.getName() + ", ";
		}
		list = list.substring(0, list.length()-2);
		return list;
	}
	
	/**
	 * @param p        Player to get the game of
	 * @return         The {@link me.waterman1001.SpleefSVG.modules.Game} object containing player
	 */
	public Game getPlayerGame(Player p) {
		for(Game game : games) {
			for(Player gameP : game.getPlayers()) {
				if(gameP == null) continue;
				if(gameP == p) return game;
			}
		}
		return null;
	}
	
	/**
	 * @param p        Player (Spectator) to get the game of
	 * @return         The {@link me.waterman1001.SpleefSVG.modules.Game} object containing player
	 */
	public Game getPlayerGameSpectator(Player p) {
		for(Game game : games) {
			for(Player gameP : game.getSpectators()) {
				if(gameP == null) continue;
				if(gameP == p) return game;
			}
		}
		return null;
	}
	
	/**
	 * Reloads a game
	 * 
	 * @param game      Game to reload
	 */
	public void reloadGame(Game game) {
		game.getMap().loadMap();

		game.setPlayers(new Player[Main.getVars().getMaxPlayersInGame()]);
		game.setSpectators(new LinkedList<Player>());
		
		game.setStartedCountdown(false);
		game.setStartedGame(false);
	}
	
	/**
	 * Adds a player to the first available game
	 * 
	 * @param p            Player to add to the game
	 * @return String      Messages of success/failure
	 */
	public String joinPlayer(Player p) {
		if(!p.getWorld().getName().equalsIgnoreCase("Minigames")) return ChatColor.RED + "Unfortunately, you are not able to join Spleef from this world directly.";
		if(getPlayerGame(p) != null) return Messages.getInstance().alreadyInGame();
		if(getPlayerGameSpectator(p) != null) return Messages.getInstance().youAreSpectating();

		List<Game> available = getAvailableGame();
		
		if(available.size() == 0)
			return Messages.getInstance().noAvailableGames();
	
		int i = 0;
		Game game = available.get(i);
		while(game.getAvailableSpots() == 0) {
			i++;
			if(i > available.size()) return Messages.getInstance().noAvailableGames();
			game = available.get(i);
		}
		
		if(game.getGs().addPlayer(p))
			return Messages.getInstance().joiningGame();
		else
			return Messages.getInstance().couldntAddToGame();
	}

	/**
	 * Adds a player to a specified game
	 *
	 * @param p            Player to add to the game
	 * @param game		   Game to add the player to
	 * @return String      Messages of success/failure
	 */
	public String joinPlayer(Player p, Game game) {
		if(!p.getWorld().getName().equalsIgnoreCase("Minigames")) return ChatColor.RED + "Unfortunately, you are not able to join Spleef from this world directly.";
		if(getPlayerGame(p) != null) return Messages.getInstance().alreadyInGame();
		if(getPlayerGameSpectator(p) != null) return Messages.getInstance().youAreSpectating();

		if(game.getGs().addPlayer(p))
			return Messages.getInstance().joiningGame();
		else
			return Messages.getInstance().couldntAddToGame();
	}
	
	/**
	 * Removes a player from their game
	 * 
	 * @param p            Player to remove from the game
	 * @return String      Messages of success/failure
	 */
	public String quitPlayer(Player p) {
		Game game = getPlayerGame(p);
		if(game == null) {
			game = getPlayerGameSpectator(p);
			if(game == null)
				return Messages.getInstance().youArentInGame();
		}
		
		if(game.getGs().removePlayer(p, LoseReason.LEAVE_MATCH))
			return Messages.getInstance().leavingGame();
		else {
			if(game.getGs().removeSpectator(p))
				return Messages.getInstance().leavingGame();
			else
				return Messages.getInstance().couldntRemoveFromGame();
		}
	}
	
	/**
	 * Force-starts player's game
	 * 
	 * @param p            Player to forcestart game
	 * @return String      Messages of success/failure
	 */
	public String forcestartPlayer(Player p) {
		Game game = getPlayerGame(p);
		if(game == null) return Messages.getInstance().youArentInGame();
		
		if(game.startedGame()) return Messages.getInstance().gameAlreadyStarted();
		
		if(game.startedCountdown())
			game.getGs().stopCountdown();
		
		game.getGs().startGame();
		return Messages.getInstance().forceStartingGame();
	}
	
	/**
	 * @param p            Player to turn to a spectator
	 * @param target       Player to get the game of
	 * @return String      Message of success/failure
	 */
	public String spectatePlayer(Player p, Player target) {
		Game game = getPlayerGame(target);
		if(game == null) return Messages.getInstance().playerNotInGame(target);
		
		game.getGs().addSpectator(p);
		return Messages.getInstance().spectatingPlayer(target);
	}
	
	/**
	 * Creates a new map
	 * 
	 * @param p            Map creator
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String addMap(Player p, String mapName) {
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				return Messages.getInstance().alreadyMapNamed(mapName);
		}

		Region region;
		LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p));
		World selectionWorld = localSession.getSelectionWorld();
		try {
			if (selectionWorld == null) throw new IncompleteRegionException();
			region = localSession.getSelection(selectionWorld);
		} catch (IncompleteRegionException ex) {
			p.sendMessage(ChatColor.RED + "Please make a region selection first.");
			return ChatColor.RED + "Could not save the map as a schematic!";
		}

		BlockVector3 min = region.getMinimumPoint();
		BlockVector3 max = region.getMaximumPoint();

		double minY = p.getLocation().getY();
		GameMap map = new GameMap(mapName, GameType.SPLEEF, p.getWorld(), min, max, p.getLocation(), minY, p.getLocation(), p.getLocation());
		maps.add(map);
		Game game = new Game(map);
		this.games.add(game);
		
		if(StorageManager.getInstance().getStorage().saveMap(map))
			return Messages.getInstance().savedMapNamed(mapName);
		else
			return Messages.getInstance().mapAddedCouldntSave(mapName);
	}
	
	/**
	 * Deletes a new map
	 * 
	 * @param p            Map deleted
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String removeMap(Player p, String mapName) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}
		
		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);
		
		Game currentGame = null;
		for(Game game : games) {
			if(game.getMap().getName().equalsIgnoreCase(mapName))
				currentGame = game;
		}
		
		if(currentGame != null) {
			currentGame.getGs().finishGame();
			currentGame.getGs().endGame();
			games.remove(currentGame);
		}
		
		maps.remove(currentMap);
		
		if(StorageManager.getInstance().getStorage().removeMap(mapName))
			return Messages.getInstance().removedMapNamed(mapName);
		else
			return Messages.getInstance().mapRemovedCouldntSave(mapName);
	}

	/**
	 * Sets the GameType of a map
	 *
	 * @param mapName      The map to set the GameType of
	 * @param gametype     The GameType that was set
	 * @return String      Message of success
	 */
	public String setGameType(String mapName, GameType gametype) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}

		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);

		currentMap.setGameType(gametype);

		if(StorageManager.getInstance().getStorage().saveMap(currentMap))
			return ChatColor.GREEN + "Gametype of game " + currentMap.getName() + " was set to " + ChatColor.BLUE + gametype.name();
		else
			return Messages.getInstance().setMapSpawnCouldntSave(mapName);
	}

	/**
	 * Sets the spawn location of a map
	 * 
	 * @param p            Location setter
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String setMapSpawn(Player p, String mapName) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}
		
		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);
		
		currentMap.setSpawn(p.getLocation());
		
		if(StorageManager.getInstance().getStorage().saveMap(currentMap))
			return Messages.getInstance().setMapSpawn(mapName);
		else
			return Messages.getInstance().setMapSpawnCouldntSave(mapName);
	}
	
	/**
	 * Sets a map minimum Y
	 * 
	 * @param p            Map creator
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String setMapMiny(Player p, String mapName) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}
		
		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);
		
		currentMap.setminY(p.getLocation().getY());
		
		if(StorageManager.getInstance().getStorage().saveMap(currentMap))
			return Messages.getInstance().setMapMinY(mapName);
		else
			return Messages.getInstance().setMapMinYCouldntSave(mapName);
	}

	/**
	 * Sets the lose location of a map
	 *
	 * @param p            Location setter
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String setLoseLoc(Player p, String mapName) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}

		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);

		currentMap.setLoseLoc(p.getLocation());

		if(StorageManager.getInstance().getStorage().saveMap(currentMap))
			return ChatColor.GREEN + "Saved lose location to your current location.";
		else
			return ChatColor.RED + "Could not save lose location to your current location.";
	}

	/**
	 * Sets the win location of a map
	 *
	 * @param p            Location setter
	 * @param mapName      Name of the map
	 * @return String      Message of success/failure
	 */
	public String setWinLoc(Player p, String mapName) {
		GameMap currentMap = null;
		for(GameMap map : maps) {
			if(map.getName().equalsIgnoreCase(mapName))
				currentMap = map;
		}

		if(currentMap == null)
			return Messages.getInstance().couldntFindMapNamed(mapName);

		currentMap.setWinLoc(p.getLocation());

		if(StorageManager.getInstance().getStorage().saveMap(currentMap))
			return ChatColor.GREEN + "Saved win location to your current location.";
		else
			return ChatColor.RED + "Could not save win location to your current location.";
	}

}
