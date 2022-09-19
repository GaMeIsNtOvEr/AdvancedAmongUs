 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.Camera;
 import com.nktfh100.AmongUs.info.FakeBlock;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.List;
 import org.bukkit.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.configuration.ConfigurationSection;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.block.BlockBreakEvent;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class BlockBreak
   implements Listener
 {
   @EventHandler
   public void onBlockBreak(BlockBreakEvent ev) {
     final Player player = ev.getPlayer();
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
     if (pInfo.getIsIngame().booleanValue()) {
       ev.setCancelled(true); return;
     } 
     if ((player.hasPermission("amongus.admin") || player.hasPermission("amongus.admin.setup")) && player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null) {
       String displayName = ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName());
       
       if (displayName.startsWith("Fake Blocks Wand: ")) {
         if (ev.getBlock().getType() == Material.AIR) {
           return;
         }
         String name_ = displayName.split(": ")[1];
         String[] name_1 = name_.split("[|]");
         Arena arena = Main.getArenaManager().getArenaByName(name_1[0]);
         if (arena != null && arena.getCamerasManager().getCameras().size() > 0) {
           final Camera cam = arena.getCamerasManager().getCameras().get(Integer.valueOf(name_1[1]));
           if (cam != null) {
             Location loc = ev.getBlock().getLocation();
             ConfigurationSection camSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName() + ".cameras.cams." + cam.getConfigKey());
             List<String> fakeBlocks_ = camSec.getStringList("fakeblocks");
             String locStr = String.valueOf(ev.getBlock().getType().toString()) + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
             if (fakeBlocks_.contains(locStr)) {
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "This block is already a fake block" + Utils.locationToStringB(loc));
             } else {
               cam.addFakeBlock(loc, Material.AIR, ev.getBlock().getType(), null);
               fakeBlocks_.add(locStr);
               
               camSec.set("fakeblocks", fakeBlocks_);
               Main.getPlugin().saveConfig();
               
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully added fake block (" + ev.getBlock().getType().toString() + ") to cam " + cam.getId() + " " + 
                   Utils.locationToStringB(loc));
             } 
           } 
         } 
       } else if (displayName.startsWith("Remove Fake Blocks Wand: ")) {
         String name_ = displayName.split(": ")[1];
         String[] name_1 = name_.split("[|]");
         Arena arena = Main.getArenaManager().getArenaByName(name_1[0]);
         if (arena != null && arena.getCamerasManager().getCameras().size() > 0) {
           final Camera cam = arena.getCamerasManager().getCameras().get(Integer.valueOf(name_1[1]));
           if (cam != null) {
             Location loc = ev.getBlock().getLocation();
             ConfigurationSection camSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName() + ".cameras.cams." + cam.getConfigKey());
             List<String> fakeBlocks_ = camSec.getStringList("fakeblocks");
             
             String out_ = null;
             for (String fakeBlockStr : fakeBlocks_) {
               String[] splited_ = fakeBlockStr.split(",");
               if (splited_[1].equals(String.valueOf(loc.getBlockX())) && 
                 splited_[2].equals(String.valueOf(loc.getBlockY())) && 
                 splited_[3].equals(String.valueOf(loc.getBlockZ()))) {
                 out_ = fakeBlockStr;
                 
                 break;
               } 
             } 
             
             if (out_ == null) {
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "This block is not a fake block " + Utils.locationToStringB(loc) + " cam: " + cam.getId());
               return;
             } 
             for (FakeBlock fb : cam.getFakeBlocks()) {
               if (fb.getLoc().getBlockX() == loc.getBlockX() && 
                 fb.getLoc().getBlockY() == loc.getBlockY() && 
                 fb.getLoc().getBlockZ() == loc.getBlockZ()) {
                 cam.getFakeBlocks().remove(fb);
                 
                 break;
               } 
             } 
             
             fakeBlocks_.remove(out_);
             
             camSec.set("fakeblocks", fakeBlocks_);
             Main.getPlugin().saveConfig();
             
             player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully removed fake block from cam " + cam.getId() + " " + Utils.locationToStringB(loc));
           }
         
         }
       
       }
       else if (displayName.startsWith("Fake Air Blocks Wand: ")) {
         if (ev.getBlock().getType() == Material.AIR) {
           return;
         }
         String name_ = displayName.split(": ")[1];
         String[] name_1 = name_.split("[|]");
         Arena arena = Main.getArenaManager().getArenaByName(name_1[0]);
         if (arena != null && arena.getCamerasManager().getCameras().size() > 0) {
           final Camera cam = arena.getCamerasManager().getCameras().get(Integer.valueOf(name_1[1]));
           if (cam != null) {
             ev.setCancelled(true);
             Location loc = ev.getBlock().getLocation();
             ConfigurationSection camSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName() + ".cameras.cams." + cam.getConfigKey());
             List<String> fakeAirBlocks_ = camSec.getStringList("fakeairblocks");
             String locStr = String.valueOf(loc.getBlockX()) + "," + loc.getBlockY() + "," + loc.getBlockZ();
             if (fakeAirBlocks_.contains(locStr)) {
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "This block is already a fake air block " + Utils.locationToStringB(loc));
             } else {
               cam.addFakeAirBlock(loc);
               fakeAirBlocks_.add(locStr);
               
               camSec.set("fakeairblocks", fakeAirBlocks_);
               Main.getPlugin().saveConfig();
               
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully added fake air block (" + ev.getBlock().getType().toString() + ") to cam " + cam.getId() + " " + 
                   Utils.locationToStringB(loc));
             } 
             (new BukkitRunnable()
               {
                 public void run() {
                   cam.showFakeAirBlocks(player);
                 }
               }).runTaskLater(Main.getPlugin(), 1L);
           } 
         } 
       } else if (displayName.startsWith("Remove Fake Air Blocks Wand: ")) {
         String name_ = displayName.split(": ")[1];
         String[] name_1 = name_.split("[|]");
         Arena arena = Main.getArenaManager().getArenaByName(name_1[0]);
         if (arena != null && arena.getCamerasManager().getCameras().size() > 0) {
           final Camera cam = arena.getCamerasManager().getCameras().get(Integer.valueOf(name_1[1]));
           if (cam != null) {
             ev.setCancelled(true);
             Location loc = ev.getBlock().getLocation();
             ConfigurationSection camSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName() + ".cameras.cams." + cam.getConfigKey());
             List<String> fakeAirBlocks_ = camSec.getStringList("fakeairblocks");
             
             String locStr = String.valueOf(loc.getBlockX()) + "," + loc.getBlockY() + "," + loc.getBlockZ();
             if (fakeAirBlocks_.contains(locStr)) {
               
               fakeAirBlocks_.remove(locStr);
               
               camSec.set("fakeairblocks", fakeAirBlocks_);
               Main.getPlugin().saveConfig();
               
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully removed fake air block from cam " + cam.getId() + " " + Utils.locationToStringB(loc));
             } else {
               player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "This block is not a fake air block " + Utils.locationToStringB(loc) + " cam: " + cam.getId());
             } 
           } 
         } 
       } else if (displayName.startsWith("Prime Shields Blocks Wand: ")) {
         String name_ = displayName.split(": ")[1];
         Arena arena = Main.getArenaManager().getArenaByName(name_);
         if (arena == null) {
           return;
         }
         if (arena.getPrimeShieldsBlocks().contains(ev.getBlock())) {
           arena.getPrimeShieldsBlocks().remove(ev.getBlock());
           
           ConfigurationSection arenaSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName());
           String locStr = String.valueOf(ev.getBlock().getWorld().getName()) + "," + ev.getBlock().getX() + "," + ev.getBlock().getY() + "," + ev.getBlock().getZ();
           List<String> blocks_ = arenaSec.getStringList("primeshieldsblocks");
           blocks_.remove(locStr);
           arenaSec.set("primeshieldsblocks", blocks_);
           Main.getPlugin().saveConfig();
           player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully removed prime shields block " + Utils.locationToStringB(ev.getBlock().getLocation()));
           return;
         } 
         ev.setCancelled(true);
         ev.getBlock().setType(Material.REDSTONE_LAMP);
         
         arena.getPrimeShieldsBlocks().add(ev.getBlock());
         
         ConfigurationSection arenaSec = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName());
         String locStr = String.valueOf(ev.getBlock().getWorld().getName()) + "," + ev.getBlock().getX() + "," + ev.getBlock().getY() + "," + ev.getBlock().getZ();
         List<String> blocks_ = arenaSec.getStringList("primeshieldsblocks");
         blocks_.add(locStr);
         arenaSec.set("primeshieldsblocks", blocks_);
         Main.getPlugin().saveConfig();
         player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Successfully added prime shields block " + Utils.locationToStringB(ev.getBlock().getLocation()));
         return;
       } 
     } 
   }
 }


