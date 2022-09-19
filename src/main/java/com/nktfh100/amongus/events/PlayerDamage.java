 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.EntityDamageEvent;
 
 public class PlayerDamage
   implements Listener {
   @EventHandler
   public void onDamage(EntityDamageEvent ev) {
     if (ev.getEntity() instanceof Player) {
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo((Player)ev.getEntity());
       if (pInfo == null) {
         return;
       }
       if (pInfo.getIsIngame().booleanValue())
         ev.setCancelled(true); 
     } 
   }
 }


