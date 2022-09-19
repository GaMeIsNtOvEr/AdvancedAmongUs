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
 
 
 public class TaskAcceptDivertedPowerInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> leftSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(11), Integer.valueOf(18), Integer.valueOf(19), Integer.valueOf(36), Integer.valueOf(37), Integer.valueOf(20), Integer.valueOf(29) }));
   private static final ArrayList<Integer> rightSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(15), Integer.valueOf(7), Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(26), Integer.valueOf(33), Integer.valueOf(43), Integer.valueOf(44) }));
   
   private Boolean isDone = Boolean.valueOf(false);
 
   
   public TaskAcceptDivertedPowerInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     update();
   }
   
   public void handleSwitchClick() {
     if (!this.isDone.booleanValue()) {
       Main.getSoundsManager().playSound("taskAcceptDivertedPower_click", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
       this.isDone = Boolean.valueOf(true);
       checkDone();
       update();
     } 
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskAcceptDivertedPowerInv taskInv = this;
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
     this.inv.setItem(8, Main.getItemsManager().getItem("acceptDivertedPower_info").getItem().getItem());
     
     final TaskAcceptDivertedPowerInv inv = this;
     ItemInfoContainer wireItem = Main.getItemsManager().getItem("acceptDivertedPower_line");
     ItemStack wireItemS = wireItem.getItem().getItem();
     ItemStack wireItemS2 = wireItem.getItem2().getItem();
     
     for (Integer slot : leftSlots) {
       this.inv.setItem(slot, wireItemS);
     }
     
     for (Integer slot : rightSlots) {
       if (this.isDone.booleanValue()) {
         this.inv.setItem(slot, wireItemS); continue;
       } 
       this.inv.setItem(slot, wireItemS2);
     } 
     
     if (this.isDone.booleanValue()) {
       ItemInfoContainer switchItem = Main.getItemsManager().getItem("acceptDivertedPower_switchActive");
       setIcon(13, new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ")));
       setIcon(31, new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ")));
       
       setIcon(21, new Icon(switchItem.getItem().getItem()));
       setIcon(22, new Icon(switchItem.getItem2().getItem()));
       setIcon(23, new Icon(switchItem.getItem().getItem()));
     } else {
       ItemInfoContainer switchItem = Main.getItemsManager().getItem("acceptDivertedPower_switch");
       Icon icon = new Icon(switchItem.getItem().getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleSwitchClick();
             }
           });
       setIcon(13, icon);
       setIcon(31, icon);
       
       Icon icon1 = new Icon(switchItem.getItem2().getItem());
       icon1.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleSwitchClick();
             }
           });
       setIcon(22, icon1);
     } 
   }
 
 
   
   public void invClosed() {}
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


