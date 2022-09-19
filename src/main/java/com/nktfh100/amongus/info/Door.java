 package com.nktfh100.amongus.info;
 
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import org.bukkit.Bukkit;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.block.Block;
 
 
 public class Door
   implements Comparable<Door>
 {
   private Arena arena;
   private Location corner1;
   private Location corner2;
   private ArrayList<BlockInfo> blocks = new ArrayList<>();
   private ArrayList<Block> blocks_ = new ArrayList<>();
   private Integer id;
   private String configId;
   private Boolean isClosed = Boolean.valueOf(false);
   private DoorGroup doorGroup;
   private Location midPoint;
   
   public Door(Arena arena, DoorGroup doorGroup, Location corner1, Location corner2, Integer id, String configId) {
     this.arena = arena;
     this.id = id;
     this.configId = configId;
     this.doorGroup = doorGroup;
     this.corner1 = corner1;
     this.corner2 = corner2;
     if (corner1 != null && corner2 != null) {
       if (!Utils.isLocationZero(corner1).booleanValue() && !Utils.isLocationZero(corner2).booleanValue()) {
         ArrayList<Block> blocks = Utils.blocksFromTwoPoints(corner1, corner2);
         if (blocks.size() < 50) {
           for (Block block : blocks) {
             this.blocks.add(new BlockInfo(block, block.getType(), Material.IRON_BLOCK, block.getBlockData().clone()));
             this.blocks_.add(block);
           } 
         } else {
           Bukkit.getLogger().info("Door " + doorGroup.getLocName().getName() + " - " + id + " has too many blocks! (> 50)");
         } 
       } 
       this.midPoint = calculateMidPoint();
     }
     else if (this.corner1 != null) {
       this.midPoint = corner1;
     } else if (this.corner2 != null) {
       this.midPoint = corner2;
     } else {
       this.midPoint = new Location(arena.getWorld(), 0.0D, 0.0D, 0.0D);
     } 
   }
 
   
   public void openDoor(Boolean sound) {
     this.isClosed = Boolean.valueOf(false);
     replaceBlocks(Boolean.valueOf(false));
     if (sound.booleanValue()) {
       for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
         if (pInfo.getPlayer().getWorld() == this.midPoint.getWorld() && pInfo.getPlayer().getLocation().distance(this.midPoint) <= 8.0D) {
           Main.getSoundsManager().playSound("doorOpen", pInfo.getPlayer(), this.midPoint);
         }
       } 
     }
   }
   
   public void closeDoor(Boolean sound) {
     this.isClosed = Boolean.valueOf(true);
     replaceBlocks(Boolean.valueOf(true));
     if (sound.booleanValue()) {
       for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
         if (pInfo.getPlayer().getWorld() == this.midPoint.getWorld() && pInfo.getPlayer().getLocation().distance(this.midPoint) <= 8.0D) {
           Main.getSoundsManager().playSound("doorClose", pInfo.getPlayer(), this.midPoint);
         }
       } 
     }
   }
   
   private void replaceBlocks(Boolean newBlock) {
     for (BlockInfo bi : this.blocks) {
       if (newBlock.booleanValue()) {
         bi.placeNewBlock(); continue;
       } 
       bi.placeOldBlock();
     } 
   }
 
   
   public void setCorner1(Location loc) {
     this.corner1 = loc;
     this.blocks.clear();
     this.blocks_.clear();
     if (this.corner1 != null && this.corner2 != null) {
       if (!Utils.isLocationZero(this.corner1).booleanValue() && !Utils.isLocationZero(this.corner2).booleanValue()) {
         ArrayList<Block> blocks = Utils.blocksFromTwoPoints(this.corner1, this.corner2);
         if (blocks.size() < 50) {
           for (Block block : blocks) {
             this.blocks.add(new BlockInfo(block, block.getType(), Material.IRON_BLOCK, block.getBlockData().clone()));
             this.blocks_.add(block);
           } 
         }
       } 
       this.midPoint = calculateMidPoint();
     }
     else if (this.corner1 != null) {
       this.midPoint = this.corner1;
     } else if (this.corner2 != null) {
       this.midPoint = this.corner2;
     } 
   }
 
   
   public void setCorner2(Location loc) {
     this.corner2 = loc;
     this.blocks.clear();
     this.blocks_.clear();
     if (this.corner1 != null && this.corner2 != null) {
       if (!Utils.isLocationZero(this.corner1).booleanValue() && !Utils.isLocationZero(this.corner2).booleanValue()) {
         ArrayList<Block> blocks = Utils.blocksFromTwoPoints(this.corner1, this.corner2);
         if (blocks.size() < 50) {
           for (Block block : blocks) {
             this.blocks.add(new BlockInfo(block, block.getType(), Material.IRON_BLOCK, block.getBlockData().clone()));
             this.blocks_.add(block);
           } 
         }
       } 
       this.midPoint = calculateMidPoint();
     }
     else if (this.corner1 != null) {
       this.midPoint = this.corner1;
     } else if (this.corner2 != null) {
       this.midPoint = this.corner2;
     } 
   }
 
   
   public Location calculateMidPoint() {
     Location out = this.corner1.clone().add(this.corner2);
     out.multiply(0.5D);
     return out;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public Integer getId() { return this.id; }
 
 
   
   public String getConfigId() { return this.configId; }
 
 
 
   
   public int compareTo(Door v) { return this.id.compareTo(v.getId()); }
 
 
   
   public ArrayList<BlockInfo> getBlocks() { return this.blocks; }
 
 
   
   public ArrayList<Block> getBlocks_() { return this.blocks_; }
 
 
   
   public Boolean getIsClosed() { return this.isClosed; }
 
 
   
   public void setIsClosed(Boolean isClosed) { this.isClosed = isClosed; }
 
 
   
   public DoorGroup getDoorGroup() { return this.doorGroup; }
 
 
   
   public Location getCorner2() { return this.corner2; }
 
 
   
   public Location getCorner1() { return this.corner1; }
 
 
   
   public Location getMidPoint() { return this.midPoint; }
 }


