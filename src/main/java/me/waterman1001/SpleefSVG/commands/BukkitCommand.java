package me.waterman1001.SpleefSVG.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract interface BukkitCommand {
	
	abstract void setup(JavaPlugin plugin);
	abstract BukkitSubCommand getSubCommand(String name);
	abstract boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args);
	
}