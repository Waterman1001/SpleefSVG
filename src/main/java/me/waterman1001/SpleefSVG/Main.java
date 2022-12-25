package me.waterman1001.SpleefSVG;

import me.waterman1001.SpleefSVG.commands.spleef.SpleefCommand;
import me.waterman1001.SpleefSVG.listeners.*;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.managers.StorageManager;
import me.waterman1001.SpleefSVG.utils.GlobalVariables;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main instance;
	
	private static GlobalVariables vars;
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		
		setupManagers();
		
		registerCommands();
		registerEvents();
	}

	public void registerCommands() {
		SpleefCommand.getInstance().setup(this);
	}

	public void registerEvents() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new SnowMeltEvent(), this);
		pm.registerEvents(new BlockEvents(), this);
		pm.registerEvents(new PickupDropEvents(), this);
		pm.registerEvents(new StatusEvents(), this);
		pm.registerEvents(new PlayerLoseEvents(), this);
		pm.registerEvents(new DistanceEvents(), this);
		pm.registerEvents(new OnCommandMidGame(), this);
		pm.registerEvents(new OnPlayerWin(), this);
		pm.registerEvents(new OnGameStarts(), this);
		pm.registerEvents(new OnGameEnds(), this);
		pm.registerEvents(new OnMobSpawn(), this);
		pm.registerEvents(new SpleefPlayerUtils(), this);
		pm.registerEvents(new SignChangeEvents(), this);
		pm.registerEvents(new OnSignInteract(), this);
		pm.registerEvents(new OnPlayerJoin(), this);
	}

	public void setupManagers() {
		vars = new GlobalVariables(); // Loading critical variables for the game

		// Delay creating the GameManager to make sure the worlds are loaded first.
		// Otherwise we might get a problem with WorldEdit Adapter when loading the maps.
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> GameManager.getInstance(), 20);
		StorageManager.getInstance();
	}
	
	public static Main getInstance() { return instance; }
	public static GlobalVariables getVars() { return vars; }
}