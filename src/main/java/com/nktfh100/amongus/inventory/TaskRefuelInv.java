 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 import org.bukkit.scheduler.BukkitTask;
 
 
 public class TaskRefuelInv
   extends TaskInvHolder
 {
   private static ArrayList<ArrayList<Integer>> slots = new ArrayList<>();
   
   static  {
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13) })));
   }
   
   private Boolean isDone = Boolean.valueOf(false);
   private Boolean isRunning = Boolean.valueOf(false);
   private Integer progress = Integer.valueOf(0);
   private BukkitTask runnable = null;
 
   
   public TaskRefuelInv(Arena arena, TaskPlayer taskPlayer, Integer progress) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     this.progress = progress;
     final TaskRefuelInv inv = this;
     this.runnable = (new BukkitRunnable()
       {
         public void run() {
           if (inv.getIsDone().booleanValue()) {
             cancel();
             return;
           } 
           if (inv.getIsRunning().booleanValue()) {
             inv.tick();
           }
         }
       }).runTaskTimer(Main.getPlugin(), 20L, 20L);
     update();
   }
   
   public void handleClick(Player player) {
     if (this.isDone.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("taskRefuelLeverlClick", player, player.getLocation());
     this.isRunning = Boolean.valueOf(!this.isRunning.booleanValue());
     
     checkDone();
     update();
   }
   
   public void tick() {
     this.progress = Integer.valueOf(this.progress + 1);
     this.taskPlayer.setFuelProgress_(this.progress);
     if (this.progress >= 4) {
       this.isDone = Boolean.valueOf(true);
     }
     checkDone();
     update();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskRefuelInv taskInv = this;
       (new BukkitRunnable()
         {
           public void run() {
             Player player = taskInv.getTaskPlayer().getPlayerInfo().getPlayer();
             if (player.getOpenInventory().getTopInventory() == taskInv.getInventory()) {
               player.closeInventory();
             }
           }
         }).runTaskLater(Main.getPlugin(), 20L);
       return Boolean.valueOf(true);
     } 
     return Boolean.valueOf(false);
   }
 
   
   public void update() {
     this.inv.setItem(8, Main.getItemsManager().getItem("refuel_info").getItem().getItem());
     
     final TaskRefuelInv inv = this;
     ItemInfoContainer fuelItem = Main.getItemsManager().getItem("refuel_fuel");
     ItemStack fuelItemS = fuelItem.getItem().getItem();
     ItemStack fuelItemS2 = fuelItem.getItem2().getItem();
     
     for (int i = 0; i < slots.size(); i++) {
       for (Integer slot : slots.get(i)) {
         if (i < this.progress) {
           this.inv.setItem(slot, fuelItemS); continue;
         } 
         this.inv.setItem(slot, fuelItemS2);
       } 
     } 
 
     
     ItemInfoContainer buttonItem = Main.getItemsManager().getItem("refuel_button");
     Icon icon = new Icon(this.isRunning.booleanValue() ? buttonItem.getItem2().getItem() : buttonItem.getItem().getItem());
     icon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             inv.handleClick(player);
           }
         });
     setIcon(33, icon);
     setIcon(34, icon);
     setIcon(42, icon);
     setIcon(43, icon);
     
     ItemInfoContainer isRunningItem = Main.getItemsManager().getItem("refuel_isRunning");
     icon = new Icon(this.isRunning.booleanValue() ? isRunningItem.getItem2().getItem() : isRunningItem.getItem().getItem());
     setIcon(24, icon);
     
     ItemInfoContainer isDoneItem = Main.getItemsManager().getItem("refuel_isDone");
     icon = new Icon(this.isDone.booleanValue() ? isDoneItem.getItem2().getItem() : isDoneItem.getItem().getItem());
     setIcon(25, icon);
   }
 
   
   public void invClosed() {
     if (this.runnable != null) {
       this.runnable.cancel();
       this.runnable = null;
     } 
   }
 
   
   public Boolean getIsRunning() { return this.isRunning; }
 
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


