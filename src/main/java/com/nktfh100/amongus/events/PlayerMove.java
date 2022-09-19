 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.DeadBody;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.managers.PlayersManager;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.Location;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerMoveEvent;
 
 public class PlayerMove
   implements Listener
 {
   PlayersManager playersManager = Main.getPlayersManager();
 
   
   @EventHandler
   public void move(PlayerMoveEvent ev) {
     PlayerInfo pInfo = this.playersManager.getPlayerInfo(ev.getPlayer());
     if (pInfo != null && pInfo.getIsIngame().booleanValue()) {
       Arena arena = pInfo.getArena();
       if (pInfo.getIsInCameras().booleanValue()) {
         ev.getPlayer().setAllowFlight(true);
         ev.getPlayer().setFlying(true);
         ev.getPlayer().teleport(pInfo.getActiveCamera().getViewLoc());
         return;
       } 
       if (arena.getIsInMeeting().booleanValue() || pInfo.getIsInVent().booleanValue()) {
         if (Utils.hasChangedBlockCoordinates(ev.getFrom(), ev.getTo())) {
           Location from = ev.getFrom();
           double x = from.getBlockX();
           double z = from.getBlockZ();
           
           x += 0.5D;
           z += 0.5D;
           ev.getPlayer().teleport(new Location(from.getWorld(), x, from.getY(), z, from.getYaw(), from.getPitch()));
         } 
       } else if (arena.getGameState() == GameState.RUNNING && !pInfo.isGhost().booleanValue() && !pInfo.getIsInCameras().booleanValue() && !pInfo.getIsInVent().booleanValue()) {
         DeadBody db = arena.getDeadBodiesManager().isCloseToBody(ev.getTo());
         if (db != null) {
           if (!pInfo.getCanReportBody().booleanValue()) {
             pInfo.setCanReportBody(Boolean.valueOf(true), db);
           }
         } else if (pInfo.getCanReportBody().booleanValue()) {
           pInfo.setCanReportBody(Boolean.valueOf(false), null);
         } 
         
         if (arena.getEnableReducedVision().booleanValue() && 
           Utils.hasChangedBlockCoordinates(ev.getFrom(), ev.getTo())) {
           arena.getVisibilityManager().playerMoved(pInfo, ev.getTo());
         }
 
         
         if (arena.getDisableJumping().booleanValue() && 
           ev.getFrom().getY() < ev.getTo().getY() && 
           !ev.getPlayer().isOnGround()) {
           ev.setCancelled(true);
         }
       } 
 
 
       
       if (pInfo.getIsImposter().booleanValue()) {
         pInfo.teleportImposterHolo();
       }
       
       if (arena.getGameState() == GameState.RUNNING && !arena.getIsInMeeting().booleanValue() && !pInfo.getIsInCameras().booleanValue() && !pInfo.getIsInVent().booleanValue())
         pInfo.updateUseItemState(ev.getTo()); 
     } 
   }
 }


