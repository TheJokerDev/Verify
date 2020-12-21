package me.thejokerdev.verify.database2.types;

import me.thejokerdev.verify.Main;
import me.thejokerdev.verify.database2.DataSource;
import me.thejokerdev.verify.player.VPlayer;

import java.sql.*;

public class SQLite extends DataSource {
    private static Connection con;

    public SQLite() {
        this.connect();
        this.setup();
    }

    private synchronized void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Main.log("SQLite driver loaded");
            con = DriverManager.getConnection("jdbc:sqlite:"+Main.getPlugin().getDataFolder()+"/Database.db");
            Main.log("SQLite.connect: isClosed = " + con.isClosed());
        } catch (SQLException | ClassNotFoundException var2) {
            var2.printStackTrace();
        }

    }

    private synchronized void setup() {
        Statement var1 = null;

        try {
            var1 = con.createStatement();
            var1.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS '%s' ('id' INTEGER PRIMARY KEY, 'uuid' TEXT(40), 'username' TEXT(32), 'verified' INT(12) DEFAULT '0'); CREATE INDEX IF NOT EXISTS vdata_username ON %s(username); CREATE INDEX IF NOT EXISTS vdata_uuid ON %s(uuid);", this.TABLE_DATA, this.TABLE_DATA, this.TABLE_DATA));
            this.addColumn(this.TABLE_DATA, "uuid", "VARCHAR(255) NOT NULL UNIQUE");
            this.addColumn(this.TABLE_DATA, "username", "VARCHAR(255) DEFAULT NULL");
            this.addColumn(this.TABLE_DATA, "verified", "INT(12) DEFAULT 0");
            var1.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            this.close(var1);
        }

        Main.log("SQLite Setup finished");
    }

    private void addColumn(String var1, String var2, String var3) {
        ResultSet var4 = null;
        Statement var5 = null;

        try {
            var5 = con.createStatement();
            DatabaseMetaData var6 = con.getMetaData();
            var4 = var6.getColumns(null, null, var1, var2);
            if (!var4.next()) {
                var5.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s;", var1, var2, var3));
            }
        } catch (SQLException var10) {
            var10.printStackTrace();
        } finally {
            this.close(var4);
            this.close(var5);
        }

    }

    public synchronized Connection getConnection() {
        return con;
    }

    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

    }

    public synchronized void loadPlayerData(VPlayer var1) {
        this.loadPlayerData(con, var1);
    }

    public void uploadPlayerData(VPlayer var1) {
        this.uploadPlayerData(con, var1);
    }

}
