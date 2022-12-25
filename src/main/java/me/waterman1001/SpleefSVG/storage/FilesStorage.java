package me.waterman1001.SpleefSVG.storage;

import com.sk89q.worldedit.math.BlockVector3;
import me.waterman1001.SpleefSVG.Main;
import me.waterman1001.SpleefSVG.modules.GameMap;
import me.waterman1001.SpleefSVG.modules.GameType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FilesStorage implements Storage {

	private File file;
	private YamlConfiguration config;

	public FilesStorage() {
		this.file = new File(Main.getInstance().getDataFolder(), "maps.yml");
		
		if(!this.file.exists()) {
			try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
		}
		
		this.config = YamlConfiguration.loadConfiguration(this.file);
	}

	@Override
	public List<GameMap> getMaps() {
		List<GameMap> list = new LinkedList<GameMap>();
		
		int failed = 0;
		
		for(String s : this.config.getConfigurationSection("").getKeys(false)) {
			try {
				World schematicWorld = Bukkit.getWorld(this.config.getString(s + ".world"));

				GameType gametype = GameType.valueOf(this.config.getString(s + ".gametype"));
				boolean anticamping = this.config.getBoolean(s + ".anticamping");

				Location spawn = new Location(
						Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
						this.config.getDouble(s + ".spawn.x"),
						this.config.getDouble(s + ".spawn.y"),
						this.config.getDouble(s + ".spawn.z"),
						this.config.getLong(s + ".spawn.yaw"),
						this.config.getLong(s + ".spawn.pitch"));

				double maxY = this.config.getDouble(s + ".maxY");
				double minY = this.config.getDouble(s + ".minY");

				Location loseloc = new Location(
						Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
						this.config.getDouble(s + ".loseloc.x"),
						this.config.getDouble(s + ".loseloc.y"),
						this.config.getDouble(s + ".loseloc.z"),
						this.config.getLong(s + ".loseloc.yaw"),
						this.config.getLong(s + ".loseloc.pitch"));

				Location winloc = new Location(
						Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
						this.config.getDouble(s + ".winloc.x"),
						this.config.getDouble(s + ".winloc.y"),
						this.config.getDouble(s + ".winloc.z"),
						this.config.getLong(s + ".winloc.yaw"),
						this.config.getLong(s + ".winloc.pitch"));

				String[] minpoint = this.config.getString(s + ".minpoint").split(",");
				String[] maxpoint = this.config.getString(s + ".minpoint").split(",");
				BlockVector3 min = BlockVector3.at(Integer.parseInt(minpoint[0]), Integer.parseInt(minpoint[1]), Integer.parseInt(minpoint[2]));
				BlockVector3 max = BlockVector3.at(Integer.parseInt(maxpoint[0]), Integer.parseInt(maxpoint[1]), Integer.parseInt(maxpoint[2]));

				list.add(new GameMap(s, gametype, anticamping, schematicWorld, min, max, spawn, minY, maxY, loseloc, winloc));
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded map " + s);
			} catch(Exception e) {
				failed++;
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to load " + failed + " maps!");
				e.printStackTrace(); // Make sure an error occurs explaining why map loading failed.
			}
		}
		return list;
	}
	
	public boolean saveMap(GameMap map) {
		this.config.set(map.getName().toLowerCase() + ".gametype", map.getGameType().name());
		this.config.set(map.getName().toLowerCase() + ".anticamping", map.getAntiCamping());
		this.config.set(map.getName().toLowerCase() + ".world", map.getSchematicWorld().getName());
		this.config.set(map.getName().toLowerCase() + ".spawn.world", map.getSpawn().getWorld().getName());
		this.config.set(map.getName().toLowerCase() + ".spawn.x", map.getSpawn().getX());
		this.config.set(map.getName().toLowerCase() + ".spawn.y", map.getSpawn().getY());
		this.config.set(map.getName().toLowerCase() + ".spawn.z", map.getSpawn().getZ());
		this.config.set(map.getName().toLowerCase() + ".spawn.yaw", map.getSpawn().getYaw());
		this.config.set(map.getName().toLowerCase() + ".spawn.pitch", map.getSpawn().getPitch());
		this.config.set(map.getName().toLowerCase() + ".loseloc.x", map.getLoseLoc().getX());
		this.config.set(map.getName().toLowerCase() + ".loseloc.y", map.getLoseLoc().getY());
		this.config.set(map.getName().toLowerCase() + ".loseloc.z", map.getLoseLoc().getZ());
		this.config.set(map.getName().toLowerCase() + ".loseloc.yaw", map.getLoseLoc().getYaw());
		this.config.set(map.getName().toLowerCase() + ".loseloc.pitch", map.getLoseLoc().getPitch());
		this.config.set(map.getName().toLowerCase() + ".winloc.x", map.getWinLoc().getX());
		this.config.set(map.getName().toLowerCase() + ".winloc.y", map.getWinLoc().getY());
		this.config.set(map.getName().toLowerCase() + ".winloc.z", map.getWinLoc().getZ());
		this.config.set(map.getName().toLowerCase() + ".winloc.yaw", map.getWinLoc().getYaw());
		this.config.set(map.getName().toLowerCase() + ".winloc.pitch", map.getWinLoc().getPitch());
		this.config.set(map.getName().toLowerCase() + ".minY", map.getminY());
		this.config.set(map.getName().toLowerCase() + ".maxY", map.getmaxY());
		this.config.set(map.getName().toLowerCase() + ".maxpoint", map.getMaxPoint().toParserString());
		this.config.set(map.getName().toLowerCase() + ".minpoint", map.getMinPoint().toParserString());
		
		try {
			this.config.save(this.file);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public boolean removeMap(String mapName) {
		this.config.set(mapName.toLowerCase(), null);
		try {
			this.config.save(this.file);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public GameMap getMap(String mapName) {
		mapName = mapName.toLowerCase();
		for(String s : this.config.getConfigurationSection("").getKeys(false)) {
			if(!s.equalsIgnoreCase(mapName)) continue;

			World schematicWorld = Bukkit.getWorld(this.config.getString(s + ".world"));

			GameType gametype = GameType.valueOf(this.config.getString(s + ".gametype"));
			boolean anticamping = this.config.getBoolean(s + ".anticamping");
				
			Location spawn = new Location(
					Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
					this.config.getDouble(s + ".spawn.x"),
					this.config.getDouble(s + ".spawn.y"),
					this.config.getDouble(s + ".spawn.z"),
					this.config.getLong(s + ".spawn.yaw"),
					this.config.getLong(s + ".spawn.pitch"));

			double minY = this.config.getDouble(s + ".minY");
			double maxY = this.config.getDouble(s + ".maxY");

			Location loseloc = new Location(
					Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
					this.config.getDouble(s + ".loseloc.x"),
					this.config.getDouble(s + ".loseloc.y"),
					this.config.getDouble(s + ".loseloc.z"),
					this.config.getLong(s + ".loseloc.yaw"),
					this.config.getLong(s + ".loseloc.pitch"));

			Location winloc = new Location(
					Bukkit.getWorld(this.config.getString(s + ".spawn.world")),
					this.config.getDouble(s + ".winloc.x"),
					this.config.getDouble(s + ".winloc.y"),
					this.config.getDouble(s + ".winloc.z"),
					this.config.getLong(s + ".winloc.yaw"),
					this.config.getLong(s + ".winloc.pitch"));

			String[] minpoint = this.config.getString(s + ".minpoint").split(",");
			String[] maxpoint = this.config.getString(s + ".minpoint").split(",");
			BlockVector3 min = BlockVector3.at(Integer.parseInt(minpoint[0]), Integer.parseInt(minpoint[1]), Integer.parseInt(minpoint[2]));
			BlockVector3 max = BlockVector3.at(Integer.parseInt(maxpoint[0]), Integer.parseInt(maxpoint[1]), Integer.parseInt(maxpoint[2]));

			return new GameMap(s, gametype, anticamping, schematicWorld, min, max, spawn, minY, maxY, loseloc, winloc);
		}
		return null;
	}
}
