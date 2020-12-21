package me.thejokerdev.verify.player;

import me.thejokerdev.verify.Main;
import me.thejokerdev.verify.database2.DatabaseHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class VPlayer extends VData{
    private String name;
    private UUID UniqueId;
    private boolean verified;

    public VPlayer(String name, UUID uuid){
        this.name = name;
        UniqueId = uuid;
        this.addData("upload_data", false);
        this.load();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        UniqueId = uniqueId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
        this.addData("upload_data", true);
    }

    public void load() {
        this.loadData();
    }

    public void loadData() {
        DatabaseHandler.getDS().loadPlayerData(this);
    }

    public void upload(boolean var1) {
        if (var1) {
            this.uploadData();
        } else {
            this.uploadAsyncData();
        }
    }

    public void uploadAsyncData() {
        if (this.hasData("upload_data") && this.getBoolean("upload_data")) {
            (new BukkitRunnable() {
                public void run() {
                    DatabaseHandler.getDS().uploadPlayerData(VPlayer.this);
                }
            }).runTaskAsynchronously(Main.getPlugin());
        }

    }

    public void uploadData() {
        if (this.hasData("upload_data") && this.getBoolean("upload_data")) {
            DatabaseHandler.getDS().uploadPlayerData(this);
        }

    }
}
