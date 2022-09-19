 package com.nktfh100.amongus.api.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import org.bukkit.entity.Player;
 import org.bukkit.event.Cancellable;
 import org.bukkit.event.Event;
 import org.bukkit.event.HandlerList;
 
 public class AUArenaPlayerJoin
   extends Event
   implements Cancellable {
   private Arena arena;
   private Player player;
   private static final HandlerList HANDLERS_LIST = new HandlerList();
   private boolean isCancelled;
   
   public AUArenaPlayerJoin(Arena arena, Player player) {
     this.arena = arena;
     this.player = player;
     this.isCancelled = false;
   }
 
 
   
   public boolean isCancelled() { return this.isCancelled; }
 
 
 
   
   public void setCancelled(boolean cancelled) { this.isCancelled = cancelled; }
 
 
 
   
   public HandlerList getHandlers() { return HANDLERS_LIST; }
 
 
   
   public static HandlerList getHandlerList() { return HANDLERS_LIST; }
 
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public Player getPlayer() { return this.player; }
 }


