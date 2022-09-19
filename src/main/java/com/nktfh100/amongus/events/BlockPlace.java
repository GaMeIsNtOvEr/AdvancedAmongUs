 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.block.BlockPlaceEvent;
 
 public class BlockPlace
   implements Listener {
   @EventHandler(ignoreCancelled = true)
   public void onBlockPlace(BlockPlaceEvent ev) {
     Player player = ev.getPlayer();
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
     if (pInfo.getIsIngame().booleanValue())
       ev.setCancelled(true); 
   }
 }


