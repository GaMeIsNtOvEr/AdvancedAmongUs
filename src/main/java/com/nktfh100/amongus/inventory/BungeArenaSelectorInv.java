 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.info.BungeArena;
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.managers.BungeArenaManager;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 
 public class BungeArenaSelectorInv
   extends CustomHolder
 {
   public BungeArenaSelectorInv() { super(Integer.valueOf(45), Main.getMessagesManager().getGameMsg("arenasSelectorInvTitle", null, null)); }
 
   
   public void update() {
     clearInv();
     Material mat = Main.getItemsManager().getItem("arenasSelector_border").getItem().getMat();
     Utils.addBorder(this.inv, Integer.valueOf(45), mat);
     
     BungeArenaManager arenaManager = Main.getBungeArenaManager();
     
     Boolean showRunning = Main.getConfigManager().getShowRunningArenas();
     for (BungeArena arena : arenaManager.getAllArenas()) {
       Boolean canJoin = Boolean.valueOf(true);
       if (arena.getGameState() == GameState.RUNNING || arena.getGameState() == GameState.FINISHING) {
         canJoin = Boolean.valueOf(false);
       }
       if (arena.getCurrentPlayers() == arena.getMaxPlayers()) {
         canJoin = Boolean.valueOf(false);
       }
       if (!showRunning.booleanValue() && !canJoin.booleanValue()) {
         continue;
       }
       String gameStateStr = Main.getMessagesManager().getGameState(arena.getGameState());
       ItemInfo arenaItem = canJoin.booleanValue() ? Main.getItemsManager().getItem("arenasSelector_arena").getItem2() : Main.getItemsManager().getItem("arenasSelector_arena").getItem();
       ItemStack arenaItemS = arenaItem.getItem(arena.getName(), "" + arena.getCurrentPlayers(), "" + arena.getMaxPlayers(), "" + Utils.getStateColor(arena.getGameState()), gameStateStr);
       Icon icon = new Icon(arenaItemS);
       
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               Main.sendPlayerToArena(player, arena.getServer());
             }
           });
       addIcon(icon);
     } 
   }
 }


