 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Random;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class TaskFixWeatherNodeInv
   extends TaskInvHolder
 {
   private Integer activeLocation = Integer.valueOf(0);
   private Boolean isHeadClicked = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskFixWeatherNodeInv(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public static ArrayList<Integer> generateMaze() {
     ArrayList<Integer> out = new ArrayList<>();
     Boolean isOk = Boolean.valueOf(false);
     Integer slot = Integer.valueOf(10);
     out.add(Integer.valueOf(1));
     out.add(Integer.valueOf(10));
     while (!isOk.booleanValue()) {
       float rand = (new Random()).nextFloat();
       int nextMove = 0;
       if (rand >= 0.33333333333D && rand <= 0.66666666666D) {
         nextMove = 1;
       }
       if (rand > 0.66666666666D) {
         nextMove = 2;
       }
       if (slot == 7 || slot == 16 || slot == 25 || slot == 34 || slot == 43) {
         nextMove = 0;
       }
       if (slot == 45 || slot == 46) {
         nextMove = 2;
       }
       
       if (nextMove == 0 && slot >= 36 && slot <= 42) {
         nextMove = 1;
       }
       
       if (nextMove == 1 && slot >= 10 && slot <= 16) {
         nextMove = 0;
       }
       
       if (nextMove == 1 && out.contains(Integer.valueOf(slot - 10))) {
         nextMove = 2;
       }
       
       if (nextMove == 0 && out.contains(Integer.valueOf(slot + 9))) {
         nextMove = 2;
       }
       
       if (nextMove == 1 && out.contains(Integer.valueOf(slot - 9))) {
         nextMove = 2;
       }
       
       if (nextMove == 0) {
         slot = Integer.valueOf(slot + 9);
       }
       if (nextMove == 1) {
         slot = Integer.valueOf(slot - 9);
       }
       if (nextMove == 2) {
         slot = Integer.valueOf(slot + 1);
       }
       
       if (slot < 53) {
         out.add(slot);
       }
       
       if (slot >= 52) {
         isOk = Boolean.valueOf(true);
         break;
       } 
     } 
     return out;
   }
   
   public void handleHeadClick() {
     if (this.isDone.booleanValue() || this.isHeadClicked.booleanValue()) {
       return;
     }
     this.isHeadClicked = Boolean.valueOf(true);
     update();
   }
   
   public void handleTargetClick() {
     if (this.isDone.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("taskFixWeatherNode_click", getPlayerInfo().getPlayer(), getPlayerInfo().getPlayer().getLocation());
     this.isHeadClicked = Boolean.valueOf(false);
     this.activeLocation = Integer.valueOf(this.activeLocation + 1);
     if (this.activeLocation >= this.taskPlayer.getMaze_().size() - 1) {
       this.isDone = Boolean.valueOf(true);
     }
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskFixWeatherNodeInv inv = this;
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
     final TaskFixWeatherNodeInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("fixWeatherNode_info").getItem().getItem());
     
     ItemStack mazeBGItemS = Main.getItemsManager().getItem("fixWeatherNode_mazeBG").getItem().getItem();
     ItemInfoContainer mazeItemInfo = Main.getItemsManager().getItem("fixWeatherNode_maze");
     ItemStack mazeItem = mazeItemInfo.getItem().getItem();
     ItemStack mazeHeadItemS = mazeItemInfo.getItem2().getItem();
     ItemStack mazeTargetItemS = mazeItemInfo.getItem3().getItem();
     if (this.isHeadClicked.booleanValue()) {
       Utils.enchantedItem(mazeHeadItemS, Enchantment.DURABILITY, 1);
     }
     
     int i = 0;
     for (Integer slot : this.taskPlayer.getMaze_()) {
       if (this.activeLocation == i) {
         Icon icon = new Icon(mazeHeadItemS);
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv.handleHeadClick();
               }
             });
         setIcon(slot, icon);
       } else if (this.activeLocation + 1 == i && this.isHeadClicked.booleanValue()) {
         Icon icon = new Icon(mazeTargetItemS);
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv.handleTargetClick();
               }
             });
         setIcon(slot, icon);
       } else if (this.activeLocation < i) {
         Icon icon = new Icon(mazeBGItemS);
         setIcon(slot, icon);
       } else {
         Icon icon = new Icon(mazeItem);
         setIcon(slot, icon);
       } 
       i++;
     } 
   }
   
   public void invClosed() {}
 }


