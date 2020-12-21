package me.thejokerdev.verify.listeners;

import me.thejokerdev.verify.Main;
import me.thejokerdev.verify.player.VPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LoginListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        VPlayer var3 = new VPlayer(p.getName(), p.getUniqueId());
        Main.VPlayers.put(p.getName(), var3);
        Main.VPlayersUUID.put(p.getUniqueId(), var3);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        VPlayer vPlayer = Main.getVPlayer(p);
        if (vPlayer != null){
            vPlayer.upload(false);
            Main.VPlayers.remove(p.getName());
            Main.VPlayersUUID.remove(p.getUniqueId());
        }
    }
}
