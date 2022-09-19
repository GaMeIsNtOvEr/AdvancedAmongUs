 package com.nktfh100.amongus.info;
 
 import com.gmail.filoghost.holographicdisplays.api.Hologram;
 import org.bukkit.Location;
 
 public class Vent
   implements Comparable<Vent>
 {
   private Arena arena;
   private VentGroup ventGroup;
   private Location loc;
   private Location playerLoc;
   private LocationName locName;
   private Integer id;
   private String configId;
   private Hologram holo;
   
   public Vent(Arena arena, VentGroup ventGroup, Location loc, LocationName locName, Integer id, String configId) {
     this.arena = arena;
     this.ventGroup = ventGroup;
     this.loc = loc;
     this.locName = locName;
     this.id = id;
     this.configId = configId;
     this.playerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.85D, loc.getZ(), loc.getYaw(), loc.getPitch());
   }
   
   public void delete() {
     this.arena = null;
     this.ventGroup = null;
     this.loc = null;
     this.playerLoc = null;
     this.locName = null;
     this.id = null;
     this.configId = null;
     this.holo = null;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public Location getLoc() { return this.loc; }
 
 
   
   public void setLoc(Location loc) { this.loc = loc; }
 
 
   
   public Integer getId() { return this.id; }
 
 
   
   public Hologram getHolo() { return this.holo; }
 
 
   
   public void setHolo(Hologram holo) { this.holo = holo; }
 
 
   
   public String getConfigId() { return this.configId; }
 
 
   
   public LocationName getLocName() { return this.locName; }
 
 
   
   public void setLocName(LocationName locName) { this.locName = locName; }
 
 
 
   
   public int compareTo(Vent v) { return this.id.compareTo(v.getId()); }
 
 
   
   public Location getPlayerLoc() { return this.playerLoc; }
 
 
   
   public VentGroup getVentGroup() { return this.ventGroup; }
 }


