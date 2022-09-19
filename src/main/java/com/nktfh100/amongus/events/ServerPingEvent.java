 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.server.ServerListPingEvent;
 
 public class ServerPingEvent
   implements Listener {
   @EventHandler
   public void onServerListPing(ServerListPingEvent event) {
     if (Main.getConfigManager().getBungeecord().booleanValue() && 
       Main.getArenaManager().getAllArenas().size() > 0) {
       Arena arena = Main.getArenaManager().getAllArenas().iterator().next();
       if (arena != null)
         event.setMotd(Main.getMessagesManager().getGameState(arena.getGameState())); 
     } 
   }
 }


