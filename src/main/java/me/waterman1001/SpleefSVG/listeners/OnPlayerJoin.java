package me.waterman1001.SpleefSVG.listeners;

import me.waterman1001.SpleefSVG.managers.GameManager;
import me.waterman1001.SpleefSVG.modules.Game;
import me.waterman1001.SpleefSVG.utils.SpleefPlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        for (Game game : GameManager.getInstance().getGames()) {
            if(game.getPlayersQuitDuringGame().contains(p.getUniqueId())) {
                game.getPlayersQuitDuringGame().remove(p.getUniqueId());
                SpleefPlayerUtils.teleport(p, game.getMap().getLoseLoc());
            }
        }
    }
}
