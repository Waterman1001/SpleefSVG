package me.waterman1001.SpleefSVG.commands.spleef;

import me.waterman1001.SpleefSVG.commands.BukkitSubCommand;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandSpectate extends BukkitSubCommand {

	@Override
	public void onCommand(CommandSender cs, String[] args) {
		Player p = (Player)cs;
		
		if(!p.hasPermission("spleefsvg.command.spectate")) {
			p.sendMessage(Messages.noPermissions);
			return;
		}
		
		if(args.length == 0) {
			p.sendMessage(getInfo());
			return;
		}
		
		Player t = Bukkit.getPlayer(args[0]);
		if(t == null) {
			p.sendMessage(Messages.couldntFindPlayer(args[0]));
			return;
		}
		p.sendMessage(GameManager.getInstance().spectatePlayer(p, t));
		return;
	}

	@Override
	public String getName() {
		return "spectate";
	}

	@Override
	public String getInfo() {
		return ChatColor.RED + "Usage: /spleef <spectate> <player>";
	}

	@Override
	public String[] aliases() {
		return new String[] {"spectate", "spec", "specplayer", "spectateplayer"};
	}

}
