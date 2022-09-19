 package com.nktfh100.amongus.events;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.BungeArena;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.entity.EntityPortalEnterEvent;
 import org.bukkit.scheduler.BukkitRunnable;
 
 public class PlayerEnterPortal
   implements Listener
 {
   private static final long cooldownTime = 2500L;
   
   @EventHandler
   public void onEnter(EntityPortalEnterEvent ev) {
     if (!(ev.getEntity() instanceof Player)) {
       return;
     }
     if (Main.getConfigManager().getEnablePortalJoin().booleanValue()) {
       final Player player = (Player)ev.getEntity();
       PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
       if (pInfo == null) {
         pInfo = Main.getPlayersManager()._addPlayer(player);
       }
       if (pInfo.getIsIngame().booleanValue()) {
         return;
       }
       
       long finish = System.currentTimeMillis();
       long timeElapsed = finish - pInfo.getPortalCooldown();
       if (timeElapsed >= 2500L) {
         pInfo.setPortalCooldown(System.currentTimeMillis());
         (new BukkitRunnable()
           {
             public void run()
             {
               if (Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
                 BungeArena arena_ = Main.getBungeArenaManager().getArenaWithMostPlayers();
                 if (arena_ != null) {
                   Main.sendPlayerToArena(player, arena_.getServer());
                 }
               } else {
                 Arena arena = Main.getArenaManager().getArenaWithMostPlayers();
                 if (arena != null) {
                   arena.playerJoin(player);
                 }
               } 
             }
           }).runTaskLater(Main.getPlugin(), 2L);
       } 
     } 
   }
 }


