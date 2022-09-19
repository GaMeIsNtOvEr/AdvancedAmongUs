 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 public class TaskSwitchWeatherNodeInv
   extends TaskInvHolder
 {
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskSwitchWeatherNodeInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public void handleClick() {
     if (this.isDone.booleanValue()) {
       return;
     }
     this.isDone = Boolean.valueOf(true);
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskSwitchWeatherNodeInv inv = this;
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
     final TaskSwitchWeatherNodeInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("switchWeatherNode_info").getItem().getItem());
     
     ItemInfoContainer nodeItemInfoC = Main.getItemsManager().getItem("switchWeatherNode_node");
     ItemInfoContainer node2ItemInfoC = Main.getItemsManager().getItem("switchWeatherNode_node2");
     ItemInfoContainer nodeActiveItemInfoC = Main.getItemsManager().getItem("switchWeatherNode_nodeActive");
     ItemInfoContainer nodeActiveDoneItemInfoC = Main.getItemsManager().getItem("switchWeatherNode_nodeActiveDone");
     
     this.inv.setItem(2, nodeItemInfoC.getItem().getItem());
     this.inv.setItem(3, nodeItemInfoC.getItem2().getItem());
     this.inv.setItem(4, nodeItemInfoC.getItem2().getItem());
     this.inv.setItem(5, nodeItemInfoC.getItem2().getItem());
     this.inv.setItem(6, nodeItemInfoC.getItem3().getItem());
     
     if (this.isDone.booleanValue()) {
       this.inv.setItem(20, nodeActiveDoneItemInfoC.getItem().getItem());
       this.inv.setItem(21, nodeActiveDoneItemInfoC.getItem2().getItem());
       this.inv.setItem(22, nodeActiveDoneItemInfoC.getItem2().getItem());
       this.inv.setItem(23, nodeActiveDoneItemInfoC.getItem2().getItem());
       this.inv.setItem(24, nodeActiveDoneItemInfoC.getItem3().getItem());
     } else {
       ItemStack item_ = nodeActiveItemInfoC.getItem().getItem();
       if (!this.isDone.booleanValue()) {
         Utils.enchantedItem(item_, Enchantment.DURABILITY, 1);
       }
       Icon icon = new Icon(item_);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleClick();
             }
           });
       setIcon(20, icon);
       this.inv.setItem(21, nodeActiveItemInfoC.getItem2().getItem());
       this.inv.setItem(22, nodeActiveItemInfoC.getItem2().getItem());
       this.inv.setItem(23, nodeActiveItemInfoC.getItem2().getItem());
       this.inv.setItem(24, nodeActiveItemInfoC.getItem3().getItem());
     } 
     
     this.inv.setItem(38, node2ItemInfoC.getItem().getItem());
     this.inv.setItem(39, node2ItemInfoC.getItem2().getItem());
     this.inv.setItem(40, node2ItemInfoC.getItem2().getItem());
     this.inv.setItem(41, node2ItemInfoC.getItem2().getItem());
     this.inv.setItem(42, node2ItemInfoC.getItem3().getItem());
   }
   
   public void invClosed() {}
 }


