package me.waterman1001.SpleefSVG.commands.spleef;

import me.waterman1001.SpleefSVG.commands.BukkitSubCommand;
import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandQuit extends BukkitSubCommand {

	@Override
	public void onCommand(CommandSender cs, String[] args) {
		Player p = (Player)cs;
		
		if(!p.hasPermission("spleef.command.quit")) {
			p.sendMessage(Messages.noPermissions);
			return;
		}
		p.sendMessage(GameManager.getInstance().quitPlayer(p));
		return;
	}

	@Override
	public String getName() {
		return "quit";
	}

	@Override
	public String getInfo() {
		return ChatColor.RED + "Usage: /spleef <quit>";
	}

	@Override
	public String[] aliases() {
		return new String[] {"leave", "quitqueue", "leavequeue"};
	}

}
