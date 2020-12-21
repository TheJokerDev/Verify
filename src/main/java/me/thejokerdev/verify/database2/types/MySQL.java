package me.thejokerdev.verify.database2.types;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.thejokerdev.verify.Main;
import me.thejokerdev.verify.database2.DataSource;
import me.thejokerdev.verify.player.VPlayer;

import java.sql.*;

public class MySQL extends DataSource {
    private final int port = Main.getPlugin().getConfig().getInt("data.mysql.port");
    private final String host = Main.getPlugin().getConfig().getString("data.mysql.server");
    private final String database = Main.getPlugin().getConfig().getString("data.mysql.db");
    private final String username = Main.getPlugin().getConfig().getString("data.mysql.user");
    private final String password = Main.getPlugin().getConfig().getString("data.mysql.password");
    private HikariDataSource ds;

    public MySQL() throws SQLException {
        try {
            this.setConnectionArguments();
        } catch (RuntimeException var3) {
            if (var3 instanceof IllegalArgumentException) {
                Main.log("Invalid database arguments! Please check your configuration!");
                Main.log("If this error persists, please report it to the developer!");
                throw new IllegalArgumentException(var3);
            }

            if (var3 instanceof HikariPool.PoolInitializationException) {
                Main.log("Can't initialize database connection! Please check your configuration!");
                Main.log("If this error persists, please report it to the developer!");
                throw new HikariPool.PoolInitializationException(var3);
            }

            Main.log("Can't use the Hikari Connection Pool! Please, report this error to the developer!");
            throw var3;
        }

        this.setupConnection();
    }

    private synchronized void setConnectionArguments() {
        this.ds = new HikariDataSource();
        this.ds.setPoolName("SkyWars MySQL");
        if (Main.getPlugin().getConfig().getBoolean("debug-database")) {
            this.ds.setJdbcUrl("jdbc:log4jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
            this.ds.setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
        } else {
            this.ds.setDriverClassName("com.mysql.jdbc.Driver");
            this.ds.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
        }

        this.ds.addDataSourceProperty("cachePrepStmts", "true");
        this.ds.addDataSourceProperty("prepStmtCacheSize", "250");
        this.ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.ds.addDataSourceProperty("characterEncoding", "utf8");
        this.ds.addDataSourceProperty("encoding", "UTF-8");
        this.ds.addDataSourceProperty("useUnicode", "true");
        this.ds.addDataSourceProperty("useSSL", "false");
        this.ds.setUsername(this.username);
        this.ds.setPassword(this.password);
        this.ds.setMaxLifetime(180000L);
        this.ds.setIdleTimeout(60000L);
        this.ds.setMinimumIdle(1);
        this.ds.setMaximumPoolSize(8);
        Main.log("Connection arguments loaded, Hikari ConnectionPool ready!");
    }

    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    private void setupConnection() throws SQLException {
        Connection var1 = this.getConnection();
        Throwable var2 = null;

        try {
            Statement var3 = var1.createStatement();
            var3.executeUpdate(String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(32) NOT NULL UNIQUE, `uuid` varchar(40) UNIQUE, `verified` INT(12) DEFAULT '0', PRIMARY KEY (id), KEY `vdata_username_idx` (`username`(32))) ENGINE=InnoDB;", this.TABLE_DATA));
            this.addColumn(this.TABLE_DATA, "uuid", "VARCHAR(255) NOT NULL UNIQUE", "id");
            this.addColumn(this.TABLE_DATA, "username", "VARCHAR(255) NOT NULL UNIQUE", "uuid");
            this.addColumn(this.TABLE_DATA, "verified", "INT(12) DEFAULT 0", "username");
            var3.close();
            DatabaseMetaData var5 = var1.getMetaData();
            ResultSet var4 = var5.getIndexInfo(null, null, this.TABLE_DATA, true, false);
            boolean var6 = false;

            while (var4.next()) {
                String var7 = var4.getString("COLUMN_NAME");
                String var8 = var4.getString("INDEX_NAME");
                if (var8 != null && var8.startsWith("username_")) {
                    var3 = var1.createStatement();
                    var3.executeUpdate(String.format("DROP INDEX %s ON %s", var8, this.TABLE_DATA));
                    var3.close();
                }

                if (var7 != null && var8 != null && var7.equalsIgnoreCase("username") && var8.equalsIgnoreCase("username")) {
                    var6 = true;
                }
            }

            var4.close();
            if (!var6) {
                var3 = var1.createStatement();
                var3.executeUpdate(String.format("ALTER TABLE %s ADD UNIQUE (username);", this.TABLE_DATA));
                var3.close();
            }

        } catch (Throwable var16) {
            var2 = var16;
            throw var16;
        } finally {
            if (var1 != null) {
                if (var2 != null) {
                    try {
                        var1.close();
                    } catch (Throwable var15) {
                        var2.addSuppressed(var15);
                    }
                } else {
                    var1.close();
                }
            }

        }

        Main.log("MySQL setup finished");
    }

    private void addColumn(String var1, String var2, String var3, String var4) {
        ResultSet var5 = null;
        Statement var6 = null;

        try {
            Connection var7 = this.getConnection();
            Throwable var8 = null;

            try {
                var6 = var7.createStatement();
                DatabaseMetaData var9 = var7.getMetaData();
                var5 = var9.getColumns(null, null, var1, var2);
                if (!var5.next()) {
                    var6.executeUpdate(String.format("ALTER TABLE %s ADD COLUMN %s %s AFTER %s;", var1, var2, var3, var4));
                }
            } catch (Throwable var26) {
                throw var26;
            } finally {
                if (var7 != null) {
                    if (var8 != null) {
                        try {
                            var7.close();
                        } catch (Throwable var25) {
                            var8.addSuppressed(var25);
                        }
                    } else {
                        var7.close();
                    }
                }

            }
        } catch (SQLException var28) {
            var28.printStackTrace();
        } finally {
            this.close(var5);
            this.close(var6);
        }

    }

    public void close() {
        if (this.ds != null && !this.ds.isClosed()) {
            this.ds.close();
        }

    }

    public void loadPlayerData(VPlayer var1) {
        try {
            Connection var2 = this.getConnection();
            Throwable var3 = null;

            try {
                this.loadPlayerData(var2, var1);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (var2 != null) {
                    if (var3 != null) {
                        try {
                            var2.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        var2.close();
                    }
                }

            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

    }

    public void uploadPlayerData(VPlayer var1) {
        try {
            Connection var2 = this.getConnection();
            Throwable var3 = null;

            try {
                this.uploadPlayerData(var2, var1);
            } catch (Throwable var13) {
                throw var13;
            } finally {
                if (var2 != null) {
                    if (var3 != null) {
                        try {
                            var2.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        var2.close();
                    }
                }

            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

    }

}
