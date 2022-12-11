package me.waterman1001.SpleefSVG.storage;

import me.waterman1001.SpleefSVG.modules.GameMap;

import java.util.List;

public interface Storage {

	public List<GameMap> getMaps();
	
	public boolean saveMap(GameMap map);
	
	public boolean removeMap(String mapName);
	
	public GameMap getMap(String mapName);
}
