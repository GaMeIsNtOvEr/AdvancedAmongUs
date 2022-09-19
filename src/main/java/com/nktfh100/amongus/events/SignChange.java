 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.List;
 import org.bukkit.ChatColor;
 import org.bukkit.block.Block;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.block.SignChangeEvent;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class SignChange
   implements Listener
 {
   @EventHandler
   public void onSignChange(SignChangeEvent ev) {
     if ((ev.getPlayer().hasPermission("amongus.admin") || ev.getPlayer().hasPermission("amongus.admin.setup")) && 
       ev.getLine(0) != null && ev.getLine(0).equals("[au]") && 
       ev.getLine(1) != null && !ev.getLine(1).equals("")) {
       Arena arena = Main.getArenaManager().getArenaByName(ev.getLine(1));
       if (arena != null) {
         List<String> signs_ = Main.getConfigManager().getConfig().getStringList("arenas." + arena.getName() + ".signs");
         Block block = ev.getBlock();
         signs_.add(String.valueOf(block.getWorld().getName()) + "," + block.getX() + "," + block.getY() + "," + block.getZ());
         final String arenaName = arena.getName();
         Main.getConfigManager().getConfig().set("arenas." + arena.getName() + ".signs", signs_);
         Main.getPlugin().saveConfig();
         arena.addSign(block.getLocation());
         ev.getPlayer().sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully added sign at " + Utils.locationToStringB(block.getLocation()));
         (new BukkitRunnable()
           {
             public void run() {
               Main.getArenaManager().getArenaByName(arenaName).updateSigns();
             }
           }).runTaskLater(Main.getPlugin(), 20L);
       } 
     } 
   }
 }


