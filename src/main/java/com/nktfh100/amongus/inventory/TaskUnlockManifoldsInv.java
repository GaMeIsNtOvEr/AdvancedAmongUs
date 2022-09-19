 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class TaskUnlockManifoldsInv
   extends TaskInvHolder
 {
   private ArrayList<Integer> numbers = new ArrayList<>();
   private Integer activeNum = Integer.valueOf(0);
   private Boolean isRedActive = Boolean.valueOf(false);
   private Boolean isRed = Boolean.valueOf(false);
 
   
   public TaskUnlockManifoldsInv(Arena arena, TaskPlayer taskPlayer, ArrayList<Integer> numbers_) {
     super(36, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     if (numbers_ == null) {
       this.numbers = generateNumbers();
     } else {
       this.numbers = numbers_;
     } 
     update();
   }
   
   public void numberClick(Player player, Integer num) {
     if (this.isRedActive.booleanValue()) {
       return;
     }
     if (this.activeNum == num - 1) {
       this.activeNum = Integer.valueOf(this.activeNum + 1);
       Main.getSoundsManager().playSound("taskUnlockManifoldsClick" + num, player, player.getLocation());
       update();
       checkDone();
     } else {
       Main.getSoundsManager().playSound("taskUnlockManifoldsClickWrong", player, player.getLocation());
       this.activeNum = Integer.valueOf(0);
       this.isRedActive = Boolean.valueOf(true);
       this.isRed = Boolean.valueOf(true);
       update();
       final TaskUnlockManifoldsInv unlockManifoldsInv = this;
       (new BukkitRunnable() {
           Integer i = Integer.valueOf(0);
 
           
           public void run() {
             if (this.i >= 2) {
               unlockManifoldsInv.setIsRedActive(Boolean.valueOf(false));
               unlockManifoldsInv.setIsRed(Boolean.valueOf(false));
               unlockManifoldsInv.update();
               cancel();
               return;
             } 
             unlockManifoldsInv.setIsRed(Boolean.valueOf(!unlockManifoldsInv.getIsRed().booleanValue()));
             unlockManifoldsInv.update();
             this.i = Integer.valueOf(this.i + 1);
           }
         }).runTaskTimer(Main.getPlugin(), 8L, 8L);
     } 
   }
   
   public static ArrayList<Integer> generateNumbers() {
     ArrayList<Integer> out = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8), Integer.valueOf(9), Integer.valueOf(10) }));
     Collections.shuffle(out);
     return out;
   }
 
   
   public Boolean checkDone() {
     if (this.activeNum < 10) {
       return Boolean.valueOf(false);
     }
     this.taskPlayer.taskDone();
     final TaskUnlockManifoldsInv unlockManifoldsInv = this;
     (new BukkitRunnable()
       {
         public void run() {
           Player player = unlockManifoldsInv.getTaskPlayer().getPlayerInfo().getPlayer();
           if (player.getOpenInventory().getTopInventory() == unlockManifoldsInv.getInventory()) {
             player.closeInventory();
           }
         }
       }).runTaskLater(Main.getPlugin(), 15L);
     return Boolean.valueOf(true);
   }
 
 
   
   public void update() {
     this.inv.setItem(8, Main.getItemsManager().getItem("unlockManifolds_info").getItem().getItem());
     
     final TaskUnlockManifoldsInv unlockManifoldsInv = this;
     
     Integer slot = Integer.valueOf(11);
     for (Integer num : this.numbers) {
       ItemInfoContainer squareItem = Main.getItemsManager().getItem("unlockManifolds_square" + num);
       ItemStack squareItemS_ = this.isRed.booleanValue() ? squareItem.getItem3().getItem() : ((this.activeNum >= num) ? squareItem.getItem2().getItem() : squareItem.getItem().getItem());
       squareItemS_.setAmount(num);
       Icon icon = new Icon(squareItemS_);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               unlockManifoldsInv.numberClick(player, num);
             }
           });
       
       setIcon(slot, icon);
       slot = Integer.valueOf(slot + 1);
       if (slot == 16) {
         slot = Integer.valueOf(20);
       }
     } 
   }
 
 
   
   public void invClosed() {}
 
   
   public ArrayList<Integer> getNumbers() { return this.numbers; }
 
 
   
   public Integer getActiveNum() { return this.activeNum; }
 
 
   
   public void setActiveNum(Integer activeNum) { this.activeNum = activeNum; }
 
 
   
   public void setIsRed(Boolean is) { this.isRed = is; }
 
 
   
   public Boolean getIsRed() { return this.isRed; }
 
 
   
   public Boolean getIsRedActive() { return this.isRedActive; }
 
 
   
   public void setIsRedActive(Boolean isRedActive) { this.isRedActive = isRedActive; }
 }


