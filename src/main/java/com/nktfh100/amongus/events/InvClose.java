 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.inventory.SabotageInvHolder;
 import com.nktfh100.AmongUs.inventory.TaskInvHolder;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.inventory.InventoryCloseEvent;
 import org.bukkit.inventory.InventoryHolder;
 
 public class InvClose
   implements Listener
 {
   @EventHandler
   public void onInvClose(InventoryCloseEvent ev) {
     Player player = (Player)ev.getPlayer();
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
     if (pInfo != null && 
       pInfo.getIsIngame().booleanValue()) {
       InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
       if (holder instanceof com.nktfh100.AmongUs.inventory.CustomHolder)
         if (holder instanceof TaskInvHolder) {
           TaskInvHolder taskInvHolder = (TaskInvHolder)holder;
           taskInvHolder.invClosed();
         } else if (holder instanceof SabotageInvHolder) {
           SabotageInvHolder saboInvHolder = (SabotageInvHolder)holder;
           saboInvHolder.invClosed(player);
           if (pInfo.getIsImposter().booleanValue())
             pInfo.setKillCoolDownPaused(Boolean.valueOf(false)); 
         }  
     } 
   }
 }


