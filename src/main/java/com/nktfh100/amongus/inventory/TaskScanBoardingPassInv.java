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
 
 
 public class TaskScanBoardingPassInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> passSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(19), Integer.valueOf(21), Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30) }));
   private static final ArrayList<Integer> scannerSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(23), Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34) }));
   private Boolean isPassClicked = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskScanBoardingPassInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public void handlePassClick() {
     if (this.isDone.booleanValue() || this.isPassClicked.booleanValue()) {
       return;
     }
     
     Main.getSoundsManager().playSound("taskScanBoardingPass_cardClick", getPlayerInfo().getPlayer(), getPlayerInfo().getPlayer().getLocation());
     this.isPassClicked = Boolean.valueOf(true);
     update();
   }
   
   public void handleScannerClick() {
     if (this.isDone.booleanValue() || !this.isPassClicked.booleanValue()) {
       return;
     }
     
     Main.getSoundsManager().playSound("taskScanBoardingPass_scannerClick", getPlayerInfo().getPlayer(), getPlayerInfo().getPlayer().getLocation());
     
     this.isDone = Boolean.valueOf(true);
     
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskScanBoardingPassInv inv = this;
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
     final TaskScanBoardingPassInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("scanBoardingPass_info").getItem().getItem());
     
     ItemInfoContainer scannerItemInfo = Main.getItemsManager().getItem("scanBoardingPass_scanner");
     ItemStack scannerItemS = scannerItemInfo.getItem().getItem();
     if (this.isDone.booleanValue()) {
       scannerItemS = scannerItemInfo.getItem3().getItem();
     } else if (this.isPassClicked.booleanValue()) {
       scannerItemS = scannerItemInfo.getItem2().getItem();
     } 
     Icon scannerIcon = new Icon(scannerItemS);
     scannerIcon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             inv.handleScannerClick();
           }
         });
     for (Integer slot : scannerSlots) {
       setIcon(slot, scannerIcon);
     }
     
     ItemInfoContainer cardItemInfo = Main.getItemsManager().getItem("scanBoardingPass_card");
     ItemStack cardItemS = cardItemInfo.getItem().getItem();
     if (this.isPassClicked.booleanValue()) {
       cardItemS = Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " ");
     }
     Icon cardIcon = new Icon(cardItemS);
     if (!this.isPassClicked.booleanValue()) {
       cardIcon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handlePassClick();
             }
           });
     }
     for (Integer slot : passSlots) {
       setIcon(slot, cardIcon);
     }
     if (this.isPassClicked.booleanValue()) {
       setIcon(20, cardIcon);
     } else {
       setIcon(20, new Icon(cardItemInfo.getItem2().getItem(getPlayerInfo().getPlayer().getName(), null)));
     } 
   }
   
   public void invClosed() {}
 }


