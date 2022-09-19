 package com.nktfh100.amongus.inventory;
 
 import com.comphenix.protocol.events.PacketContainer;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Packets;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import org.bukkit.Bukkit;
 import org.bukkit.Material;
 import org.bukkit.Particle;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.scheduler.BukkitRunnable;
 import org.bukkit.scheduler.BukkitTask;
 
 
 public class TaskEmptyGarbageInv
   extends TaskInvHolder
 {
   private static ArrayList<ArrayList<Integer>> slots = new ArrayList<>();
   
   static  {
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23) })));
     slots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14) })));
   }
   
   private Boolean isDone = Boolean.valueOf(false);
   private ArrayList<Integer> randomRow = generateRandomRow();
   private Integer topRow = Integer.valueOf(3);
   private Boolean isRunning = Boolean.valueOf(false);
   private BukkitTask runnable = null;
 
   
   public TaskEmptyGarbageInv(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     final TaskEmptyGarbageInv inv = this;
     this.runnable = (new BukkitRunnable()
       {
         public void run() {
           if (inv.getIsDone().booleanValue()) {
             cancel();
             return;
           } 
           if (inv.getIsRunning().booleanValue()) {
             inv.tick();
           }
         }
       }).runTaskTimer(Main.getPlugin(), 10L, 22L);
     update();
   }
   
   private ArrayList<Integer> generateRandomRow() {
     ArrayList<Integer> out = new ArrayList<>();
     Integer offNum = Integer.valueOf(Utils.getRandomNumberInRange(1, 4));
     for (int i = 0; i < 5; i++) {
       if (offNum > 0) {
         out.add(Integer.valueOf(1));
         offNum = Integer.valueOf(offNum - 1);
       } else {
         out.add(Integer.valueOf(0));
       } 
     } 
     Collections.shuffle(out);
     return out;
   }
   
   public void handleClick(Player player) {
     if (this.isDone.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("taskEmptyGarbageLeverClick", player, player.getLocation());
     this.isRunning = Boolean.valueOf(!this.isRunning.booleanValue());
     
     checkDone();
     update();
   }
   
   public void tick() {
     this.topRow = Integer.valueOf(this.topRow - 1);
     if (this.topRow < 0) {
       this.isDone = Boolean.valueOf(true);
     }
     this.randomRow = generateRandomRow();
     checkDone();
     update();
   }
   
   public void playVisuals() {
     PacketContainer packet = Packets.PARTICLES(this.pInfo.getPlayer().getLocation().add(0.0D, 1.3D, 0.0D), Particle.BLOCK_CRACK, Bukkit.createBlockData(Material.PODZOL), Integer.valueOf(60), 0.5F, 0.5F, 0.5F);
     Packets.sendPacket(this.pInfo.getPlayer(), packet);
     for (PlayerInfo pInfo_ : this.arena.getPlayersInfo()) {
       if (this.pInfo != pInfo_) {
         if (this.arena.getEnableReducedVision().booleanValue()) {
           if (pInfo_.isGhost().booleanValue() || !pInfo_.getPlayersHidden().contains(this.pInfo.getPlayer()))
             Packets.sendPacket(pInfo_.getPlayer(), packet); 
           continue;
         } 
         Packets.sendPacket(pInfo_.getPlayer(), packet);
       } 
     } 
   }
 
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       if (this.arena.getEnableVisualTasks().booleanValue() && this.taskPlayer.getActiveTask().getEnableVisuals().booleanValue() && !this.pInfo.isGhost().booleanValue()) {
         playVisuals();
       }
       this.taskPlayer.taskDone();
       final TaskEmptyGarbageInv taskInv = this;
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
     this.inv.setItem(8, Main.getItemsManager().getItem("emptyGarbage_info").getItem().getItem());
     
     final TaskEmptyGarbageInv inv = this;
     ItemInfoContainer garbageItem = Main.getItemsManager().getItem("emptyGarbage_garbage");
     ItemStack garbageItemS = garbageItem.getItem().getItem();
     ItemStack garbageItemS2 = garbageItem.getItem2().getItem();
     
     for (int i = 0; i < slots.size(); i++) {
       for (Integer slot : slots.get(i)) {
         if (i <= this.topRow) {
           this.inv.setItem(slot, garbageItemS); continue;
         } 
         this.inv.setItem(slot, garbageItemS2);
       } 
     } 
     
     if (this.topRow < 3) {
       int randomI = 0;
       for (Integer slot : slots.get(this.topRow + 1)) {
         if (((Integer)this.randomRow.get(randomI)) == 0) {
           this.inv.setItem(slot, garbageItemS);
         } else {
           this.inv.setItem(slot, garbageItemS2);
         } 
         randomI++;
       } 
     } 
     
     ItemInfoContainer leverItem = Main.getItemsManager().getItem("emptyGarbage_lever");
     Icon icon = new Icon(this.isRunning.booleanValue() ? leverItem.getItem2().getItem() : leverItem.getItem().getItem());
     icon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             inv.handleClick(player);
           }
         });
     setIcon(25, icon);
     
     ItemInfoContainer leverTopItem = Main.getItemsManager().getItem("emptyGarbage_leverTop");
     icon = new Icon(this.isRunning.booleanValue() ? leverTopItem.getItem2().getItem() : leverTopItem.getItem().getItem());
     setIcon(16, icon);
     
     ItemInfoContainer leverBottomItem = Main.getItemsManager().getItem("emptyGarbage_leverBottom");
     icon = new Icon(this.isRunning.booleanValue() ? leverBottomItem.getItem().getItem() : leverBottomItem.getItem2().getItem());
     setIcon(34, icon);
   }
 
   
   public void invClosed() {
     if (this.runnable != null) {
       this.runnable.cancel();
       this.runnable = null;
     } 
   }
 
   
   public Boolean getIsRunning() { return this.isRunning; }
 
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


