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
 
 
 public class TaskStabilizeSteeringInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> availableSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(43) }));
   
   private Integer activeSlot = Integer.valueOf(0);
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskStabilizeSteeringInv(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     this.activeSlot = availableSlots.get(Utils.getRandomNumberInRange(0, availableSlots.size() - 1));
     
     update();
   }
   
   public void crosshairClick() {
     if (!this.isDone.booleanValue()) {
       Main.getSoundsManager().playSound("taskStabilizeSteeringClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
       this.isDone = Boolean.valueOf(true);
       update();
       checkDone();
     } 
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskStabilizeSteeringInv inv = this;
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
     final TaskStabilizeSteeringInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("StabilizeSteering_info").getItem().getItem());
     
     ItemInfoContainer crosshairItem = Main.getItemsManager().getItem("StabilizeSteering_crosshair");
     ItemStack crosshairItemS = this.isDone.booleanValue() ? crosshairItem.getItem2().getItem() : crosshairItem.getItem().getItem();
     
     ClickAction action = new ClickAction()
       {
         public void execute(Player player) {
           inv.crosshairClick();
         }
       };
     
     Icon icon = new Icon(crosshairItemS);
     icon.addClickAction(action);
     setIcon(this.activeSlot, icon);
     
     icon = new Icon(crosshairItemS);
     icon.addClickAction(action);
     setIcon(this.activeSlot + 1, icon);
     
     icon = new Icon(crosshairItemS);
     icon.addClickAction(action);
     setIcon(this.activeSlot - 1, icon);
     
     icon = new Icon(crosshairItemS);
     icon.addClickAction(action);
     setIcon(this.activeSlot - 9, icon);
     
     icon = new Icon(crosshairItemS);
     icon.addClickAction(action);
     setIcon(this.activeSlot + 9, icon);
   }
   
   public void invClosed() {}
 }


