package me.waterman1001.SpleefSVG.commands.spleef;

import me.waterman1001.SpleefSVG.commands.BukkitSubCommand;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpleefCommand implements CommandExecutor {

	private static SpleefCommand instance = new SpleefCommand();
	private SpleefCommand() {}
	public static SpleefCommand getInstance() {
		return instance;
	}
	
	private List<BukkitSubCommand> subcommands = new ArrayList<BukkitSubCommand>();

	public final String spleef = "spleef";
	
    /**
     * Sets the Spleef sub-commands
     */
	public void setup(JavaPlugin plugin) {
		plugin.getCommand(spleef).setExecutor(this);
		this.subcommands.add(new SubCommandJoin());
		this.subcommands.add(new SubCommandQuit());
		this.subcommands.add(new SubCommandSpectate());
		this.subcommands.add(new SubCommandForcestart());
		this.subcommands.add(new SubCommandMap());
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase(spleef)) {
			if(!(cs instanceof Player)) {
				cs.sendMessage(Messages.mustBeAPlayer);
				return true;
			}
			
			Player p = (Player) cs;
			
			if(args.length == 0) {
				if(!p.hasPermission("spleefsvg.commandlist")) {
					p.sendMessage(Messages.noPermissions);
					return true;
				}
				commandList(p);
				return true;
			}
			
			BukkitSubCommand subcommand = getSubCommand(args[0].toLowerCase());
			if(subcommand == null) {
				if(!p.hasPermission("spleefsvg.commandlist")) {
					p.sendMessage(Messages.noPermissions);
					return true;
				}
				commandList(p);
				return true;
			}
			
			String[] arguments = new String[args.length-1];
			for(int i = 0; i < arguments.length; i++)
				arguments[i] = args[i+1];
			
			try {
				subcommand.onCommand(cs, arguments);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return true;
	}
	
	/**
     * @param name      Name of a Sub-Command
     * @return          A {@link me.waterman1001.SpleefSVG.commands.BukkitSubCommand} instance
     */
	public BukkitSubCommand getSubCommand(String name) {
		Iterator<BukkitSubCommand> subcommands = this.subcommands.iterator();
		while(subcommands.hasNext()) {
			BukkitSubCommand sc = subcommands.next();
			if(sc.getName().equalsIgnoreCase(name))
				return sc;
			
			for(String s : sc.aliases())
				if(s.equalsIgnoreCase(name))
					return sc;
		}
		return null;
	}
	
	/**
	 * @param p     The player to print the command list to
	 */
	public void commandList(Player p) {
		p.sendMessage("§e===== §8[§6SpleefSVG§8]§a §e=====");
		if(p.hasPermission("spleefsvg.command.join")) p.sendMessage(ChatColor.GOLD + "/spleef join");
		if(p.hasPermission("spleefsvg.command.quit")) p.sendMessage(ChatColor.GOLD + "/spleef quit");
		if(p.hasPermission("spleefsvg.command.forcestart")) p.sendMessage(ChatColor.GOLD + "/spleef forcestart");
		if(p.hasPermission("spleefsvg.command.spectate")) p.sendMessage(ChatColor.GOLD + "/spleef spectate <player>");
		if(p.hasPermission("spleefsvg.command.map")) p.sendMessage(ChatColor.GOLD + "/spleef map");
		if(p.hasPermission("spleefsvg.command.map.list")) p.sendMessage(ChatColor.GOLD + "/spleef map list");
		if(p.hasPermission("spleefsvg.command.map.create")) p.sendMessage(ChatColor.GOLD + "/spleef map create");
		if(p.hasPermission("spleefsvg.command.map.remove")) p.sendMessage(ChatColor.GOLD + "/spleef map remove");
		if(p.hasPermission("spleefsvg.command.map.setminy")) p.sendMessage(ChatColor.GOLD + "/spleef map setminy");
		if(p.hasPermission("spleefsvg.command.map.setspawn")) p.sendMessage(ChatColor.GOLD + "/spleef map setspawn");
		if(p.hasPermission("spleefsvg.command.map.setloseloc")) p.sendMessage(ChatColor.GOLD + "/spleef map setloseloc");
		if(p.hasPermission("spleefsvg.command.map.setwinloc")) p.sendMessage(ChatColor.GOLD + "/spleef map setwinloc");
		if(p.hasPermission("spleefsvg.command.map.togglegametype")) p.sendMessage(ChatColor.GOLD + "/spleef map togglegametype");
	}
}