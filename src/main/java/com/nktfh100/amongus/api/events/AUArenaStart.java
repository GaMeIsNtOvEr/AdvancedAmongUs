 package com.nktfh100.amongus.api.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import org.bukkit.event.Event;
 import org.bukkit.event.HandlerList;
 
 public class AUArenaStart
   extends Event
 {
   private Arena arena;
   private static final HandlerList HANDLERS_LIST = new HandlerList();
 
   
   public AUArenaStart(Arena arena) { this.arena = arena; }
 
 
 
   
   public HandlerList getHandlers() { return HANDLERS_LIST; }
 
 
   
   public static HandlerList getHandlerList() { return HANDLERS_LIST; }
 
 
   
   public Arena getArena() { return this.arena; }
 }


