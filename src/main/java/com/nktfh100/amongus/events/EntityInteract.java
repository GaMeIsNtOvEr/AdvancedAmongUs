 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerInteractEntityEvent;
 
 public class EntityInteract
   implements Listener
 {
   @EventHandler
   public void onClick(PlayerInteractEntityEvent ev) {
     Player player = ev.getPlayer();
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
     
     if (pInfo.getIsIngame().booleanValue() && pInfo.getIsInCameras().booleanValue() && pInfo.getIsInCameras().booleanValue() && player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null) {
       ev.setCancelled(true);
       String displayName = player.getItemInHand().getItemMeta().getDisplayName();
       if (displayName.equals(Main.getItemsManager().getItem("cameras_left").getItem().getTitle())) {
         pInfo.getArena().getCamerasManager().playerPrevCamera(pInfo);
       }
       if (displayName.equals(Main.getItemsManager().getItem("cameras_right").getItem().getTitle())) {
         pInfo.getArena().getCamerasManager().playerNextCamera(pInfo);
       } else if (displayName.equals(Main.getItemsManager().getItem("cameras_leave").getItem().getTitle())) {
         pInfo.getArena().getCamerasManager().playerLeaveCameras(pInfo, Boolean.valueOf(false));
       } 
       return;
     } 
   }
 }


