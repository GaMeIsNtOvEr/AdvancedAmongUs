 package com.nktfh100.amongus.main;
 
 import com.nktfh100.AmongUs.enums.SabotageLength;
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.info.SabotageArena;
 import com.nktfh100.AmongUs.info.SabotageTask;
 import com.nktfh100.AmongUs.info.Task;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.Location;
 import org.bukkit.entity.Player;
 import org.bukkit.map.MapCanvas;
 import org.bukkit.map.MapCursor;
 import org.bukkit.map.MapCursorCollection;
 import org.bukkit.map.MapRenderer;
 import org.bukkit.map.MapView;
 
 
 
 
 public class Renderer
   extends MapRenderer
 {
   private static Byte getCardinalDirection(Player player) {
     double rotation = ((player.getLocation().getYaw() - 90.0F) % 360.0F);
     if (rotation < 0.0D) {
       rotation += 360.0D;
     }
     if (0.0D <= rotation && rotation < 22.5D) {
       return Byte.valueOf((byte)4);
     }
     if (22.5D <= rotation && rotation < 67.5D) {
       return Byte.valueOf((byte)6);
     }
     if (67.5D <= rotation && rotation < 112.5D) {
       return Byte.valueOf((byte)8);
     }
     if (112.5D <= rotation && rotation < 157.5D) {
       return Byte.valueOf((byte)10);
     }
     if (157.5D <= rotation && rotation < 202.5D) {
       return Byte.valueOf((byte)12);
     }
     if (202.5D <= rotation && rotation < 247.5D) {
       return Byte.valueOf((byte)14);
     }
     if (247.5D <= rotation && rotation < 292.5D) {
       return Byte.valueOf((byte)0);
     }
     if (292.5D <= rotation && rotation < 337.5D) {
       return Byte.valueOf((byte)2);
     }
     if (337.5D <= rotation && rotation < 360.0D) {
       return Byte.valueOf((byte)4);
     }
     return null;
   }
   
   private Boolean didSet = Boolean.valueOf(false);
   public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
     int playerZ, playerX;
     Integer centerZ, centerX;
     PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
     
     if (pInfo == null || pInfo.getArena() == null) {
       return;
     }
 
 
     
     if (pInfo.getArena().getMoveMapWithPlayer().booleanValue()) {
       Location loc_ = player.getLocation();
       centerX = Integer.valueOf(loc_.getBlockX());
       centerZ = Integer.valueOf(loc_.getBlockZ());
       mapView.setCenterX(centerX);
       mapView.setCenterZ(centerZ);
     } else {
       centerX = Integer.valueOf(pInfo.getArena().getMapCenter().getBlockX());
       centerZ = Integer.valueOf(pInfo.getArena().getMapCenter().getBlockZ());
       if (!this.didSet.booleanValue()) {
         mapView.setCenterX(centerX);
         mapView.setCenterZ(centerZ);
         this.didSet = Boolean.valueOf(true);
       } 
     } 
     MapCursorCollection cursors = new MapCursorCollection();
     Boolean isCommsDisabled = Boolean.valueOf(false);
     if (pInfo.getArena().getSabotageManager().getIsSabotageActive().booleanValue() && 
       pInfo.getArena().getSabotageManager().getActiveSabotage().getType() == SabotageType.COMMUNICATIONS) {
       isCommsDisabled = Boolean.valueOf(true);
     }
     
     if (pInfo.getArena().getTasksManager().getTasksForPlayer(player) != null && !isCommsDisabled.booleanValue()) {
       for (TaskPlayer tasksAssignment : pInfo.getArena().getTasksManager().getTasksForPlayer(player)) {
         if (tasksAssignment.getIsDone().booleanValue()) {
           continue;
         }
         
         Task task = tasksAssignment.getActiveTask();
         int taskX = task.getLocation().getBlockX();
         int taskZ = task.getLocation().getBlockZ();
         
         int mapX = 0;
         int mapY = 0;
         
         int difX = Math.abs(taskX - centerX);
         int difZ = Math.abs(taskZ - centerZ);
         String label = "";
         if (mapView.getScale() == MapView.Scale.CLOSEST) {
           difX *= 2;
           difZ *= 2;
           if (difX > 126) {
             difX = 126;
             difX -= 5;
           } 
           
           if (difZ > 126) {
             difZ = 126;
             difZ -= 5;
           } 
         } 
 
         
         if (taskX > centerX) {
           mapX += difX;
         } else {
           mapX -= difX;
         } 
         
         if (taskZ > centerZ) {
           mapY += difZ;
         } else {
           mapY -= difZ;
         } 
         
         cursors.addCursor(new MapCursor((byte)mapX, (byte)mapY, (byte)8, MapCursor.Type.BANNER_YELLOW, true, label));
       } 
     }
 
     
     if (pInfo.getArena().getSabotageManager().getIsSabotageActive().booleanValue()) {
       SabotageArena activeSabotageAr = pInfo.getArena().getSabotageManager().getActiveSabotage();
       ArrayList<SabotageTask> saboTasks = new ArrayList<>(Arrays.asList(new SabotageTask[] { activeSabotageAr.getTask1() }));
       if (activeSabotageAr.getLength() != SabotageLength.SINGLE) {
         saboTasks.add(activeSabotageAr.getTask2());
       }
       for (SabotageTask saboTask : saboTasks) {
         
         int locX = saboTask.getLocation().getBlockX();
         int locZ = saboTask.getLocation().getBlockZ();
         
         int mapX = 0;
         int mapY = 0;
         
         int difX = Math.abs(locX - centerX);
         int difZ = Math.abs(locZ - centerZ);
         if (mapView.getScale() == MapView.Scale.CLOSEST) {
           difX *= 2;
           difZ *= 2;
           
           if (difX > 126) {
             continue;
           }
           if (difZ > 126) {
             continue;
           }
         } 
         
         if (locX > centerX) {
           mapX += difX;
         } else {
           mapX -= difX;
         } 
         
         if (locZ > centerZ) {
           mapY += difZ;
         } else {
           mapY -= difZ;
         } 
         
         cursors.addCursor(new MapCursor((byte)mapX, (byte)mapY, (byte)8, MapCursor.Type.BANNER_RED, true, ""));
       } 
     } 
 
 
     
     if (pInfo.getArena().getMoveMapWithPlayer().booleanValue()) {
       playerX = centerX;
       playerZ = centerZ;
     } else {
       Location loc_ = player.getLocation();
       playerX = loc_.getBlockX();
       playerZ = loc_.getBlockZ();
     } 
     
     int mapX = 0;
     int mapY = 0;
     
     int difX = Math.abs(playerX - centerX);
     int difZ = Math.abs(playerZ - centerZ);
     if (mapView.getScale() == MapView.Scale.CLOSEST) {
       difX *= 2;
       difZ *= 2;
       if (difX > 126) {
         difX = 126;
         difX -= 5;
       } 
       if (difZ > 126) {
         difZ = 126;
         difZ -= 5;
       } 
     } 
     
     if (playerX > centerX) {
       mapX += difX;
     } else {
       mapX -= difX;
     } 
     
     if (playerZ > centerZ) {
       mapY += difZ;
     } else {
       mapY -= difZ;
     } 
     
     cursors.addCursor(new MapCursor((byte)mapX, (byte)mapY, getCardinalDirection(player).byteValue(), MapCursor.Type.BLUE_POINTER, true));
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     mapCanvas.setCursors(cursors);
   }
 }


