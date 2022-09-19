 package com.nktfh100.amongus.managers;
 
 import com.comphenix.protocol.events.PacketContainer;
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Packets;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import org.bukkit.Bukkit;
 import org.bukkit.ChatColor;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.PlayerJoinEvent;
 import org.bukkit.event.player.PlayerLoginEvent;
 import org.bukkit.event.player.PlayerQuitEvent;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class PlayersManager
   implements Listener
 {
   private HashMap<String, PlayerInfo> players = new HashMap<>();
   
   public PlayersManager() {
     for (Player player : Bukkit.getServer().getOnlinePlayers()) {
       this.players.put(player.getUniqueId().toString(), new PlayerInfo(player));
     }
     for (PlayerInfo pInfo : getPlayers()) {
       if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
         pInfo.getStatsManager().mysql_registerPlayer(Boolean.valueOf(true)); continue;
       } 
       pInfo.getStatsManager().loadStats();
     } 
   }
 
   
   public PlayerInfo _addPlayer(Player player) {
     PlayerInfo out = new PlayerInfo(player);
     if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
       out.getStatsManager().mysql_registerPlayer(Boolean.valueOf(true));
     } else {
       out.getStatsManager().loadStats();
     } 
     this.players.put(player.getUniqueId().toString(), out);
     return out;
   }
   
   public PlayerInfo getPlayerInfo(Player player) {
     PlayerInfo pInfo = this.players.get(player.getUniqueId().toString());
     if (pInfo == null) {
       pInfo = _addPlayer(player);
     }
     return pInfo;
   }
 
   
   public PlayerInfo getPlayerByUUID(String uuid) { return this.players.get(uuid); }
 
   
   public List<PlayerInfo> getPlayers() {
     List<PlayerInfo> players_ = new ArrayList<>(this.players.values());
     return players_;
   }
   
   @EventHandler
   public void onLogin(PlayerLoginEvent ev) {
     if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue() && Main.getArenaManager().getAllArenas().size() > 0) {
       Arena arena = Main.getArenaManager().getAllArenas().iterator().next();
       if (arena.getGameState() == GameState.RUNNING || arena.getGameState() == GameState.FINISHING) {
         if (ev.getPlayer().hasPermission("amongus.admin") || ev.getPlayer().hasPermission("amongus.admin.setup")) {
           ev.allow();
         } else {
           
           ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Arena running");
         } 
       } else if (arena.getPlayers().size() == arena.getMaxPlayers()) {
         if (ev.getPlayer().hasPermission("amongus.admin") || ev.getPlayer().hasPermission("amongus.admin.setup")) {
           ev.allow();
         } else {
           
           ev.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Arena is full!");
         } 
       } 
     } 
   }
   
   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent ev) {
     final Player player = ev.getPlayer();
     if (this.players.get(ev.getPlayer().getUniqueId().toString()) == null) {
       this.players.put(ev.getPlayer().getUniqueId().toString(), new PlayerInfo(ev.getPlayer()));
     } else {
       ((PlayerInfo)this.players.get(ev.getPlayer().getUniqueId().toString()))._setPlayer(player);
     } 
     if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
       getPlayerInfo(player).getStatsManager().mysql_registerPlayer(Boolean.valueOf(true));
     } else {
       getPlayerInfo(player).getStatsManager().loadStats();
     } 
     if (Main.getConfigManager().getGiveLobbyItems().booleanValue()) {
       player.getInventory().clear();
       
       ItemInfo arenasSelectorItem = Main.getItemsManager().getItem("arenasSelector").getItem();
       player.getInventory().setItem(Main.getConfigManager().getLobbyItemSlot("arenasSelector"), arenasSelectorItem.getItem());
       if (Main.getIsPlayerPoints().booleanValue()) {
         player.getInventory().setItem(Main.getConfigManager().getLobbyItemSlot("cosmeticsSelector"), Main.getItemsManager().getItem("cosmeticsSelector").getItem().getItem());
       }
     } 
     if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue() && Main.getArenaManager().getAllArenas().size() > 0) {
       final Arena arena = Main.getArenaManager().getAllArenas().iterator().next();
       if ((arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.STARTING) && arena.getPlayersInfo().size() < arena.getMaxPlayers()) {
         arena.playerJoin(player);
         (new BukkitRunnable()
           {
             public void run() {
               Main.getArenaManager().sendBungeUpdate(arena);
             }
           }).runTaskLater(Main.getPlugin(), 10L);
       } 
     } 
     if (!Main.getConfigManager().getBungeecord().booleanValue() || (Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getBungeecordIsLobby().booleanValue())) {
       if (Main.getConfigManager().getTpToLobbyOnJoin().booleanValue() && 
         Main.getConfigManager().getMainLobby() != null) {
         player.teleport(Main.getConfigManager().getMainLobby());
       }
 
       
       if (Main.getConfigManager().getEnableLobbyScoreboard().booleanValue()) {
         for (PlayerInfo pInfo_ : getPlayers()) {
           if (!pInfo_.getIsIngame().booleanValue()) {
             pInfo_.updateScoreBoard();
           }
         } 
       }
       if (!Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue() && Main.getConfigManager().getHidePlayersOutSideArena().booleanValue()) {
         (new BukkitRunnable()
           {
             public void run() {
               PacketContainer packet = Packets.REMOVE_PLAYER(player.getUniqueId(), player.getName(), player.getName());
               for (Arena arena : Main.getArenaManager().getAllArenas()) {
                 for (Player p : arena.getPlayers()) {
                   Packets.sendPacket(p, packet);
                   Packets.sendPacket(player, Packets.REMOVE_PLAYER(p.getUniqueId(), p.getName(), p.getName()));
                 }
               
               } 
             }
           }).runTaskLater(Main.getPlugin(), 5L);
       }
     } 
   }
   
   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
     Player player = event.getPlayer();
     PlayerInfo pInfo = this.players.get(player.getUniqueId().toString());
     if (pInfo == null) {
       return;
     }
     if (pInfo.getIsIngame().booleanValue()) {
       pInfo.getArena().get_playersToDelete().add(player);
       pInfo.getArena().playerLeave(player, Boolean.valueOf(false), Boolean.valueOf(true), Boolean.valueOf(true));
     } else {
       this.players.remove(player.getUniqueId().toString());
     } 
   }
 
   
   public void deletePlayer(String UUID) {
     if (this.players.get(UUID) != null) {
       ((PlayerInfo)this.players.get(UUID)).delete();
     }
     this.players.remove(UUID);
   }
   
   public void delete() {
     for (PlayerInfo pInfo : this.players.values()) {
       pInfo.delete();
     }
     this.players.clear();
     this.players = null;
   }
 }


