 package com.nktfh100.amongus.api;
 
 import com.nktfh100.AmongUs.enums.CosmeticType;
 import com.nktfh100.AmongUs.enums.StatInt;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import java.io.File;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import org.bukkit.configuration.file.YamlConfiguration;
 import org.bukkit.entity.Player;
 
 
 
 
 public class AmongUsApi
 {
   public static Arena getArena(String arena) { return Main.getArenaManager().getArenaByName(arena); }
 
 
   
   public static PlayerInfo getPlayerInfo(Player player) { return Main.getPlayersManager().getPlayerInfo(player); }
 
 
 
 
 
 
 
   
   public static HashMap<String, Integer> getPlayerStats(Player player) {
     if (player.isOnline()) {
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo == null || pInfo.getStatsManager() == null) {
         return new HashMap<>();
       }
       HashMap<String, Integer> out = new HashMap<>(); byte b; int i; StatInt[] arrayOfStatInt;
       for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i; ) { StatInt statIntE = arrayOfStatInt[b];
         out.put(statIntE.getName(), (Integer)pInfo.getStatsManager().getStatsInt().get(statIntE)); b++; }
       
       return out;
     } 
     HashMap<String, Integer> out = new HashMap<>();
     if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
       Connection connection = Main.getConfigManager().getMysql_connection();
       try {
         PreparedStatement ps = connection.prepareStatement("SELECT * FROM stats WHERE UUID = ?");
         ps.setString(1, player.getUniqueId().toString());
         ResultSet rs = ps.executeQuery();
         rs.next(); byte b; int i; StatInt[] arrayOfStatInt;
         for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i; ) { StatInt statIntE = arrayOfStatInt[b];
           out.put(statIntE.getName(), Integer.valueOf(rs.getInt(statIntE.getName()))); b++; }
         
         rs.close();
         ps.close();
       } catch (SQLException e) {
         e.printStackTrace();
       } 
       return out;
     } 
     File statsFile = new File(Main.getPlugin().getDataFolder() + File.separator + "stats", String.valueOf(player.getUniqueId().toString()) + ".yml");
     if (statsFile.exists()) {
       YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile); byte b; int i; StatInt[] arrayOfStatInt;
       for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i; ) { StatInt statIntE = arrayOfStatInt[b];
         out.put(statIntE.getName(), Integer.valueOf(statsConfig.getInt(statIntE.getName(), 0))); b++; }
     
     } 
     return out;
   }
 
 
 
 
 
 
 
 
   
   public static ArrayList<String> getUnlockedCosmetics(Player player) {
     if (player.isOnline()) {
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo == null || pInfo.getStatsManager() == null) {
         return new ArrayList<>();
       }
       ArrayList<String> out = new ArrayList<>();
       for (String cosmetic_ : pInfo.getStatsManager().getUnlockedCosmetics()) {
         out.add(cosmetic_);
       }
       return out;
     } 
     ArrayList<String> out = new ArrayList<>();
     if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
       Connection connection = Main.getConfigManager().getMysql_connection();
       try {
         PreparedStatement ps = connection.prepareStatement("SELECT * FROM unlocked_cosmetics WHERE UUID = ?");
         ps.setString(1, player.getUniqueId().toString());
         ResultSet rs = ps.executeQuery();
         while (rs.next()) {
           out.add(rs.getString("cosmetic"));
         }
         rs.close();
         ps.close();
       } catch (SQLException e) {
         e.printStackTrace();
       } 
     } else {
       File statsFile = new File(Main.getPlugin().getDataFolder() + File.separator + "stats", String.valueOf(player.getUniqueId().toString()) + ".yml");
       if (statsFile.exists()) {
         YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
         for (String cosmetic_ : statsConfig.getStringList("unlocked_cosmetics")) {
           out.add(cosmetic_);
         }
       } 
     } 
     return out;
   }
 
 
 
 
 
 
 
 
   
   public static String getSelectedCosmetic(Player player, CosmeticType type) {
     if (player.isOnline()) {
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo == null || pInfo.getStatsManager() == null) {
         return Main.getCosmeticsManager().getDefaultCosmetic(type);
       }
       return pInfo.getStatsManager().getSelectedCosmetic(type);
     } 
     if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
       Connection connection = Main.getConfigManager().getMysql_connection();
       try {
         PreparedStatement ps = connection.prepareStatement("SELECT * FROM selected_cosmetics WHERE UUID=? AND type=?");
         ps.setString(1, player.getUniqueId().toString());
         ps.setString(2, type.getName());
         ResultSet rs = ps.executeQuery();
         String out = Main.getCosmeticsManager().getDefaultCosmetic(type);
         if (rs.next()) {
           out = rs.getString("selected");
         }
         rs.close();
         ps.close();
         return out;
       } catch (SQLException e) {
         e.printStackTrace();
       } 
     } else {
       File statsFile = new File(Main.getPlugin().getDataFolder() + File.separator + "stats", String.valueOf(player.getUniqueId().toString()) + ".yml");
       if (statsFile.exists()) {
         YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
         return statsConfig.getString(type.getName(), Main.getCosmeticsManager().getDefaultCosmetic(type));
       } 
     } 
     
     return Main.getCosmeticsManager().getDefaultCosmetic(type);
   }
 }


