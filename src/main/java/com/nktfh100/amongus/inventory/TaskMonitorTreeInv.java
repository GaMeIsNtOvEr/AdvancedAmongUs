 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class TaskMonitorTreeInv
   extends TaskInvHolder
 {
   private static final ArrayList<ArrayList<Integer>> barsSlots = new ArrayList<>();
   private static final ArrayList<String> barColors = new ArrayList<>(Arrays.asList(new String[] { "Yellow", "Green", "Red", "Blue" }));
   
   static  {
     barsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(46), Integer.valueOf(37), Integer.valueOf(28), Integer.valueOf(19), Integer.valueOf(10) })));
     barsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(48), Integer.valueOf(39), Integer.valueOf(30), Integer.valueOf(21), Integer.valueOf(12) })));
     barsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(50), Integer.valueOf(41), Integer.valueOf(32), Integer.valueOf(23), Integer.valueOf(14) })));
     barsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(52), Integer.valueOf(43), Integer.valueOf(34), Integer.valueOf(25), Integer.valueOf(16) })));
   }
   
   private ArrayList<Integer> barsHeight = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1), Integer.valueOf(-1) }));
   private ArrayList<Integer> barsTarget = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(Utils.getRandomNumberInRange(1, 4)), Integer.valueOf(Utils.getRandomNumberInRange(1, 4)), Integer.valueOf(Utils.getRandomNumberInRange(1, 4)), Integer.valueOf(Utils.getRandomNumberInRange(1, 4)) }));
   private Integer clickedBar = Integer.valueOf(-1);
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskMonitorTreeInv(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     update();
   }
   
   public void handleBarClick(Integer i) {
     if (this.isDone.booleanValue()) {
       return;
     }
     if (((Integer)this.barsHeight.get(i)) + 1 != ((Integer)this.barsTarget.get(i))) {
       this.clickedBar = i;
       Main.getSoundsManager().playSound("taskMonitorTree_barClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     } 
     update();
   }
   
   public void handleTargetClick(Integer i) {
     if (this.isDone.booleanValue()) {
       return;
     }
     if (this.clickedBar != -1) {
       Main.getSoundsManager().playSound("taskMonitorTree_barClickDone", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
       this.barsHeight.set(i, Integer.valueOf(((Integer)this.barsTarget.get(i)) - 1));
       this.clickedBar = Integer.valueOf(-1);
       Boolean isDone_ = Boolean.valueOf(true);
       for (int i1 = 0; i1 < 4; i1++) {
         if (((Integer)this.barsHeight.get(i1)) + 1 != ((Integer)this.barsTarget.get(i1))) {
           isDone_ = Boolean.valueOf(false);
           break;
         } 
       } 
       this.isDone = isDone_;
     } 
     update();
     checkDone();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskMonitorTreeInv inv = this;
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
     final TaskMonitorTreeInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("monitorTree_info").getItem().getItem());
     ItemInfoContainer barBGItem = Main.getItemsManager().getItem("monitorTree_barBG");
     ItemStack barBgItemS = barBGItem.getItem().getItem();
     
     for (int i = 0; i < 4; i++) {
       ItemInfoContainer barItem = Main.getItemsManager().getItem("monitorTree_bar" + (String)barColors.get(i));
       ItemStack barItem2S = barItem.getItem2().getItem();
       
       Integer height = this.barsHeight.get(i);
       Integer target = this.barsTarget.get(i);
       final Integer i_ = Integer.valueOf(i);
       for (int i1 = 0; i1 < 5; i1++) {
         if (i1 == 0) {
           ItemStack item_ = barItem.getItem().getItem();
           if (this.clickedBar == i) {
             Utils.enchantedItem(item_, Enchantment.DURABILITY, 1);
           }
           Icon icon = new Icon(item_);
           if (height + 1 != target) {
             icon.addClickAction(new ClickAction()
                 {
                   public void execute(Player player) {
                     inv.handleBarClick(i_);
                   }
                 });
           }
           setIcon(((Integer)((ArrayList<Integer>)barsSlots.get(i)).get(i1)), icon);
         
         }
         else if (height + 1 != target && i1 == target) {
           Icon icon = new Icon(barBGItem.getItem2().getItem());
           icon.addClickAction(new ClickAction()
               {
                 public void execute(Player player) {
                   inv.handleTargetClick(i_);
                 }
               });
           setIcon(((Integer)((ArrayList<Integer>)barsSlots.get(i)).get(i1)), icon);
         
         }
         else if (i1 > height + 1) {
           setIcon(((Integer)((ArrayList<Integer>)barsSlots.get(i)).get(i1)), new Icon(barBgItemS));
         } else {
           setIcon(((Integer)((ArrayList<Integer>)barsSlots.get(i)).get(i1)), new Icon(barItem2S));
         } 
       } 
     } 
   }
   
   public void invClosed() {}
 }


