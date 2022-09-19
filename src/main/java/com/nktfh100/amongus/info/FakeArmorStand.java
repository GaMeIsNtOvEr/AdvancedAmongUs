 package com.nktfh100.amongus.info;
 
 import com.comphenix.protocol.PacketType;
 import com.comphenix.protocol.events.PacketContainer;
 import com.comphenix.protocol.wrappers.Vector3F;
 import com.comphenix.protocol.wrappers.WrappedDataWatcher;
 import com.nktfh100.AmongUs.utils.Packets;
 import java.util.ArrayList;
 import java.util.UUID;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 
 
 
 
 public class FakeArmorStand
 {
   private PlayerInfo pInfo;
   private Location loc;
   private int entityId;
   private UUID uuid;
   private Vector3F headRotation = null;
   private Vector3F bodyRotation = null;
   
   private ArrayList<Player> shownTo = new ArrayList<>();
   
   public FakeArmorStand(PlayerInfo pInfo, Location loc, Vector3F headRotation, Vector3F bodyRotation) {
     this.pInfo = pInfo;
     this.loc = loc;
     this.entityId = (int)(Math.random() * 2.147483647E9D);
     this.uuid = UUID.randomUUID();
     this.headRotation = headRotation;
     this.bodyRotation = bodyRotation;
   }
   
   public void updateLocation(Location newLoc) {
     this.loc = newLoc;
     for (Player player : this.shownTo) {
       Packets.sendPacket(player, Packets.ENTITY_TELEPORT(this.entityId, newLoc));
     }
   }
   
   public void updateRotation(Vector3F headRotation, Vector3F bodyRotation) {
     PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
     packet.getIntegers().write(0, Integer.valueOf(this.entityId));
     
     WrappedDataWatcher watcher = new WrappedDataWatcher();
     watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), Byte.valueOf((byte)32));
     if (headRotation != null) {
       this.headRotation = headRotation;
       watcher.setObject(15, WrappedDataWatcher.Registry.getVectorSerializer(), this.headRotation);
     } 
     if (bodyRotation != null) {
       watcher.setObject(14, WrappedDataWatcher.Registry.getVectorSerializer(), this.bodyRotation);
       this.bodyRotation = bodyRotation;
     } 
     
     packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
     for (Player player : this.shownTo) {
       Packets.sendPacket(player, packet);
     }
   }
   
   public void showTo(Player player, Boolean register) {
     Packets.sendPacket(player, Packets.ARMOR_STAND(this.loc, Integer.valueOf(this.entityId), this.uuid));
     
     PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
     packet.getIntegers().write(0, Integer.valueOf(this.entityId));
     
     WrappedDataWatcher watcher = new WrappedDataWatcher();
     watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), Byte.valueOf((byte)32));
     if (this.headRotation != null) {
       watcher.setObject(15, WrappedDataWatcher.Registry.getVectorSerializer(), this.headRotation);
     }
     if (this.bodyRotation != null) {
       watcher.setObject(14, WrappedDataWatcher.Registry.getVectorSerializer(), this.bodyRotation);
     }
 
 
 
 
     
     packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
     Packets.sendPacket(player, packet);
     
     Packets.sendPacket(player, Packets.ENTITY_EQUIPMENT_HEAD(this.entityId, Material.LIME_STAINED_GLASS_PANE));
     
     if (register.booleanValue()) {
       this.shownTo.add(player);
     }
   }
   
   public void hideFrom(Player player, Boolean register) {
     Packets.sendPacket(player, Packets.DESTROY_ENTITY(this.entityId));
     if (register.booleanValue()) {
       this.shownTo.remove(player);
     }
   }
 
   
   public void resetAllShownTo() {
     for (Player p : this.shownTo) {
       hideFrom(p, Boolean.valueOf(false));
     }
     this.shownTo.clear();
   }
 
   
   public Location getLoc() { return this.loc; }
 
 
   
   public int getEntityId() { return this.entityId; }
 
 
   
   public UUID getUuid() { return this.uuid; }
 
 
   
   public PlayerInfo getPlayerInfo() { return this.pInfo; }
 }


