package me.thejokerdev.verify;

import me.thejokerdev.verify.database2.DatabaseHandler;
import me.thejokerdev.verify.listeners.LoginListener;
import me.thejokerdev.verify.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private static DatabaseHandler databaseHandler;
    public static HashMap<UUID, VPlayer> VPlayersUUID = new HashMap();
    public static HashMap<String, VPlayer> VPlayers = new HashMap();

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(new LoginListener(), this);

        databaseHandler = new DatabaseHandler();
    }

    public static Main getPlugin(){
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("[Verify] Debes ser un jugador para ejecutar comandos");
            return true;
        }
        Player p = (Player)sender;
        VPlayer vPlayer = Main.getVPlayer(p);
        if (vPlayer == null){
            return true;
        }
        if (command.getName().equalsIgnoreCase("verificar")){
            if (!vPlayer.isVerified()) {
                vPlayer.setVerified(true);
                p.sendMessage("§a¡Felicidades! Ahora estás verificado.");
            } else {
                p.sendMessage("§cYa estás verificado.");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("verificado")){
            if (!vPlayer.isVerified()){
                p.sendMessage("§cNo estás verificado.");
            } else {
                p.sendMessage("§aYa estás verificado.");
            }
            return true;
        }
        return true;
    }

    public static void log(String msg){
        if (plugin.getConfig().getBoolean("debug")){
            System.out.println("[Verify] "+msg);
        }
    }
    public static VPlayer getVPlayer(Player var0) {
        if (var0 == null) {
            log("Trying to get null player");
            return null;
        } else {
            return VPlayersUUID.getOrDefault(var0.getUniqueId(), VPlayers.getOrDefault(var0.getName(), null));
        }
    }

    @Override
    public void onDisable() {
        if (databaseHandler != null) {
            log("Disabling all data");
            DatabaseHandler.getDS().close();
        }
    }
}
