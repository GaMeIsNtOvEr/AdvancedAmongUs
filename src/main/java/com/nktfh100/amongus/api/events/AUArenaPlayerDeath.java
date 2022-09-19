 package com.nktfh100.amongus.api.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import org.bukkit.entity.Player;
 import org.bukkit.event.Event;
 import org.bukkit.event.HandlerList;
 
 public class AUArenaPlayerDeath
   extends Event
 {
   private Arena arena;
   private Player player;
   private Player killer;
   private Boolean killed;
   private static final HandlerList HANDLERS_LIST = new HandlerList();
   
   public AUArenaPlayerDeath(Arena arena, Player player, Boolean killed, Player killer) {
     this.arena = arena;
     this.player = player;
     this.killed = killed;
     this.killer = killer;
   }
 
 
   
   public HandlerList getHandlers() { return HANDLERS_LIST; }
 
 
   
   public static HandlerList getHandlerList() { return HANDLERS_LIST; }
 
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public Player getPlayer() { return this.player; }
 
 
   
   public Player getKiller() { return this.killer; }
 
 
   
   public Boolean getKilled() { return this.killed; }
 }


