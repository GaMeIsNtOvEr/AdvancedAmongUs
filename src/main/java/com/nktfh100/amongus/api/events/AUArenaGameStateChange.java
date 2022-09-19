 package com.nktfh100.amongus.api.events;
 
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.info.Arena;
 import org.bukkit.event.Event;
 import org.bukkit.event.HandlerList;
 
 public class AUArenaGameStateChange
   extends Event
 {
   private Arena arena;
   private GameState gameState;
   private static final HandlerList HANDLERS_LIST = new HandlerList();
   
   public AUArenaGameStateChange(Arena arena, GameState newGameState) {
     this.arena = arena;
     this.gameState = newGameState;
   }
 
 
   
   public HandlerList getHandlers() { return HANDLERS_LIST; }
 
 
   
   public static HandlerList getHandlerList() { return HANDLERS_LIST; }
 
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public GameState getNewGameState() { return this.gameState; }
 }


