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
 
 
 public class TaskRepairDrillInv
   extends TaskInvHolder
 {
   private static final ArrayList<ArrayList<Integer>> squaresSlots = new ArrayList<>();
   static  {
     squaresSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(10), Integer.valueOf(11) })));
     squaresSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(46), Integer.valueOf(47) })));
     squaresSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(15), Integer.valueOf(16) })));
     squaresSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(42), Integer.valueOf(43), Integer.valueOf(51), Integer.valueOf(52) })));
   }
   private ArrayList<Integer> squaresLeft = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(4), Integer.valueOf(4), Integer.valueOf(4), Integer.valueOf(4) }));
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskRepairDrillInv(Arena arena, TaskPlayer taskPlayer, Boolean isHot) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public void handleSquareClick(Integer i) {
     if (this.isDone.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("taskRepairDrill_click", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     if (((Integer)this.squaresLeft.get(i)) > 0) {
       this.squaresLeft.set(i, Integer.valueOf(((Integer)this.squaresLeft.get(i)) - 1));
     }
     Boolean isDone_ = Boolean.valueOf(true);
     for (Integer left : this.squaresLeft) {
       if (left != 0) {
         isDone_ = Boolean.valueOf(false);
         break;
       } 
     } 
     this.isDone = isDone_;
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskRepairDrillInv inv = this;
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
     final TaskRepairDrillInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("repairDrill_info").getItem().getItem());
     
     ItemInfoContainer squareItem = Main.getItemsManager().getItem("repairDrill_square");
     
     for (int i = 0; i < 4; i++) {
       ItemStack squareItemS = squareItem.getItem().getItem("" + this.squaresLeft.get(i), "");
       if (((Integer)this.squaresLeft.get(i)) == 0) {
         squareItemS = Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
       } else {
         squareItemS.setAmount(((Integer)this.squaresLeft.get(i)));
       } 
       Icon icon = new Icon(squareItemS);
       final Integer i_ = Integer.valueOf(i);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleSquareClick(i_);
             }
           });
       for (Integer slot : squaresSlots.get(i)) {
         setIcon(slot, icon);
       }
     } 
     
     ItemInfoContainer statusItem = Main.getItemsManager().getItem("repairDrill_status");
     this.inv.setItem(49, this.isDone.booleanValue() ? statusItem.getItem2().getItem() : statusItem.getItem().getItem());
   }
   
   public void invClosed() {}
 }


