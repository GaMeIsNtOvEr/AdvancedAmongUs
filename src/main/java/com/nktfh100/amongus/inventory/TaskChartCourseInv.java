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
 
 
 public class TaskChartCourseInv
   extends TaskInvHolder
 {
   private static final ArrayList<ArrayList<Integer>> pointsSlots = new ArrayList<>();
   
   static  {
     pointsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(19), Integer.valueOf(28) })));
     pointsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(12), Integer.valueOf(21), Integer.valueOf(30) })));
     pointsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(14), Integer.valueOf(23), Integer.valueOf(32) })));
     pointsSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(16), Integer.valueOf(25), Integer.valueOf(34) })));
   }
   
   private ArrayList<Integer> activeSlots = new ArrayList<>();
   private Integer activePoint = Integer.valueOf(0);
   private Boolean isShipClicked = Boolean.valueOf(false);
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskChartCourseInv(Arena arena, TaskPlayer taskPlayer) {
     super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     
     for (int i = 0; i < 4; i++) {
       this.activeSlots.add(((ArrayList<Integer>)pointsSlots.get(i)).get(Utils.getRandomNumberInRange(0, 2)));
     }
     
     update();
   }
   
   public void shipClick() {
     Main.getSoundsManager().playSound("taskChartCourseShipClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     if (!this.isShipClicked.booleanValue() && !this.isDone.booleanValue()) {
       this.isShipClicked = Boolean.valueOf(true);
       update();
     } 
   }
   
   public void pointClick(Integer pointId) {
     Main.getSoundsManager().playSound("taskChartCoursePointClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
     if (this.isShipClicked.booleanValue() && !this.isDone.booleanValue()) {
       this.isShipClicked = Boolean.valueOf(false);
       if (this.activePoint == pointId - 1) {
         this.activePoint = pointId;
         if (pointId == 3) {
           this.isDone = Boolean.valueOf(true);
           checkDone();
         } 
       } 
       
       update();
     } 
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskChartCourseInv inv = this;
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
     this.inv.setItem(8, Main.getItemsManager().getItem("chartCourse_info").getItem().getItem());
     
     ItemInfoContainer shipItem = Main.getItemsManager().getItem("chartCourse_ship");
     ItemInfoContainer pointItem = Main.getItemsManager().getItem("chartCourse_point");
     ItemStack pointItemS = pointItem.getItem().getItem();
     
     final TaskChartCourseInv inv = this;
     
     for (int i = 0; i < 4; i++) {
       Icon icon = new Icon(pointItemS);
       final Integer id_ = Integer.valueOf(i);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.pointClick(id_);
             }
           });
       setIcon(((Integer)this.activeSlots.get(i)), icon);
     } 
     
     ItemInfoContainer midItem = Main.getItemsManager().getItem("chartCourse_middle");
     ItemStack midItemS = midItem.getItem().getItem();
     
     this.inv.setItem(20, midItemS);
     this.inv.setItem(22, midItemS);
     this.inv.setItem(24, midItemS);
     
     ItemStack shipItemS = shipItem.getItem().getItem();
     if (this.isShipClicked.booleanValue()) {
       Utils.enchantedItem(shipItemS, Enchantment.DURABILITY, 1);
     }
     Icon icon = new Icon(shipItemS);
     icon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             inv.shipClick();
           }
         });
     setIcon(((Integer)this.activeSlots.get(this.activePoint)), icon);
   }
   
   public void invClosed() {}
 }


