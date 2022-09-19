 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.EntityPickupItemEvent;
 
 
 public class PlayerPickUp
   implements Listener
 {
   @EventHandler
   public void playerPickUp(EntityPickupItemEvent ev) {
     if (ev.getEntity() instanceof Player) {
       Player player = (Player)ev.getEntity();
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo.getIsIngame().booleanValue())
         ev.setCancelled(true); 
     } 
   }
 }


