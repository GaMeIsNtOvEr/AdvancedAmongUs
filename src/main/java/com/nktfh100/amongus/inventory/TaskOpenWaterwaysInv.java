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
 
 
 public class TaskOpenWaterwaysInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> circleSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(23), Integer.valueOf(32), Integer.valueOf(31), Integer.valueOf(30), Integer.valueOf(21) }));
   private static final ArrayList<Integer> waterBarSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(44), Integer.valueOf(35), Integer.valueOf(26), Integer.valueOf(17), Integer.valueOf(8) }));
   
   private Integer waterProgress = Integer.valueOf(0);
   private Integer firstColor = Integer.valueOf(0);
   private long lastTimeClicked = System.currentTimeMillis() - 2000L;
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskOpenWaterwaysInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public void handleCircleClick() {
     if (this.isDone.booleanValue()) {
       return;
     }
     long finish = System.currentTimeMillis();
     long timeElapsed = finish - this.lastTimeClicked;
     if (timeElapsed > 1300L) {
       Main.getSoundsManager().playSound("taskOpenWaterways_valveClick", getPlayerInfo().getPlayer(), getPlayerInfo().getPlayer().getLocation());
       this.lastTimeClicked = System.currentTimeMillis();
       this.waterProgress = Integer.valueOf(this.waterProgress + 1);
       this.firstColor = Integer.valueOf((this.firstColor == 0) ? 1 : 0);
       if (this.waterProgress >= 5) {
         this.isDone = Boolean.valueOf(true);
       }
       update();
       checkDone();
     } 
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskOpenWaterwaysInv inv = this;
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
     final TaskOpenWaterwaysInv inv = this;
     
     this.inv.setItem(7, Main.getItemsManager().getItem("openWaterways_info").getItem().getItem());
     
     ItemInfoContainer circleItems = Main.getItemsManager().getItem("openWaterways_circle");
     ItemStack circleItemS = circleItems.getItem().getItem();
     ItemStack circleItem2S = circleItems.getItem2().getItem();
     
     ClickAction ca = new ClickAction()
       {
         public void execute(Player player) {
           inv.handleCircleClick();
         }
       };
     Integer color_ = this.firstColor;
     for (Integer slot : circleSlots) {
       Icon icon = new Icon((color_ == 0) ? circleItemS : circleItem2S);
       icon.addClickAction(ca);
       setIcon(slot, icon);
       if (slot == 21) {
         icon = new Icon((color_ == 0) ? circleItemS : circleItem2S);
         icon.addClickAction(ca);
         setIcon(20, icon);
       } else if (slot == 13) {
         icon = new Icon((color_ == 0) ? circleItemS : circleItem2S);
         icon.addClickAction(ca);
         setIcon(4, icon);
       } else if (slot == 23) {
         icon = new Icon((color_ == 0) ? circleItemS : circleItem2S);
         icon.addClickAction(ca);
         setIcon(24, icon);
       } else if (slot == 31) {
         icon = new Icon((color_ == 0) ? circleItemS : circleItem2S);
         icon.addClickAction(ca);
         setIcon(40, icon);
       } 
       color_ = Integer.valueOf((color_ == 0) ? 1 : 0);
     } 
     
     ItemInfoContainer waterBarItems = Main.getItemsManager().getItem("openWaterways_waterBar");
     ItemStack waterBarS = waterBarItems.getItem().getItem();
     ItemStack waterBar2S = waterBarItems.getItem2().getItem();
     
     for (int i = 0; i < 5; i++) {
       if (i < this.waterProgress) {
         this.inv.setItem(((Integer)waterBarSlots.get(i)), waterBar2S);
       } else {
         this.inv.setItem(((Integer)waterBarSlots.get(i)), waterBarS);
       } 
     } 
   }
   
   public void invClosed() {}
 }


