 package com.nktfh100.amongus.info;
 
 import com.comphenix.protocol.wrappers.WrappedBlockData;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import org.bukkit.Location;
 import org.bukkit.Material;
 import org.bukkit.entity.ArmorStand;
 import org.bukkit.entity.Player;
 import org.bukkit.util.EulerAngle;
 import org.bukkit.util.Vector;
 
 
 
 public class Camera
   implements Comparable<Camera>
 {
   public static final String camera = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=";
   public static final String lampOn = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUyYzE4YWIzNTk0OWJmOWY5ZTdkNmE2OWI4ODVjY2Q4Y2MyZWZiOTQ3NTk0NmQ3ZDNmYjVjM2ZlZjYxIn19fQ==";
   public static final String lampOff = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmMTRlNTAxN2IyNzliMDNkYWM5N2Q0MjliNGE1ZmE2YzM5OGFkNTY4ZWE0M2U3YzQwNjgzYzczOThjMTYyNyJ9fX0=";
   private Arena arena;
   private Integer id;
   private String configKey;
   private Location viewLoc;
   private Location camLoc;
   private Location lampLoc;
   private LocationName locName;
   private ArmorStand armorStand = null;
   private ArmorStand lampArmorStand = null;
   private Boolean isActive = Boolean.valueOf(false);
   
   private ArrayList<FakeBlock> fakeBlocks = new ArrayList<>();
   private ArrayList<FakeBlock> fakeAirBlocks = new ArrayList<>();
   
   public Camera(Arena arena, Integer id, Location viewLoc, Location camLoc, Location lampLoc, LocationName locName, String configKey) {
     this.arena = arena;
     this.id = id;
     this.camLoc = camLoc;
     this.lampLoc = lampLoc;
     this.locName = locName;
     this.configKey = configKey;
     
     Vector dir = viewLoc.getDirection().normalize();
     this.viewLoc = viewLoc.add(dir.multiply(0.56D));
   }
   
   public void createArmorStand() {
     this.armorStand = Utils.createArmorStand(this.camLoc);
     this.armorStand.getEquipment().setHelmet(Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFlM2EzYTRhMWFhNTBkODVkYmNkYWM4ZGE2M2Q3Y2JmZDQ1ZTUyMGRmZWMyZDUwYmVkZjhlOTBlOGIwZTRlYSJ9fX0=", "", 1, new String[] { "" }));
     this.armorStand.setHeadPose(new EulerAngle(Math.toRadians(this.camLoc.getPitch()), 0.0D, 0.0D));
     this.armorStand.setCustomName("camera_armor_stand" + this.id);
     
     this.lampArmorStand = Utils.createArmorStand(this.lampLoc);
     this.lampArmorStand.getEquipment().setHelmet(Utils.createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmMTRlNTAxN2IyNzliMDNkYWM5N2Q0MjliNGE1ZmE2YzM5OGFkNTY4ZWE0M2U3YzQwNjgzYzczOThjMTYyNyJ9fX0=", "", 1, new String[] { "" }));
     this.lampArmorStand.setHeadPose(new EulerAngle(Math.toRadians(this.lampLoc.getPitch()), 0.0D, 0.0D));
     this.lampArmorStand.setCustomName("camera_armor_stand1" + this.id);
   }
   
   public void deleteArmorStands() {
     if (this.armorStand != null) {
       this.armorStand.remove();
     }
     if (this.lampArmorStand != null) {
       this.lampArmorStand.remove();
     }
   }
   
   public void updateLamp() {
     Boolean isActive_ = Boolean.valueOf((this.arena.getCamerasManager().getPlayersInCameras().size() > 0));
     this.lampArmorStand.getEquipment().setHelmet(Utils.createSkull(isActive_.booleanValue() ? "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGUyYzE4YWIzNTk0OWJmOWY5ZTdkNmE2OWI4ODVjY2Q4Y2MyZWZiOTQ3NTk0NmQ3ZDNmYjVjM2ZlZjYxIn19fQ==" : "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmRmMTRlNTAxN2IyNzliMDNkYWM5N2Q0MjliNGE1ZmE2YzM5OGFkNTY4ZWE0M2U3YzQwNjgzYzczOThjMTYyNyJ9fX0=", "", 1, new String[] { "" }));
   }
 
   
   public void addFakeBlock(Location loc, Material oldMat, Material newMat, WrappedBlockData oldBlockData) { this.fakeBlocks.add(new FakeBlock(loc, oldMat, newMat, oldBlockData)); }
 
   
   public void showFakeBlocks(Player player) {
     for (FakeBlock fb : this.fakeBlocks) {
       fb.sendNewBlock(player);
     }
   }
   
   public void hideFakeBlocks(Player player) {
     for (FakeBlock fb : this.fakeBlocks) {
       fb.sendOldBlock(player);
     }
   }
 
   
   public void addFakeAirBlock(Location loc) { this.fakeAirBlocks.add(new FakeBlock(loc, loc.getBlock().getType(), Material.BARRIER, WrappedBlockData.createData(loc.getBlock().getBlockData()))); }
 
   
   public void showFakeAirBlocks(Player player) {
     for (FakeBlock fb : this.fakeAirBlocks) {
       fb.sendNewBlock(player);
     }
   }
   
   public void hideFakeAirBlocks(Player player) {
     for (FakeBlock fb : this.fakeAirBlocks) {
       fb.sendOldBlock(player);
     }
   }
   
   public void updateViewLoc(Location viewLoc) {
     Vector dir = viewLoc.getDirection().normalize();
     this.viewLoc = viewLoc.add(dir.multiply(0.56D));
   }
   
   public void updateCamLoc(Location camLoc) {
     this.camLoc = camLoc;
     deleteArmorStands();
     createArmorStand();
   }
   
   public void updateLampLoc(Location lampLoc) {
     this.lampLoc = lampLoc;
     deleteArmorStands();
     createArmorStand();
   }
   
   public void delete() {
     deleteArmorStands();
     this.arena = null;
     this.id = null;
     this.configKey = null;
     this.viewLoc = null;
     this.camLoc = null;
     this.lampLoc = null;
     this.locName = null;
     this.armorStand = null;
     this.lampArmorStand = null;
     this.isActive = Boolean.valueOf(false);
     this.fakeBlocks = null;
     this.fakeAirBlocks = null;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public Boolean getIsActive() { return this.isActive; }
 
 
   
   public void setIsActive(Boolean is) { this.isActive = is; }
 
 
   
   public LocationName getLocName() { return this.locName; }
 
 
   
   public void setLocName(LocationName locName) { this.locName = locName; }
 
 
   
   public Integer getId() { return this.id; }
 
 
   
   public String getConfigKey() { return this.configKey; }
 
 
   
   public Location getViewLoc() { return this.viewLoc; }
 
 
   
   public Location getCamLoc() { return this.camLoc; }
 
 
   
   public Location getLampLoc() { return this.lampLoc; }
 
 
   
   public ArrayList<FakeBlock> getFakeBlocks() { return this.fakeBlocks; }
 
 
   
   public void deleteFakeBlocks() { this.fakeBlocks = new ArrayList<>(); }
 
 
   
   public ArrayList<FakeBlock> getFakeAirBlocks() { return this.fakeAirBlocks; }
 
 
   
   public void deleteFakeAirBlocks() { this.fakeAirBlocks = new ArrayList<>(); }
 
 
 
   
   public int compareTo(Camera c) { return this.id.compareTo(c.getId()); }
 }


