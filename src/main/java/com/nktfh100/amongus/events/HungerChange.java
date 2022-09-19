 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.FoodLevelChangeEvent;
 
 public class HungerChange
   implements Listener
 {
   @EventHandler
   public void FoodLevelChange(FoodLevelChangeEvent ev) {
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo((Player)ev.getEntity());
     if (pInfo != null && pInfo.getIsIngame().booleanValue())
       ev.setCancelled(true); 
   }
 }


