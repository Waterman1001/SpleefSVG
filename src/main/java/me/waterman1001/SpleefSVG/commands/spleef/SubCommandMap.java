package me.waterman1001.SpleefSVG.commands.spleef;

import me.waterman1001.SpleefSVG.commands.BukkitSubCommand;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandMap extends BukkitSubCommand {

	@Override
	public void onCommand(CommandSender cs, String[] args) {
		Player p = (Player)cs;
		
		if(!p.hasPermission("spleef.command.map")) {
			p.sendMessage(Messages.noPermissions);
			return;
		}
		
		if(args.length == 0) {
			p.sendMessage(getInfo());
			return;
		}
		
		String action = args[0];
		
		if(action.equalsIgnoreCase("list") || action.equalsIgnoreCase("maps")) {
			if(!p.hasPermission("spleef.command.map.list")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}
			p.sendMessage(GameManager.getInstance().mapsList());
			return;
		} else if(action.equalsIgnoreCase("create") || action.equalsIgnoreCase("add")) {
			if(!p.hasPermission("spleef.command.map.create")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}
			
			if(args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}
			
			p.sendMessage(GameManager.getInstance().addMap(p, args[1]));
			return;
		} else if(action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("delete")) {
			if(!p.hasPermission("spleef.command.map.remove")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}
			
			if(args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}
			
			p.sendMessage(GameManager.getInstance().removeMap(p, args[1]));
			return;
		} else if(action.equalsIgnoreCase("setminy") || action.equalsIgnoreCase("sety") || action.equalsIgnoreCase("setminimumy") || action.equalsIgnoreCase("miny")) {
			if(!p.hasPermission("spleef.command.map.setminy")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}
			
			if(args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}
			
			p.sendMessage(GameManager.getInstance().setMapMiny(p, args[1]));
			return;
		} else if(action.equalsIgnoreCase("setspawn") || action.equalsIgnoreCase("spawn") || action.equalsIgnoreCase("setmapspawn")) {
			if (!p.hasPermission("spleef.command.map.setspawn")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}

			if (args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}

			p.sendMessage(GameManager.getInstance().setMapSpawn(p, args[1]));
			return;
		} else if(action.equalsIgnoreCase("setloseloc") || action.equalsIgnoreCase("loseloc")) {
			if(!p.hasPermission("spleef.command.map.setloseloc")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}

			if(args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}

			p.sendMessage(GameManager.getInstance().setLoseLoc(p, args[1]));
			return;
		} else if(action.equalsIgnoreCase("setwinloc") || action.equalsIgnoreCase("winloc")) {
			if(!p.hasPermission("spleef.command.map.setwinloc")) {
				p.sendMessage(Messages.noPermissions);
				return;
			}

			if(args.length == 1) {
				p.sendMessage(getInfo());
				return;
			}

			p.sendMessage(GameManager.getInstance().setWinLoc(p, args[1]));
			return;
		} else {
			p.sendMessage(getInfo());
			return;
		}
	}

	@Override
	public String getName() {
		return "map";
	}

	@Override
	public String getInfo() {
		return ChatColor.RED + "Usage: /spleef map <list/create/remove/setminy/setspawn> <mapname>";
	}

	@Override
	public String[] aliases() {
		return new String[] {"map", "arena"};
	}

}
