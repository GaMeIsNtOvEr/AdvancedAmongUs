 package com.nktfh100.amongus.info;
 
 import com.gmail.filoghost.holographicdisplays.api.Hologram;
 import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.entity.Player;
 
 
 public class SabotageTask
 {
   private Integer id;
   private Location location;
   private SabotageType sabotageType;
   private Integer timer;
   private Boolean hasTimer;
   private Arena arena;
   private Hologram holo;
   private TouchHandler touchHandler;
   
   public SabotageTask(SabotageType sabotageType, Integer id, Integer timer) {
     this.id = id;
     this.sabotageType = sabotageType;
     this.timer = timer;
     this.hasTimer = Boolean.valueOf((timer > 0));
   }
   
   public void setInfo(Location location, Arena arena) {
     this.location = location;
     this.arena = arena;
     final SabotageTask sabotage = this;
     this.touchHandler = new TouchHandler()
       {
         public void onTouch(Player p) {
           if (sabotage.getArena().getGameState() == GameState.RUNNING) {
             sabotage.getArena().getSabotageManager().sabotageHoloClick(p, sabotage.getId());
           }
           else if (p.hasPermission("amongus.admin")) {
             PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(p);
             if (!pInfo.getIsIngame().booleanValue()) {
               p.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Sabotage holo click " + sabotage.sabotageType.toString() + " " + sabotage.getId());
             }
           } 
         }
       };
   }
 
 
   
   public void setHolo(Hologram holo) { this.holo = holo; }
 
 
   
   public SabotageType getSabotageType() { return this.sabotageType; }
 
 
   
   public Location getLocation() { return this.location; }
 
 
   
   public Hologram getHolo() { return this.holo; }
 
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public TouchHandler getTouchHandler() { return this.touchHandler; }
 
 
   
   public Integer getTimer() { return this.timer; }
 
 
   
   public Boolean getHasTimer() { return this.hasTimer; }
 
 
   
   public Integer getId() { return this.id; }
 }


