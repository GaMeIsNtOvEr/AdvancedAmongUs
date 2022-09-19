 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemFlag;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 public class TaskSwipeCardInv
   extends TaskInvHolder
 {
   private Boolean isCardClicked = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
 
   
   public TaskSwipeCardInv(Arena arena, TaskPlayer taskPlayer) {
     super(27, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     update();
   }
   
   public void handleBoxClick() {
     if (this.isDone.booleanValue() || !this.isCardClicked.booleanValue()) {
       return;
     }
     
     Main.getSoundsManager().playSound("taskSwipeCardBoxClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     
     this.isDone = Boolean.valueOf(true);
     
     checkDone();
     update();
   }
   
   public void handleCardClick() {
     if (this.isDone.booleanValue() || this.isCardClicked.booleanValue()) {
       return;
     }
     
     Main.getSoundsManager().playSound("taskSwipeCardClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     
     this.isCardClicked = Boolean.valueOf(true);
     
     update();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskSwipeCardInv taskInv = this;
       (new BukkitRunnable()
         {
           public void run() {
             Player player = taskInv.getTaskPlayer().getPlayerInfo().getPlayer();
             if (player.getOpenInventory().getTopInventory() == taskInv.getInventory()) {
               player.closeInventory();
             }
           }
         }).runTaskLater(Main.getPlugin(), 15L);
       return Boolean.valueOf(true);
     } 
     return Boolean.valueOf(false);
   }
 
   
   public void update() {
     final TaskSwipeCardInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("swipeCard_info").getItem().getItem());
     
     ItemStack cardItemS = this.isCardClicked.booleanValue() ? Main.getItemsManager().getItem("swipeCard_card").getItem2().getItem() : Main.getItemsManager().getItem("swipeCard_card").getItem().getItem();
     Utils.addItemFlag(cardItemS, ItemFlag.values());
     if (this.isCardClicked.booleanValue()) {
       Utils.enchantedItem(cardItemS, Enchantment.DURABILITY, 1);
     }
     Icon cardIcon = new Icon(cardItemS);
     if (!this.isCardClicked.booleanValue()) {
       cardIcon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleCardClick();
             }
           });
     }
     setIcon(10, cardIcon);
     
     Icon boxIcon = new Icon(Main.getItemsManager().getItem("swipeCard_box").getItem().getItem());
     if (this.isCardClicked.booleanValue()) {
       boxIcon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleBoxClick();
             }
           });
     }
     setIcon(16, boxIcon);
   }
 
 
   
   public void invClosed() {}
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


