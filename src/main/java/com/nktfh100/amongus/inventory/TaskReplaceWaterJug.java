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
 
 
 public class TaskReplaceWaterJug
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> buttonSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32) }));
   private static final ArrayList<ArrayList<Integer>> leftJugSlots = new ArrayList<>();
   private static final ArrayList<ArrayList<Integer>> rightJugSlots = new ArrayList<>();
   
   static  {
     leftJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(11) })));
     leftJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(18), Integer.valueOf(19), Integer.valueOf(20) })));
     leftJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(27), Integer.valueOf(28), Integer.valueOf(29) })));
     leftJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(36), Integer.valueOf(37), Integer.valueOf(38) })));
     
     rightJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(17) })));
     rightJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(26) })));
     rightJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(35) })));
     rightJugSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(42), Integer.valueOf(43), Integer.valueOf(44) })));
   }
   
   private Boolean isRunning = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
   private BukkitTask runnable = null;
   
   public TaskReplaceWaterJug(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     update();
   }
   
   public void handleButtonClick() {
     if (this.isDone.booleanValue()) {
       return;
     }
     this.isRunning = Boolean.valueOf(!this.isRunning.booleanValue());
     Main.getSoundsManager().playSound("taskReplaceWaterJug_buttonClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     if (this.isRunning.booleanValue()) {
       final TaskReplaceWaterJug inv = this;
       this.runnable = (new BukkitRunnable()
         {
           public void run() {
             inv.progressTick();
           }
         }).runTaskTimer(Main.getPlugin(), 20L, 25L);
     } else {
       this.runnable.cancel();
       this.runnable = null;
     } 
     update();
   }
   
   public void progressTick() {
     this.taskPlayer.setWaterJugProgress_(Integer.valueOf(this.taskPlayer.getWaterJugProgress_() + 1));
     if (this.taskPlayer.getWaterJugProgress_() >= 4) {
       this.isDone = Boolean.valueOf(true);
       this.runnable.cancel();
       this.runnable = null;
     } 
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskReplaceWaterJug inv = this;
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
     final TaskReplaceWaterJug inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("replaceWaterJug_info").getItem().getItem());
     
     ItemInfoContainer buttonItem = Main.getItemsManager().getItem("replaceWaterJug_button");
     ItemStack buttonItemS = this.isRunning.booleanValue() ? buttonItem.getItem2().getItem() : buttonItem.getItem().getItem();
     
     for (Integer slot : buttonSlots) {
       Icon icon = new Icon(buttonItemS);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleButtonClick();
             }
           });
       setIcon(slot, icon);
     } 
     
     ItemInfoContainer jugItem = Main.getItemsManager().getItem("replaceWaterJug_waterJug");
     ItemStack jugItemS = jugItem.getItem().getItem();
     ItemStack jugItem2S = jugItem.getItem2().getItem();
     
     Integer progress = this.taskPlayer.getWaterJugProgress_();
     
     for (int i = 0; i < rightJugSlots.size(); i++) {
       for (Integer slot : rightJugSlots.get(i)) {
         if (i < progress) {
           this.inv.setItem(slot, jugItemS); continue;
         } 
         this.inv.setItem(slot, jugItem2S);
       } 
     } 
     
     progress = Integer.valueOf(4 - progress);
     for (int i = 0; i < leftJugSlots.size(); i++) {
       for (Integer slot : leftJugSlots.get(i)) {
         if (i < progress) {
           this.inv.setItem(slot, jugItemS); continue;
         } 
         this.inv.setItem(slot, jugItem2S);
       } 
     } 
   }
 
 
   
   public void invClosed() {
     if (this.runnable != null) {
       this.runnable.cancel();
       this.runnable = null;
     } 
   }
 }


