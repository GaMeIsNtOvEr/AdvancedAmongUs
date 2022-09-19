 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.info.Vent;
 import com.nktfh100.AmongUs.info.VentGroup;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.Location;
 import org.bukkit.World;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerToggleSneakEvent;
 
 public class PlayerSneak
   implements Listener {
   @EventHandler
   public void onSneak(PlayerToggleSneakEvent ev) {
     Player player = ev.getPlayer();
     if (ev.isSneaking()) {
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo == null) {
         return;
       }
       
       if (pInfo.getIsIngame().booleanValue())
         if (pInfo.getIsInVent().booleanValue()) {
           pInfo.getArena().getVentsManager().playerLeaveVent(pInfo, Boolean.valueOf(false), Boolean.valueOf(false));
         } else if (pInfo.getIsInCameras().booleanValue()) {
           pInfo.getArena().getCamerasManager().playerLeaveCameras(pInfo, Boolean.valueOf(false));
         } else if (Main.getConfigManager().getSneakToVent().booleanValue() && pInfo.getIsImposter().booleanValue() && !pInfo.getIsInCameras().booleanValue() && !pInfo.getIsInVent().booleanValue()) {
           Arena arena = pInfo.getArena();
           Location pLoc = player.getLocation();
           World pWorld = player.getWorld();
           for (VentGroup vg : arena.getVentsManager().getVentGroups()) {
             for (Vent v : vg.getVents()) {
               if (v.getLoc().getWorld() == pWorld && 
                 pLoc.distance(v.getLoc()) <= 3.0D)
                 arena.getVentsManager().ventHoloClick(pInfo, vg.getId(), v.getId()); 
             } 
           } 
         }  
     } 
   }
 }


