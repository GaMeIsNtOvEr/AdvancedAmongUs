 package com.nktfh100.amongus.managers;
 
 import com.nktfh100.AmongUs.info.SoundInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Packets;
 import java.io.File;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Set;
 import java.util.logging.Level;
 import org.bukkit.Bukkit;
 import org.bukkit.Location;
 import org.bukkit.Sound;
 import org.bukkit.configuration.ConfigurationSection;
 import org.bukkit.configuration.file.YamlConfiguration;
 import org.bukkit.entity.Player;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 
 public class SoundsManager
 {
   private HashMap<String, ArrayList<SoundInfo>> sounds = new HashMap<>();
   
   public void loadSounds() {
     File soundsConfigFIle = new File(Main.getPlugin().getDataFolder(), "sounds.yml");
     if (!soundsConfigFIle.exists()) {
       try {
         Main.getPlugin().saveResource("sounds.yml", false);
       } catch (Exception e) {
         e.printStackTrace();
       } 
     }
     
     YamlConfiguration soundsConfig = YamlConfiguration.loadConfiguration(soundsConfigFIle);
     try {
       this.sounds = new HashMap<>();
       
       ConfigurationSection soundsSC = soundsConfig.getConfigurationSection("sounds");
       Set<String> soundsKeys = soundsSC.getKeys(false);
       for (String key : soundsKeys) {
         try {
           this.sounds.put(key, new ArrayList<>());
           if (soundsSC.getString(key).equalsIgnoreCase("none"))
             continue;  byte b; int i;
           String[] arrayOfString;
           for (i = (arrayOfString = soundsSC.getString(key).split("-")).length, b = 0; b < i; ) { float pitch, volume; String soundInfoStr = arrayOfString[b];
             String[] soundData = soundInfoStr.split(",");
             Sound soundType = Sound.valueOf(soundData[0].toUpperCase());
             
             float volume2 = -1.0F;
             if (soundData[1].startsWith("@")) {
               String[] volumeData = soundData[1].replace("@", "").split("/");
               volume = Float.valueOf(volumeData[0]).floatValue();
               volume2 = Float.valueOf(volumeData[1]).floatValue();
             } else {
               volume = Float.parseFloat(soundData[1]);
             } 
 
             
             float pitch2 = -1.0F;
             if (soundData[2].startsWith("@")) {
               String[] pitchData = soundData[2].replace("@", "").split("/");
               pitch = Float.valueOf(pitchData[0]).floatValue();
               pitch2 = Float.valueOf(pitchData[1]).floatValue();
             } else {
               pitch = Float.parseFloat(soundData[2]);
             } 
             
             int delay = Integer.parseInt(soundData[3]);
             int delay2 = -1;
             if (soundData[3].startsWith("@")) {
               String[] delayData = soundData[3].replace("@", "").split("/");
               delay = Integer.valueOf(delayData[0]);
               delay2 = Integer.valueOf(delayData[1]);
             } else {
               delay = Integer.parseInt(soundData[3]);
             } 
             
             SoundInfo soundInfo = new SoundInfo(soundType, volume, pitch, delay, volume2, pitch2, delay2);
             
             ((ArrayList<SoundInfo>)this.sounds.get(key)).add(soundInfo);
             b++; }
         
         } catch (Exception e) {
           e.printStackTrace();
           Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with your sounds.yml file! (" + key + ")");
           Main.getPlugin().getPluginLoader().disablePlugin(Main.getPlugin());
         } 
       } 
     } catch (Exception e) {
       e.printStackTrace();
       Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with your sounds.yml file!");
       Main.getPlugin().getPluginLoader().disablePlugin(Main.getPlugin());
     } 
   }
   
   public void playSound(String key, final Player player, final Location loc) {
     for (SoundInfo soundInfo : getSound(key)) {
       if (soundInfo.getDelay() > 0) {
         (new BukkitRunnable()
           {
             public void run() {
               Packets.sendPacket(player, soundInfo.getPacket(loc));
             }
           }).runTaskLater(Main.getPlugin(), soundInfo.getDelay()); continue;
       } 
       Packets.sendPacket(player, soundInfo.getPacket(loc));
     } 
   }
 
   
   public ArrayList<SoundInfo> getSound(String key) {
     ArrayList<SoundInfo> out = this.sounds.get(key);
     if (out == null) {
       Main.getPlugin().getLogger().warning("Sound '" + key + "' is missing from your sounds.yml file!");
       return new ArrayList<>();
     } 
     return out;
   }
   
   public void delete() {
     this.sounds.clear();
     this.sounds = null;
   }
 }


