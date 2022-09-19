 package com.nktfh100.amongus.managers;
 
 import com.gmail.filoghost.holographicdisplays.api.Hologram;
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.Camera;
 import com.nktfh100.AmongUs.info.DeadBody;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Packets;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import org.bukkit.Location;
 import org.bukkit.entity.Player;
 import org.bukkit.scheduler.BukkitRunnable;
 import org.bukkit.util.Vector;
 
 
 
 public class CamerasManager
 {
   private Arena arena;
   private Location holoLoc;
   private Hologram holo;
   private ArrayList<Camera> cameras = new ArrayList<>();
   private ArrayList<PlayerInfo> playersInCameras = new ArrayList<>();
 
   
   public CamerasManager(Arena arena) { this.arena = arena; }
 
 
   
   public void camerasHoloClick(PlayerInfo pInfo) {
     if (this.cameras.size() == 0 || this.arena.getGameState() != GameState.RUNNING) {
       return;
     }
     if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue() && this.arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.COMMUNICATIONS) {
       return;
     }
     
     Iterator<PlayerInfo> iter = this.playersInCameras.iterator();
     while (iter.hasNext()) {
       PlayerInfo next = iter.next();
       if (!next.getIsIngame().booleanValue() || !next.getIsInCameras().booleanValue()) {
         iter.remove();
       }
     } 
     
     Player player = pInfo.getPlayer();
     if (!pInfo.isGhost().booleanValue()) {
       this.playersInCameras.add(pInfo);
       for (Camera cam : this.cameras) {
         cam.updateLamp();
       }
     } 
     
     Location loc_ = player.getLocation().clone();
     if (!player.isOnGround()) {
       loc_.setY(Math.floor(loc_.getY()));
       for (int i = 0; i < 3; i++) {
         loc_.add(0.0D, -1.0D, 0.0D);
         if (!loc_.getBlock().isEmpty()) {
           loc_.add(0.0D, 1.0D, 0.0D);
           break;
         } 
       } 
     } 
     pInfo.setPlayerCamLocTemp(loc_);
     pInfo.setIsInCameras(Boolean.valueOf(true));
     pInfo.setActiveCamera(this.cameras.get(0));
 
 
     
     for (PlayerInfo pInfo1 : pInfo.getArena().getPlayersInfo()) {
       if (pInfo1 != pInfo) {
         Packets.sendPacket(pInfo1.getPlayer(), Packets.DESTROY_ENTITY(pInfo.getPlayer().getEntityId()));
         if (!pInfo.isGhost().booleanValue() || (pInfo.isGhost().booleanValue() && pInfo1.isGhost().booleanValue())) {
           if (this.arena.getEnableReducedVision().booleanValue()) {
             if (Utils.isInsideCircle(pInfo1.getPlayer().getLocation(), Double.valueOf(pInfo1.getVision()), pInfo.getPlayer().getLocation()) != 2)
               pInfo.getFakePlayer().showPlayerTo(pInfo1, pInfo.getPlayerCamLocTemp(), Boolean.valueOf(false), Boolean.valueOf(true)); 
             continue;
           } 
           pInfo.getFakePlayer().showPlayerTo(pInfo1, pInfo.getPlayerCamLocTemp(), Boolean.valueOf(false), Boolean.valueOf(true));
         } 
       } 
     } 
 
     
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(true));
     pInfo.getActiveCamera().showFakeBlocks(player);
     pInfo.getActiveCamera().showFakeAirBlocks(player);
     
     pInfo.removeVisionBlocks();
     this.arena.giveGameInventory(pInfo);
     player.setVelocity(new Vector(0, 0, 0));
     
     player.setAllowFlight(true);
     player.setFlying(true);
     player.teleport(pInfo.getActiveCamera().getViewLoc());
     
     Main.getSoundsManager().playSound("playerGetInCameras", player, pInfo.getActiveCamera().getViewLoc());
 
     
     if (!pInfo.isGhost().booleanValue()) {
       for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
         if (pInfo != pInfo1 && !pInfo1.isGhost().booleanValue() && !pInfo1.getIsInCameras().booleanValue() && !pInfo1.getIsInVent().booleanValue()) {
           this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
         }
       } 
     }
     
     for (DeadBody db : this.arena.getDeadBodiesManager().getBodies()) {
       db.showTo(pInfo, Boolean.valueOf(true));
     }
   }
   
   public void playerLeaveCameras(PlayerInfo pInfo, Boolean isForce) {
     Player player = pInfo.getPlayer();
     
     if (!pInfo.isGhost().booleanValue()) {
       this.playersInCameras.remove(pInfo);
       for (Camera cam : this.cameras) {
         cam.updateLamp();
       }
     } 
     
     pInfo.setIsInCameras(Boolean.valueOf(false));
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(false));
     pInfo.getActiveCamera().hideFakeBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().hideFakeAirBlocks(player);
     
     pInfo.getPlayer().teleport(pInfo.getPlayerCamLocTemp());
     Main.getSoundsManager().playSound("playerLeaveCameras", player, pInfo.getPlayerCamLocTemp());
     
     if (pInfo.isGhost().booleanValue() && Main.getConfigManager().getGhostsFly().booleanValue()) {
       pInfo.getPlayer().setAllowFlight(true);
     } else {
       pInfo.getPlayer().setAllowFlight(false);
     } 
     pInfo.getPlayer().setFlying(false);
     pInfo.setActiveCamera(null);
     if (!isForce.booleanValue()) {
       this.arena.giveGameInventory(pInfo);
       for (DeadBody db : this.arena.getDeadBodiesManager().getBodies()) {
         this.arena.getVisibilityManager().checkPlayerBodyVis(pInfo, db);
       }
     } 
     for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
       if (pInfo1 == null) {
         continue;
       }
       if (pInfo1 != pInfo) {
         pInfo.getFakePlayer().hidePlayerFrom(pInfo1.getPlayer(), Boolean.valueOf(true));
         if (this.arena.getEnableReducedVision().booleanValue()) {
           if (Utils.isInsideCircle(pInfo1.getPlayer().getLocation(), Double.valueOf(pInfo1.getVision()), pInfo.getPlayerCamLocTemp()) != 2)
             this.arena.getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true)); 
           continue;
         } 
         this.arena.getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
       } 
     } 
     
     this.arena.getVisibilityManager().playerMoved(pInfo, pInfo.getPlayerCamLocTemp());
     pInfo.setPlayerCamLocTemp(null);
   }
   
   public void playerPrevCamera(final PlayerInfo pInfo) {
     int id = pInfo.getActiveCamera().getId();
     if (id == 0) {
       id = this.cameras.size() - 1;
     } else {
       id--;
     } 
     pInfo.getPlayer().getInventory().clear();
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(false));
     pInfo.getActiveCamera().hideFakeBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().hideFakeAirBlocks(pInfo.getPlayer());
     
     pInfo.setActiveCamera(this.cameras.get(id));
     
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(true));
     pInfo.getActiveCamera().showFakeBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().showFakeAirBlocks(pInfo.getPlayer());
     
     pInfo.getPlayer().setAllowFlight(true);
     pInfo.getPlayer().setFlying(true);
     pInfo.getPlayer().teleport(pInfo.getActiveCamera().getViewLoc());
     (new BukkitRunnable()
       {
         public void run() {
           if (pInfo.getIsIngame().booleanValue()) {
             pInfo.getArena().giveGameInventory(pInfo);
           }
         }
       }).runTaskLater(Main.getPlugin(), 5L);
     Utils.sendActionBar(pInfo.getPlayer(), getCameraActionBar(pInfo.getActiveCamera()));
     Main.getSoundsManager().playSound("playerNextCamera", pInfo.getPlayer(), pInfo.getActiveCamera().getViewLoc());
     
     for (PlayerInfo pInfo1 : pInfo.getArena().getPlayersInfo()) {
       if (pInfo1 != pInfo && !pInfo1.getIsInVent().booleanValue() && !pInfo1.getIsInCameras().booleanValue())
       {
         if (pInfo1.isGhost().booleanValue()) {
           this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
         }
       }
     } 
     
     for (DeadBody db : this.arena.getDeadBodiesManager().getBodies()) {
       db.showTo(pInfo, Boolean.valueOf(true));
     }
   }
   
   public void playerNextCamera(final PlayerInfo pInfo) {
     int id = pInfo.getActiveCamera().getId();
     if (id == this.cameras.size() - 1) {
       id = 0;
     } else {
       id++;
     } 
     pInfo.getPlayer().getInventory().clear();
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(false));
     pInfo.getActiveCamera().hideFakeBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().hideFakeAirBlocks(pInfo.getPlayer());
     
     pInfo.setActiveCamera(this.cameras.get(id));
     
     pInfo.getActiveCamera().showFakeBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().showFakeAirBlocks(pInfo.getPlayer());
     pInfo.getActiveCamera().setIsActive(Boolean.valueOf(true));
     
     pInfo.getPlayer().setAllowFlight(true);
     pInfo.getPlayer().setFlying(true);
     pInfo.getPlayer().teleport(pInfo.getActiveCamera().getViewLoc());
     (new BukkitRunnable()
       {
         public void run() {
           if (pInfo.getIsIngame().booleanValue()) {
             pInfo.getArena().giveGameInventory(pInfo);
           }
         }
       }).runTaskLater(Main.getPlugin(), 5L);
     Utils.sendActionBar(pInfo.getPlayer(), getCameraActionBar(pInfo.getActiveCamera()));
     Main.getSoundsManager().playSound("playerNextCamera", pInfo.getPlayer(), pInfo.getActiveCamera().getViewLoc());
     
     for (PlayerInfo pInfo1 : pInfo.getArena().getPlayersInfo()) {
       if (pInfo1 != pInfo && !pInfo1.getIsInVent().booleanValue() && !pInfo1.getIsInCameras().booleanValue())
       {
         if (pInfo1.isGhost().booleanValue()) {
           this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
         }
       }
     } 
     
     for (DeadBody db : this.arena.getDeadBodiesManager().getBodies()) {
       db.showTo(pInfo, Boolean.valueOf(true));
     }
   }
   
   public String getCameraActionBar(Camera camera) {
     if (camera.getLocName() == null) {
       return Main.getMessagesManager().getGameMsg("cameraActionBar1", this.arena, null);
     }
     return Main.getMessagesManager().getGameMsg("cameraActionBar", this.arena, camera.getLocName().getName());
   }
 
   
   public void addCamera(Camera c) {
     this.cameras.add(c);
     Collections.sort(this.cameras);
   }
   
   public void delete() {
     for (Camera cam : this.cameras) {
       cam.delete();
     }
     this.arena = null;
     this.cameras = null;
     this.playersInCameras = null;
     this.holoLoc = null;
     this.holo = null;
     this.cameras = null;
     this.playersInCameras = null;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public ArrayList<Camera> getCameras() { return this.cameras; }
 
 
   
   public Hologram getHolo() { return this.holo; }
 
 
   
   public void setHolo(Hologram holo) { this.holo = holo; }
 
 
   
   public Location getHoloLoc() { return this.holoLoc; }
 
 
   
   public void setHoloLoc(Location holoLoc) { this.holoLoc = holoLoc; }
 
 
   
   public ArrayList<PlayerInfo> getPlayersInCameras() { return this.playersInCameras; }
 }


