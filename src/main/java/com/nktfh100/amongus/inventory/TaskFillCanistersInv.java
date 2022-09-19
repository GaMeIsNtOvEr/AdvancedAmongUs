 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
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
 
 
 public class TaskFillCanistersInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> leftCanisterSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(18), Integer.valueOf(19), Integer.valueOf(27), Integer.valueOf(28), Integer.valueOf(36) }));
   private static final ArrayList<Integer> outerCanisterSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(20), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(25), Integer.valueOf(24), Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(32), Integer.valueOf(31), Integer.valueOf(30) }));
   private static final ArrayList<Integer> canisterProgressSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(24) }));
   
   private Integer canisterProgress = Integer.valueOf(-1);
   private Boolean isCanisterFillingUp = Boolean.valueOf(false);
   private Boolean isCanisterDone = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
   private BukkitTask runnable = null;
   
   public TaskFillCanistersInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     ItemStack item = Main.getItemsManager().getItem("fillCanisters_leftCanister").getItem().getItem();
     for (Integer slot : leftCanisterSlots) {
       this.inv.setItem(slot, item);
     }
     
     update();
   }
   
   public void canisterClick() {
     if (this.isDone.booleanValue() || this.isCanisterDone.booleanValue()) {
       return;
     }
     final TaskFillCanistersInv inv = this;
     Main.getSoundsManager().playSound("taskFillCanistersClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     if (!this.isCanisterFillingUp.booleanValue()) {
       this.isCanisterFillingUp = Boolean.valueOf(true);
       if (this.runnable != null) {
         this.runnable.cancel();
       }
       this.runnable = (new BukkitRunnable()
         {
           public void run() {
             inv.progressTick();
           }
         }).runTaskTimer(Main.getPlugin(), 20L, 30L);
       update();
     } 
   }
   
   public void resetCanister() {
     this.canisterProgress = Integer.valueOf(-1);
     this.isCanisterDone = Boolean.valueOf(false);
     this.isCanisterFillingUp = Boolean.valueOf(false);
     if (this.runnable != null) {
       this.runnable.cancel();
     }
     this.runnable = null;
   }
   
   public void progressTick() {
     Main.getSoundsManager().playSound("taskFillCanistersLoading", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     this.canisterProgress = Integer.valueOf(this.canisterProgress + 1);
     if (this.canisterProgress >= 3) {
       Main.getSoundsManager().playSound("taskFillCanistersLoadingDone", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
       final TaskFillCanistersInv inv = this;
       update();
       getTaskPlayer().setCanistersLeft_(Integer.valueOf(getTaskPlayer().getCanistersLeft_() - 1));
       this.isCanisterDone = Boolean.valueOf(true);
       if (this.taskPlayer.getCanistersLeft_() <= 0) {
         this.isDone = Boolean.valueOf(true);
       }
       if (this.runnable != null) {
         this.runnable.cancel();
       }
       this.runnable = (new BukkitRunnable()
         {
           public void run() {
             inv.resetCanister();
             inv.update();
             inv.checkDone();
           }
         }).runTaskLater(Main.getPlugin(), 20L);
     } 
     update();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskFillCanistersInv inv = this;
       (new BukkitRunnable()
         {
           public void run() {
             Player player = inv.getTaskPlayer().getPlayerInfo().getPlayer();
             if (player.getOpenInventory().getTopInventory() == inv.getInventory()) {
               player.closeInventory();
             }
           }
         }).runTaskLater(Main.getPlugin(), 15L);
       return Boolean.valueOf(true);
     } 
     return Boolean.valueOf(false);
   }
 
   
   public void update() {
     final TaskFillCanistersInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("fillCanisters_info").getItem().getItem());
     
     ItemInfoContainer outerCanisterItem = Main.getItemsManager().getItem("fillCanisters_outerCanister");
     ItemStack outerCanisterItemS = null;
     if (!this.isCanisterFillingUp.booleanValue()) {
       outerCanisterItemS = outerCanisterItem.getItem().getItem();
     } else if (this.canisterProgress < 3) {
       outerCanisterItemS = outerCanisterItem.getItem2().getItem();
     } else if (this.isCanisterDone.booleanValue()) {
       outerCanisterItemS = outerCanisterItem.getItem3().getItem();
     } 
     if (!this.isDone.booleanValue()) {
       
       for (Integer slot : outerCanisterSlots) {
         Icon icon = new Icon(outerCanisterItemS);
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv.canisterClick();
               }
             });
         setIcon(slot, icon);
       } 
       
       ItemInfoContainer canisterProgressItem = Main.getItemsManager().getItem("fillCanisters_canisterProgress");
       ItemStack canisterProgressItemS = canisterProgressItem.getItem().getItem();
       ItemStack canisterProgressItem2S = canisterProgressItem.getItem2().getItem();
       ItemStack canisterProgressItem3S = canisterProgressItem.getItem3().getItem();
       
       ItemStack canisterProgressDoneItemS = Main.getItemsManager().getItem("fillCanisters_canisterProgressDone").getItem().getItem();
       
       for (int i = 0; i < 4; i++) {
         Icon icon = new Icon(canisterProgressItemS);
         if (this.isCanisterFillingUp.booleanValue()) {
           if (this.canisterProgress >= i) {
             if (this.isCanisterDone.booleanValue()) {
               icon = new Icon(canisterProgressDoneItemS);
             } else {
               icon = new Icon(canisterProgressItem3S);
             } 
           } else {
             icon = new Icon(canisterProgressItem2S);
           } 
         }
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv.canisterClick();
               }
             });
         setIcon(((Integer)canisterProgressSlots.get(i)), icon);
       } 
     } else {
       ItemStack item = Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
       for (Integer slot : outerCanisterSlots) {
         setIcon(slot, new Icon(item));
       }
       for (Integer slot : canisterProgressSlots) {
         setIcon(slot, new Icon(item));
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


