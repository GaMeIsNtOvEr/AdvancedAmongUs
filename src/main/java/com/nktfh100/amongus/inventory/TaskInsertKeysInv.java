 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class TaskInsertKeysInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> keysSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(24) }));
   
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskInsertKeysInv(Arena arena, TaskPlayer taskPlayer) {
     super(36, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     update();
   }
   
   public void keyClick(Integer i) {
     if (this.isDone.booleanValue()) {
       return;
     }
     if (i == getPlayerInfo().getJoinedId()) {
       this.isDone = Boolean.valueOf(true);
       Main.getSoundsManager().playSound("taskInsertKeysClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     } 
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskInsertKeysInv inv = this;
       (new BukkitRunnable()
         {
           public void run() {
             Player player = inv.getTaskPlayer().getPlayerInfo().getPlayer();
             if (player.getOpenInventory().getTopInventory() == inv.getInventory()) {
               player.closeInventory();
             }
           }
         }).runTaskLater(Main.getPlugin(), 20L);
       return Boolean.valueOf(true);
     } 
     return Boolean.valueOf(false);
   }
 
   
   public void update() {
     final TaskInsertKeysInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("insertKeys_info").getItem().getItem());
     
     ItemInfoContainer keyItem = Main.getItemsManager().getItem("insertKeys_key");
     ItemStack keyItemS = keyItem.getItem().getItem();
     
     Integer i = Integer.valueOf(0);
     for (Integer slot : keysSlots) {
       Boolean isPlayerKey = Boolean.valueOf((i == getPlayerInfo().getJoinedId()));
       ItemStack item_ = isPlayerKey.booleanValue() ? keyItem.getItem2().getItem() : keyItemS;
       if (isPlayerKey.booleanValue() && this.isDone.booleanValue()) {
         item_ = Utils.enchantedItem(item_, Enchantment.DURABILITY, 1);
       }
       Icon icon = new Icon(item_);
       if (isPlayerKey.booleanValue()) {
         final Integer i_ = i;
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv.keyClick(i_);
               }
             });
       } 
       setIcon(slot, icon);
       i = Integer.valueOf(i + 1);
     } 
   }
   
   public void invClosed() {}
 }


