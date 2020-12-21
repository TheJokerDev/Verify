package me.thejokerdev.verify.database2;

import me.thejokerdev.verify.Main;
import me.thejokerdev.verify.player.VPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DataSource {
    public String TABLE_DATA = Main.getPlugin().getConfig().getString("data.mysql.tablename.data");

    protected DataSource() {
    }

    public abstract void close();

    public void close(AutoCloseable var1) {
        if (var1 != null) {
            try {
                var1.close();
            } catch (Exception var3) {
            }
        }

    }

    public abstract Connection getConnection() throws SQLException;

    public abstract void loadPlayerData(VPlayer var1);

    public abstract void uploadPlayerData(VPlayer var1);

    protected void loadPlayerData(Connection var1, VPlayer var2) {
        PreparedStatement var3 = null;
        ResultSet var4 = null;

        try {
            var3 = var1.prepareStatement(String.format("SELECT * FROM %s WHERE uuid=? OR (uuid IS NULL AND username=?) OR (username=?)", this.TABLE_DATA));
            var3.setString(1, var2.getUniqueId().toString());
            var3.setString(2, var2.getName());
            var3.setString(3, var2.getName());
            var4 = var3.executeQuery();
            if (var4.next()) {
                int var5 = var4.getInt("verified");
                if (var5 == 0){
                    var2.setVerified(false);
                } else if (var5 == 1) {
                    var2.setVerified(true);
                }
                if (var4.getString("uuid") == null || var4.getString("uuid").isEmpty()) {
                    var2.addData("upload_data", true);
                }
            } else {
                var4.close();
                var3.close();
                var3 = var1.prepareStatement(String.format("INSERT INTO %s (uuid,username) VALUES (?,?)", this.TABLE_DATA));
                var3.setString(1, var2.getUniqueId().toString());
                var3.setString(2, var2.getName());
                var3.executeUpdate();
            }
        } catch (SQLException var13) {
            var13.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var3);
        }

    }

    protected void uploadPlayerData(Connection var1, VPlayer var2) {
        PreparedStatement var3 = null;

        try {
            var3 = var1.prepareStatement(String.format("UPDATE %s SET username=?, uuid=?, verified=? OR (uuid IS NULL AND username=?)", this.TABLE_DATA));
            var3.setString(1, var2.getName());
            var3.setString(2, var2.getUniqueId().toString());
            var3.setInt(3, !var2.isVerified() ? 0 : 1);
            var3.setString(4, var2.getName());
            var3.executeUpdate();
            var2.addData("upload_data", false);
        } catch (SQLException var8) {
            var8.printStackTrace();
        } finally {
            this.close(var3);
        }

    }
}
