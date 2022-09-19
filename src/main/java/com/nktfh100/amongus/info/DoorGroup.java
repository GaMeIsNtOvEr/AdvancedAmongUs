 package com.nktfh100.amongus.info;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 
 public class DoorGroup
   implements Comparable<DoorGroup> {
   private Arena arena;
   private LocationName locName;
   private String configId;
   private Integer id;
   private ArrayList<Door> doors = new ArrayList<>();
   private Integer closeTimer = Integer.valueOf(0);
 
   
   private HashMap<String, Integer> coolDownTimer = new HashMap<>();
   
   public DoorGroup(Arena arena, LocationName locName, String configId, Integer id) {
     this.arena = arena;
     this.configId = configId;
     this.id = id;
     this.locName = locName;
   }
   
   public void closeDoors(Boolean sound) {
     for (Door d : this.doors) {
       d.closeDoor(sound);
     }
   }
   
   public void openDoors(Boolean sound) {
     for (Door d : this.doors) {
       d.openDoor(sound);
     }
   }
   
   public void addDoor(Door d) {
     this.doors.add(d);
     Collections.sort(this.doors);
   }
 
   
   public Door getDoor(Integer id) { return this.doors.get(id); }
 
 
   
   public Integer getCooldownTimer(String uuid) { return this.coolDownTimer.get(uuid); }
 
 
   
   public void setCooldownTimer(String uuid, Integer closeTimer) { this.coolDownTimer.put(uuid, closeTimer); }
 
 
   
   public HashMap<String, Integer> getCooldownTimer() { return this.coolDownTimer; }
 
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public ArrayList<Door> getDoors() { return this.doors; }
 
 
   
   public Integer getId() { return this.id; }
 
 
   
   public String getConfigId() { return this.configId; }
 
 
 
   
   public int compareTo(DoorGroup vg) { return this.id.compareTo(vg.getId()); }
 
 
   
   public LocationName getLocName() { return this.locName; }
 
 
   
   public Integer getCloseTimer() { return this.closeTimer; }
 
 
   
   public void setCloseTimer(Integer closeTimer) { this.closeTimer = closeTimer; }
   
   public void delete() {
     this.arena = null;
     this.locName = null;
     this.configId = null;
     this.id = null;
     this.doors = null;
     this.closeTimer = null;
   }
 }


