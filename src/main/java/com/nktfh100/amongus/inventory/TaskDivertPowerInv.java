 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.LocationName;
 import com.nktfh100.AmongUs.info.Task;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import org.bukkit.entity.Player;
 import org.bukkit.scheduler.BukkitRunnable;
 
 public class TaskDivertPowerInv
   extends TaskInvHolder
 {
   private static final ArrayList<Integer> locationsSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(3), Integer.valueOf(5), Integer.valueOf(7) }));
   private static final ArrayList<Integer> topFillBetweenSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(12), Integer.valueOf(14), Integer.valueOf(16) }));
   private static final ArrayList<Integer> leverSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(19), Integer.valueOf(21), Integer.valueOf(23), Integer.valueOf(25) }));
   private static final ArrayList<Integer> bottomFillBetweenSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(28), Integer.valueOf(30), Integer.valueOf(32), Integer.valueOf(34) }));
   
   private ArrayList<String> locations = new ArrayList<>();
   private Integer activeLever = Integer.valueOf(0);
   private String activeLocation = "";
   private Boolean isLeverActive = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
 
   
   public TaskDivertPowerInv(Arena arena, TaskPlayer taskPlayer, ArrayList<String> locations_, String activeLocation_, Integer activeLever_) {
     super(36, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     if (this.activeLocation == null) {
       this.activeLocation = generateActiveLocation(taskPlayer);
     } else {
       this.activeLocation = activeLocation_;
     } 
     
     if (locations_ == null || locations_.size() == 0) {
       this.locations = generateLocations(arena, this.activeLocation);
     } else {
       this.locations = locations_;
     } 
     
     if (activeLever_ != null) {
       this.activeLever = generateLever(this.locations, this.activeLocation);
     } else {
       this.activeLever = activeLever_;
     } 
     
     update();
   }
   
   public static String generateActiveLocation(TaskPlayer taskPlayer) {
     if (taskPlayer.getTasks().size() > 1 && 
       taskPlayer.getState() < taskPlayer.getTasks().size() - 1) {
       return ((Task)taskPlayer.getTasks().get(taskPlayer.getState() + 1)).getLocationName().getName();
     }
     
     return taskPlayer.getActiveTask().getLocationName().getName();
   }
   
   public static ArrayList<String> generateLocations(Arena arena, String activeLocation) {
     ArrayList<String> out = new ArrayList<>();
     out.add(activeLocation);
     ArrayList<String> arenaLocs = new ArrayList<>();
     for (LocationName locName : arena.getLocations().values()) {
       arenaLocs.add(locName.getName());
     }
     Collections.shuffle(arenaLocs);
     for (String loc_ : arenaLocs) {
       if (out.size() < 4) {
         if (!out.contains(loc_)) {
           out.add(loc_);
         }
         
         continue;
       } 
       break;
     } 
     while (out.size() < 4) {
       out.add(" ");
     }
     Collections.shuffle(out);
     return out;
   }
   
   public static Integer generateLever(ArrayList<String> locations, String activeLocation) {
     for (int i = 0; i < 4; i++) {
       if (activeLocation.equalsIgnoreCase(locations.get(i))) {
         return Integer.valueOf(i);
       }
     } 
     return Integer.valueOf(0);
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskDivertPowerInv taskInv = this;
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
     final TaskDivertPowerInv divertPowerInv = this;
     
     ItemInfo locationTopItem = Main.getItemsManager().getItem("divertPower_topLocation").getItem();
     ItemInfo leverItem = Main.getItemsManager().getItem("divertPower_lever").getItem();
     ItemInfoContainer activeLeverItem = Main.getItemsManager().getItem("divertPower_activeLever");
     ItemInfoContainer fillBetweenItem = Main.getItemsManager().getItem("divertPower_fillBetween");
     
     for (int i = 0; i < 4; i++) {
       Icon icon = new Icon(locationTopItem.getItem(this.locations.get(i), null));
       setIcon(((Integer)locationsSlots.get(i)), icon);
     } 
     
     for (int i = 0; i < 4; i++) {
       Icon icon = new Icon((this.activeLever == i && this.isDone.booleanValue()) ? activeLeverItem.getItem2().getItem() : ((this.isLeverActive.booleanValue() && this.activeLever == i) ? fillBetweenItem.getItem2().getItem() : fillBetweenItem.getItem().getItem()));
       if (this.isLeverActive.booleanValue() && i == this.activeLever) {
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 Main.getSoundsManager().playSound("taskDivertPower_moveLever", player, player.getLocation());
                 divertPowerInv.setIsLeverActive(Boolean.valueOf(false));
                 divertPowerInv.setIsDone(Boolean.valueOf(true));
                 divertPowerInv.update();
                 divertPowerInv.checkDone();
               }
             });
       }
       setIcon(((Integer)topFillBetweenSlots.get(i)), icon);
     } 
     
     for (int i = 0; i < 4; i++) {
       Icon icon;
       if (this.activeLever == i) {
         icon = new Icon(this.isDone.booleanValue() ? fillBetweenItem.getItem().getItem() : (this.isLeverActive.booleanValue() ? activeLeverItem.getItem2().getItem() : activeLeverItem.getItem().getItem()));
         if (!this.isLeverActive.booleanValue()) {
           icon.addClickAction(new ClickAction()
               {
                 public void execute(Player player) {
                   Main.getSoundsManager().playSound("taskDivertPower_clickLever", player, player.getLocation());
                   divertPowerInv.setIsLeverActive(Boolean.valueOf(true));
                   divertPowerInv.update();
                 }
               });
         }
       } else {
         icon = new Icon(leverItem.getItem());
       } 
       setIcon(((Integer)leverSlots.get(i)), icon);
     } 
     
     for (int i = 0; i < 4; i++) {
       Icon icon = new Icon(fillBetweenItem.getItem().getItem());
       setIcon(((Integer)bottomFillBetweenSlots.get(i)), icon);
     } 
     
     this.inv.setItem(8, Main.getItemsManager().getItem("divertPower_info").getItem().getItem());
   }
 
 
   
   public void invClosed() {}
 
   
   public Boolean getIsLeverActive() { return this.isLeverActive; }
 
 
   
   public void setIsLeverActive(Boolean is) { this.isLeverActive = is; }
 
 
   
   public Boolean getIsDone() { return this.isDone; }
 
 
   
   public void setIsDone(Boolean isDone) { this.isDone = isDone; }
 }


