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
 
 
 public class TaskPrimeShieldsInv
   extends TaskInvHolder
 {
   private static ArrayList<ArrayList<Integer>> slots = new ArrayList<>();
   
   static  {
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(19), Integer.valueOf(20) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(12), Integer.valueOf(13) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(23), Integer.valueOf(24) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(37), Integer.valueOf(38) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(48), Integer.valueOf(49) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(41), Integer.valueOf(42) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(30), Integer.valueOf(31) })));
   }
   
   private ArrayList<Boolean> squares = new ArrayList<>();
   private Boolean isDone = Boolean.valueOf(false);
 
   
   public TaskPrimeShieldsInv(Arena arena, TaskPlayer taskPlayer, ArrayList<Boolean> squares_) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     if (squares_ == null || squares_.size() == 0) {
       this.squares = generateShields();
     } else {
       this.squares = squares_;
     } 
     
     update();
   }
   
   public static ArrayList<Boolean> generateShields() {
     ArrayList<Boolean> out = new ArrayList<>();
     Integer offNum = Integer.valueOf(Utils.getRandomNumberInRange(3, 6));
     for (int i = 0; i < 7; i++) {
       if (offNum > 0) {
         out.add(Boolean.valueOf(false));
         offNum = Integer.valueOf(offNum - 1);
       } else {
         out.add(Boolean.valueOf(true));
       } 
     } 
     Collections.shuffle(out);
     return out;
   }
   
   public void handleSquareClick(Player player, Integer clicked) {
     if (this.isDone.booleanValue()) {
       return;
     }
     Boolean newSquareState = Boolean.valueOf(!((Boolean)this.squares.get(clicked)).booleanValue());
     this.squares.set(clicked, newSquareState);
     
     if (newSquareState.booleanValue()) {
       Main.getSoundsManager().playSound("taskPrimeShieldsClickOn", player, player.getLocation());
     } else {
       Main.getSoundsManager().playSound("taskPrimeShieldsClickOff", player, player.getLocation());
     } 
     
     Boolean isDone_ = Boolean.valueOf(true);
     for (Boolean square : this.squares) {
       if (!square.booleanValue()) {
         isDone_ = Boolean.valueOf(false);
         break;
       } 
     } 
     if (isDone_.booleanValue()) {
       this.isDone = Boolean.valueOf(true);
     }
     
     checkDone();
     update();
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       if (this.arena.getEnableVisualTasks().booleanValue() && this.taskPlayer.getActiveTask().getEnableVisuals().booleanValue() && !this.pInfo.isGhost().booleanValue()) {
         this.arena.turnPrimeShieldsOn();
       }
       this.taskPlayer.taskDone();
       final TaskPrimeShieldsInv taskInv = this;
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
     this.inv.setItem(8, Main.getItemsManager().getItem("primeShields_info").getItem().getItem());
     
     final TaskPrimeShieldsInv inv = this;
     ItemInfoContainer squareItem = Main.getItemsManager().getItem("primeShields_square");
     for (int i = 0; i < this.squares.size(); i++) {
       Boolean square = this.squares.get(i);
       ItemStack squareItemS = square.booleanValue() ? squareItem.getItem().getItem() : squareItem.getItem2().getItem();
       Icon icon = new Icon(squareItemS);
       final Integer squareI = Integer.valueOf(i);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleSquareClick(player, squareI);
             }
           });
       for (Integer slot_ : slots.get(i)) {
         setIcon(slot_, icon);
       }
     } 
   }
 
 
   
   public void invClosed() {}
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


