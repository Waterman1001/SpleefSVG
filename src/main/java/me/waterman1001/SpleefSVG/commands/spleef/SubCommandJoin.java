package me.waterman1001.SpleefSVG.commands.spleef;

import me.waterman1001.SpleefSVG.commands.BukkitSubCommand;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandJoin extends BukkitSubCommand {

	@Override
	public void onCommand(CommandSender cs, String[] args) {
		Player p = (Player)cs;
		
		if(!p.hasPermission("spleefsvg.command.join")) {
			p.sendMessage(Messages.noPermissions);
			return;
		}
		p.sendMessage(GameManager.getInstance().joinPlayer(p));
		return;
	}

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public String getInfo() {
		return ChatColor.RED + "Usage: /spleef <join>";
	}

	@Override
	public String[] aliases() {
		return new String[] {"joingame", "queue"};
	}

}
