package me.waterman1001.SpleefSVG.modules;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.waterman1001.SpleefSVG.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;

public class GameMap {

	private static File schematicFile = null;
	private String name;
	private GameType gametype;
	private Location spawn;
	private Location loseloc;
	private Location winloc;
	private World schematicWorld;
	private BlockVector3 min;
	private BlockVector3 max;
    private double minY;
	
	public GameMap(String name, GameType gametype, World schematicWorld, BlockVector3 min, BlockVector3 max, Location spawn, double minY, Location loseloc, Location winloc) {
		this.name = name;
		this.gametype = gametype;
        this.setSpawn(spawn);
        this.schematicWorld = schematicWorld;
        this.min = min;
		this.max = max;
		this.minY = minY;
		this.loseloc = loseloc;
		this.winloc = winloc;

		schematicFile = new File(Main.getInstance().getDataFolder(), name.toLowerCase() + ".schem");
		if(!schematicFile.exists()) {
			saveSpleefMapSchem();
		} else {
			loadMap();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GameType getGameType() {
		return gametype;
	}

	public void setGameType(GameType gametype) {
		this.gametype = gametype;
	}

    public World getSchematicWorld() {
        return schematicWorld;
    }

	public double getminY() {
		return minY;
	}

	public void setminY(double minY) {
		this.minY = minY;
	}

	public BlockVector3 getMinPoint() {
		return min;
	}

	public BlockVector3 getMaxPoint() {
		return max;
	}
	
	public Location getSpawn() {
		return spawn;
	}

	public Location getLoseLoc() {
		return loseloc;
	}

	public Location getWinLoc() {
		return winloc;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public void setLoseLoc(Location loseloc) {
		this.loseloc = loseloc;
	}

	public void setWinLoc(Location winloc) {
		this.winloc = winloc;
	}

	public void saveSpleefMapSchem() {
		// Adapt the world to be a WorldEdit type World.
		com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(schematicWorld);
		CuboidRegion region = new CuboidRegion(adaptedWorld, min, max);
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
		EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);

		ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
		forwardExtentCopy.setCopyingEntities(true);

		try {
			Operations.complete(forwardExtentCopy);
		} catch (WorldEditException e) {
			throw new RuntimeException(e);
		}

		File schematic_file = new File(Main.getInstance().getDataFolder(), name.toLowerCase() + ".schem");

		try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(schematic_file))) {
			writer.write(clipboard);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

	/**
	 * Loads the map and pastes it as a schematic
	 */
	public void loadMap() {
		if(schematicFile == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't find the schematic file in the map folder. Please set it up first!");
			return;
		}

		Clipboard clipboard;
		ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
		try (ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
			clipboard = reader.read(); // Load clipboard

			// Load adapted world
			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(schematicWorld);

			EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);
			Operation operation = new ClipboardHolder(clipboard)
					.createPaste(editSession)
					.to(min)
					.ignoreAirBlocks(true)
					.build();
			Operations.complete(operation); // Paste schematic
			editSession.close(); // YOU NEED TO CLOSE THE SESSION AFTER PASTING OTHERWISE NOTHING HAPPENS.
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Loaded and pasted schematic for map!");
		} catch (WorldEditException e) { // If worldedit generated an exception it will go here
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't load the schematic file in the map folder. Please set it up again!");
			Bukkit.getPluginManager().disablePlugin(Main.getInstance());
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Couldn't load the schematic file in the map folder. Please set it up again!");
			Bukkit.getPluginManager().disablePlugin(Main.getInstance());
		}
	}
}
