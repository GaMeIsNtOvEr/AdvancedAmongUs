 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 import org.bukkit.scheduler.BukkitTask;
 
 
 public class TaskCalibrateDistributorInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> circleSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(30), Integer.valueOf(29), Integer.valueOf(28), Integer.valueOf(19), Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12) }));
   
   private Boolean isDone = Boolean.valueOf(false);
   private Integer activeCircle = Integer.valueOf(0);
   private Integer activeCursor = Integer.valueOf(Utils.getRandomNumberInRange(1, 5));
   private BukkitTask runnable = null;
 
   
   public TaskCalibrateDistributorInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     this.inv.setItem(8, Main.getItemsManager().getItem("calibrateDistributor_info").getItem().getItem());
     
     final TaskCalibrateDistributorInv inv = this;
     ItemInfo btnItem = Main.getItemsManager().getItem("calibrateDistributor_button").getItem();
     int btnSlot = 23;
     for (int i = 0; i < 3; i++) {
       Icon icon = new Icon(btnItem.getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleButtonClick(player);
             }
           });
       setIcon(btnSlot, icon);
       btnSlot++;
     } 
     
     ItemInfo midItem = Main.getItemsManager().getItem("calibrateDistributor_middle").getItem();
     setIcon(22, new Icon(midItem.getItem()));
     
     update();
     this.runnable = (new BukkitRunnable()
       {
         public void run()
         {
           if (!inv.isDone.booleanValue()) {
             inv.updateCursor();
           } else {
             cancel();
           } 
         }
       }).runTaskTimer(Main.getPlugin(), 5L, 9L);
   }
   
   public void updateCursor() {
     this.activeCursor = Integer.valueOf(this.activeCursor + 1);
     if (this.activeCursor > 7) {
       this.activeCursor = Integer.valueOf(0);
     }
     update();
   }
   
   public void handleButtonClick(Player player) {
     if (this.isDone.booleanValue()) {
       return;
     }
     
     if (this.activeCursor == 0) {
       Main.getSoundsManager().playSound("taskCalibrateDistributorClickGood", player, player.getLocation());
       if (this.activeCircle == 2) {
         this.isDone = Boolean.valueOf(true);
         if (this.runnable != null) {
           this.runnable.cancel();
           this.runnable = null;
         } 
       } else {
         this.activeCircle = Integer.valueOf(this.activeCircle + 1);
         this.activeCursor = Integer.valueOf(Utils.getRandomNumberInRange(1, 5));
       } 
     } else {
       Main.getSoundsManager().playSound("taskCalibrateDistributorClickWrong", player, player.getLocation());
       this.activeCircle = Integer.valueOf(0);
       this.activeCursor = Integer.valueOf(Utils.getRandomNumberInRange(1, 5));
     } 
     
     checkDone();
     update();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskCalibrateDistributorInv taskInv = this;
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
     ItemInfoContainer barItem = Main.getItemsManager().getItem("calibrateDistributor_bar");
     ItemStack barItemS = barItem.getItem().getItem();
 
     
     ItemInfoContainer cursorItem = Main.getItemsManager().getItem("calibrateDistributor_cursor");
     ItemInfoContainer circleItem = Main.getItemsManager().getItem("calibrateDistributor_circle");
     ItemStack circleItemS = circleItem.getItem().getItem();
     if (this.activeCircle == 1) {
       circleItemS = circleItem.getItem2().getItem();
       
       barItemS = barItem.getItem2().getItem();
     } else if (this.activeCircle == 2) {
       circleItemS = circleItem.getItem3().getItem();
       
       barItemS = barItem.getItem().getItem();
     } 
     int circleSlot = 0;
     for (Integer slot : circleSlots) {
       Icon icon;
       if (circleSlot == this.activeCursor) {
         icon = new Icon(cursorItem.getItem().getItem());
       } else {
         icon = new Icon(circleItemS);
       } 
       setIcon(slot, icon);
       circleSlot++;
     } 
 
     
     Icon barIcon = new Icon(barItemS);
     setIcon(14, barIcon);
     if (this.activeCursor == 0) {
       setIcon(15, barIcon);
       setIcon(16, barIcon);
     } else {
       ItemStack bgItem = Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
       this.inv.setItem(15, bgItem);
       this.inv.setItem(16, bgItem);
     } 
     
     ItemInfo circleMidItem = Main.getItemsManager().getItem("calibrateDistributor_circleMiddle").getItem();
     ItemStack circleMidItemS = circleMidItem.getItem();
     circleMidItemS.setAmount(this.activeCircle + 1);
     setIcon(20, new Icon(circleMidItemS));
   }
 
   
   public void invClosed() {
     if (this.runnable != null) {
       this.runnable.cancel();
       this.runnable = null;
     } 
   }
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


