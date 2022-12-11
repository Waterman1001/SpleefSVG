package me.waterman1001.SpleefSVG.managers;

import me.waterman1001.SpleefSVG.storage.FilesStorage;
import me.waterman1001.SpleefSVG.storage.Storage;

public class StorageManager {

	private static StorageManager instance = new StorageManager();

	public static StorageManager getInstance() {
		return instance;
	}

	private Storage storage;
	
	private StorageManager() {
		setup();
	}

	public void setup() {
		this.storage = new FilesStorage();
	}
	
	public Storage getStorage() {
		return this.storage;
	}
}
