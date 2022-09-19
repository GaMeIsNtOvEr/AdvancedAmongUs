 package com.nktfh100.amongus.managers;
 
 import com.comphenix.protocol.PacketType;
 import com.comphenix.protocol.events.PacketContainer;
 import com.comphenix.protocol.wrappers.WrappedDataWatcher;
 import com.gmail.filoghost.holographicdisplays.api.Hologram;
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.DeadBody;
 import com.nktfh100.AmongUs.info.FakeArmorStand;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Packets;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.Location;
 import org.bukkit.entity.Entity;
 import org.bukkit.entity.Player;
 
 
 
 
 public class VisibilityManager
 {
   Arena arena;
   
   public VisibilityManager(Arena arena) { this.arena = arena; }
 
   
   public void resetBodyVis(DeadBody db) {
     db.getPlayersShownTo().clear();
     for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
       checkPlayerBodyVis(pInfo, db);
     }
   }
 
   
   public void checkPlayerBodyVis(PlayerInfo pInfo, DeadBody db) {
     Player player = pInfo.getPlayer();
     Boolean isShown = db.isShownTo(player);
     if (!pInfo.isGhost().booleanValue() && this.arena.getEnableReducedVision().booleanValue()) {
       Integer state_ = Utils.isInsideCircle(player.getLocation(), Double.valueOf(pInfo.getVision()), db.getLocation());
       if (state_ == 2 && isShown.booleanValue()) {
         db.hideFrom(player, Boolean.valueOf(true));
       } else if (state_ < 2 && !isShown.booleanValue()) {
         db.showTo(pInfo, Boolean.valueOf(true));
       } 
     } else if (!isShown.booleanValue()) {
       db.showTo(pInfo, Boolean.valueOf(true));
     } 
   }
   
   public void checkPlayerHoloVis(PlayerInfo pInfo, Hologram holo) {
     if (holo == null || holo.isDeleted()) {
       return;
     }
     if (this.arena.getEnableReducedVision().booleanValue()) {
       Integer state = Utils.isInsideCircle(pInfo.getPlayer().getLocation(), Double.valueOf(pInfo.getVision()), holo.getLocation());
       Boolean canSee = Boolean.valueOf(holo.getVisibilityManager().isVisibleTo(pInfo.getPlayer()));
       if (canSee.booleanValue() && state == 2) {
         holo.getVisibilityManager().hideTo(pInfo.getPlayer());
       } else if (!canSee.booleanValue() && state != 2) {
         holo.getVisibilityManager().showTo(pInfo.getPlayer());
       }
     
     } else if (!holo.getVisibilityManager().isVisibleTo(pInfo.getPlayer())) {
       holo.getVisibilityManager().showTo(pInfo.getPlayer());
     } 
   }
 
   
   public void playerMoved(PlayerInfo pInfo, Location newLoc) {
     Boolean areLightsOut = Boolean.valueOf((this.arena.getSabotageManager().getIsSabotageActive().booleanValue() && this.arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.LIGHTS));
     
     if (this.arena.getEnableReducedVision().booleanValue() && 
       !pInfo.isGhost().booleanValue() && !this.arena.getIsInMeeting().booleanValue() && !pInfo.getIsInCameras().booleanValue()) {
       
       if (!areLightsOut.booleanValue() || (pInfo.getIsImposter().booleanValue() && areLightsOut.booleanValue())) {
         pInfo.updateVisionBlocks(newLoc);
       }
       
       updateVisionOf(pInfo);
     } 
     
     for (DeadBody db : this.arena.getDeadBodiesManager().getBodies()) {
       checkPlayerBodyVis(pInfo, db);
     }
   }
 
   
   public void playerMoved(PlayerInfo pInfo) { playerMoved(pInfo, pInfo.getPlayer().getLocation()); }
 
   
   private void updateVisionOf(PlayerInfo pInfo) {
     if (pInfo != null && pInfo.getIsIngame().booleanValue() && pInfo.getArena().getGameState() == GameState.RUNNING && !pInfo.getArena().getIsInMeeting().booleanValue()) {
       Player player = pInfo.getPlayer();
 
       
       if (this.arena.getHideHologramsOutOfView().booleanValue()) {
         if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue()) {
           for (Hologram holo : this.arena.getSabotageManager().getActiveSabotage().getHolos()) {
             checkPlayerHoloVis(pInfo, holo);
           }
         }
         if (pInfo.getIsImposter().booleanValue() && !pInfo.isGhost().booleanValue()) {
           for (Hologram holo : this.arena.getVentsManager().getHolos()) {
             checkPlayerHoloVis(pInfo, holo);
           }
         }
         for (TaskPlayer tp : this.arena.getTasksManager().getTasksForPlayer(player)) {
           if (!tp.getIsDone().booleanValue()) {
             checkPlayerHoloVis(pInfo, tp.getActiveTask().getHolo());
           }
         } 
         if (this.arena.getCamerasManager().getHolo() != null) {
           checkPlayerHoloVis(pInfo, this.arena.getCamerasManager().getHolo());
         }
         if (this.arena.getVitalsManager() != null && this.arena.getVitalsManager().getHolo() != null) {
           checkPlayerHoloVis(pInfo, this.arena.getVitalsManager().getHolo());
         }
         checkPlayerHoloVis(pInfo, this.arena.getBtnHolo());
       } 
 
 
       
       for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
         if (pInfo1 == null) {
           continue;
         }
         Player player1 = pInfo1.getPlayer();
         if (!pInfo1.isGhost().booleanValue() && player1 != player) {
           if (!pInfo1.getIsInVent().booleanValue() && !pInfo.getIsInCameras().booleanValue()) {
             Location player1Loc = player1.getLocation();
             if (pInfo1.getIsInCameras().booleanValue()) {
               player1Loc = pInfo1.getPlayerCamLocTemp();
             }
             
             if (this.arena.getEnableReducedVision().booleanValue()) {
               if (Utils.isInsideCircle(player.getLocation(), Double.valueOf(pInfo.getVision()), player1Loc) == 2) {
                 if (!pInfo.getPlayersHidden().contains(player1)) {
                   hidePlayer(pInfo, pInfo1, Boolean.valueOf(true));
                 }
               } else if (pInfo.getPlayersHidden().contains(player1)) {
                 showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
               } 
             }
           } 
 
           
           if (!pInfo.getIsInVent().booleanValue() && !pInfo1.getIsInCameras().booleanValue()) {
             Location playerLoc = player.getLocation();
             if (pInfo.getIsInCameras().booleanValue()) {
               playerLoc = pInfo.getPlayerCamLocTemp();
             }
             if (this.arena.getEnableReducedVision().booleanValue()) {
               if (Utils.isInsideCircle(player1.getLocation(), Double.valueOf(pInfo1.getVision()), playerLoc) == 2) {
                 if (!pInfo1.getPlayersHidden().contains(player))
                   hidePlayer(pInfo1, pInfo, Boolean.valueOf(true));  continue;
               } 
               if (pInfo1.getPlayersHidden().contains(player))
                 showPlayer(pInfo1, pInfo, Boolean.valueOf(true)); 
             }  continue;
           } 
           if (!pInfo.getIsInVent().booleanValue() && pInfo1.getIsInCameras().booleanValue() && pInfo1.getPlayersHidden().contains(player) && !pInfo.isGhost().booleanValue()) {
             showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
           }
         } 
       } 
     } 
   }
   
   public void showPlayer(PlayerInfo pInfoToShowTo, PlayerInfo pInfoToShow, Boolean changeList) {
     if (pInfoToShow == null || pInfoToShowTo == null) {
       return;
     }
     if (changeList.booleanValue()) {
       pInfoToShowTo.getPlayersHidden().remove(pInfoToShow.getPlayer());
     }
     if (pInfoToShow.getIsInCameras().booleanValue()) {
       pInfoToShow.getFakePlayer().showPlayerTo(pInfoToShowTo, pInfoToShow.getPlayerCamLocTemp(), Boolean.valueOf(false), changeList);
     } else if (!pInfoToShow.getIsInVent().booleanValue()) {
       if (pInfoToShow.getIsScanning().booleanValue()) {
         for (FakeArmorStand fas : pInfoToShow.getScanArmorStands()) {
           fas.showTo(pInfoToShowTo.getPlayer(), Boolean.valueOf(true));
         }
       }
       
       PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
       Player playerToShow = pInfoToShow.getPlayer();
       spawnPacket.getIntegers().write(0, Integer.valueOf(playerToShow.getEntityId()));
       spawnPacket.getUUIDs().write(0, playerToShow.getUniqueId());
       Location loc = playerToShow.getLocation();
       spawnPacket.getDoubles().write(0, Double.valueOf(loc.getX())).write(1, Double.valueOf(loc.getY())).write(2, Double.valueOf(loc.getZ()));
       spawnPacket.getBytes().write(0, Byte.valueOf(Packets.toPackedByte(loc.getYaw()))).write(1, Byte.valueOf(Packets.toPackedByte(loc.getPitch())));
       Packets.sendPacket(pInfoToShowTo.getPlayer(), spawnPacket);
 
       
       PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
       metadataPacket.getIntegers().write(0, Integer.valueOf(pInfoToShow.getPlayer().getEntityId()));
       WrappedDataWatcher watcher = new WrappedDataWatcher();
       WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
       watcher.setEntity((Entity)pInfoToShow.getPlayer());
       
       watcher.setObject(16, serializer, Byte.valueOf((byte)1));
       watcher.setObject(16, serializer, Byte.valueOf((byte)2));
       watcher.setObject(16, serializer, Byte.valueOf((byte)4));
       watcher.setObject(16, serializer, Byte.valueOf((byte)8));
       watcher.setObject(16, serializer, Byte.valueOf((byte)16));
       watcher.setObject(16, serializer, Byte.valueOf((byte)32));
       watcher.setObject(16, serializer, Byte.valueOf((byte)64));
       if (pInfoToShow.isGhost().booleanValue()) {
         watcher.setObject(0, serializer, Byte.valueOf((byte)32));
       }
       metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
       Packets.sendPacket(pInfoToShowTo.getPlayer(), metadataPacket);
       
       Packets.sendPacket(pInfoToShowTo.getPlayer(), Packets.ENTITY_LOOK(playerToShow.getPlayer().getEntityId(), loc));
       Packets.sendPacket(pInfoToShowTo.getPlayer(), Packets.ENTITY_HEAD_ROTATION(playerToShow.getPlayer().getEntityId(), loc));
       
       if (pInfoToShow.getColor() != null) {
         Packets.sendPacket(pInfoToShowTo.getPlayer(), Packets.PLAYER_ARMOR(pInfoToShow.getColor(), playerToShow.getPlayer().getEntityId()));
       }
     } 
     if (pInfoToShow.getIsImposter().booleanValue() && pInfoToShowTo.getIsImposter().booleanValue() && 
       pInfoToShow.getImposterHolo() != null) {
       pInfoToShow.getImposterHolo().getVisibilityManager().showTo(pInfoToShowTo.getPlayer());
     }
   }
 
   
   public void hidePlayer(PlayerInfo pInfoToHideTo, PlayerInfo pInfoToHide, Boolean changeList) {
     if (changeList.booleanValue()) {
       pInfoToHideTo.getPlayersHidden().add(pInfoToHide.getPlayer());
     }
     if (pInfoToHide.getIsInCameras().booleanValue()) {
       pInfoToHide.getFakePlayer().hidePlayerFrom(pInfoToHideTo.getPlayer(), changeList);
     } else {
       if (pInfoToHide.getIsScanning().booleanValue()) {
         for (FakeArmorStand fas : pInfoToHide.getScanArmorStands()) {
           fas.hideFrom(pInfoToHideTo.getPlayer(), Boolean.valueOf(true));
         }
       }
       Packets.sendPacket(pInfoToHideTo.getPlayer(), Packets.DESTROY_ENTITY(pInfoToHide.getPlayer().getEntityId()));
     } 
     if (pInfoToHide.getIsImposter().booleanValue() && pInfoToHideTo.getIsImposter().booleanValue() && pInfoToHide.getImposterHolo() != null) {
       pInfoToHide.getImposterHolo().getVisibilityManager().hideTo(pInfoToHideTo.getPlayer());
     }
   }
   
   public void resetHologramsVis(PlayerInfo pInfo) {
     if (this.arena.getHideHologramsOutOfView().booleanValue()) {
       Player player = pInfo.getPlayer();
       if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue()) {
         for (Hologram holo : this.arena.getSabotageManager().getActiveSabotage().getHolos()) {
           holo.getVisibilityManager().showTo(player);
         }
       }
       if (pInfo.getIsImposter().booleanValue() && !pInfo.isGhost().booleanValue()) {
         for (Hologram holo : this.arena.getVentsManager().getHolos()) {
           holo.getVisibilityManager().showTo(player);
         }
       }
       for (TaskPlayer tp : this.arena.getTasksManager().getTasksForPlayer(player)) {
         if (!tp.getIsDone().booleanValue()) {
           tp.getActiveTask().getHolo().getVisibilityManager().showTo(player);
         }
       } 
       if (this.arena.getCamerasManager().getHolo() != null) {
         this.arena.getCamerasManager().getHolo().getVisibilityManager().showTo(player);
       }
       this.arena.getBtnHolo().getVisibilityManager().showTo(player);
     } 
   }
   
   public void resetPlayersHidden(PlayerInfo pInfo) {
     for (Player player1 : pInfo.getPlayersHidden()) {
       PlayerInfo pInfo1 = Main.getPlayersManager().getPlayerInfo(player1);
       if (pInfo1 != null) {
         showPlayer(pInfo, pInfo1, Boolean.valueOf(false));
       }
     } 
     pInfo.getPlayersHidden().clear();
   }
   
   public void resetFakePlayers(PlayerInfo pInfo) {
     if (pInfo.getFakePlayer() != null) {
       pInfo.getFakePlayer().resetAllPlayerVis();
     }
   }
 
   
   public Boolean canSee(PlayerInfo pInfo, PlayerInfo pInfo1) { return Boolean.valueOf(!pInfo.getPlayersHidden().contains(pInfo1.getPlayer())); }
 
 
   
   public Arena getArena() { return this.arena; }
 }


