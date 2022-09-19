 package com.nktfh100.amongus.info;
 
 import java.util.ArrayList;
 import java.util.Collections;
 
 public class VentGroup
   implements Comparable<VentGroup> {
   private Arena arena;
   private String configId;
   private Integer id;
   private Boolean loop;
   private ArrayList<Vent> vents = new ArrayList<>();
   
   public VentGroup(Arena arena, String configId, Integer id, Boolean loop) {
     this.arena = arena;
     this.configId = configId;
     this.id = id;
     this.loop = loop;
   }
   
   public void addVent(Vent v) {
     this.vents.add(v);
     Collections.sort(this.vents);
   }
 
   
   public Vent getVent(Integer id) { return this.vents.get(id); }
 
   
   public void delete() {
     for (Vent v : this.vents) {
       v.delete();
     }
     this.arena = null;
     this.configId = null;
     this.id = null;
     this.loop = null;
     this.vents = null;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public ArrayList<Vent> getVents() { return this.vents; }
 
 
   
   public Integer getId() { return this.id; }
 
 
   
   public String getConfigId() { return this.configId; }
 
 
   
   public Boolean getLoop() { return this.loop; }
 
 
   
   public void setLoop(Boolean is) { this.loop = is; }
 
 
 
   
   public int compareTo(VentGroup vg) { return this.id.compareTo(vg.getId()); }
 }


