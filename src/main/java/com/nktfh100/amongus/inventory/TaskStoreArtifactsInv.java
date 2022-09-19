 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.TaskPlayer;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.HashMap;
 import org.bukkit.Material;
 import org.bukkit.enchantments.Enchantment;
 import org.bukkit.entity.Player;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class TaskStoreArtifactsInv
   extends TaskInvHolder
 {
   private String activeArtifact = "";
   private HashMap<String, Boolean> artifactsState = new HashMap<>();
   private Boolean isDone = Boolean.valueOf(false);
   
   public TaskStoreArtifactsInv(Arena arena, TaskPlayer taskPlayer) {
     super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
     Utils.fillInv(this.inv);
     this.artifactsState.put("diamond", Boolean.valueOf(false));
     this.artifactsState.put("purple", Boolean.valueOf(false));
     this.artifactsState.put("leaf", Boolean.valueOf(false));
     this.artifactsState.put("skull", Boolean.valueOf(false));
     update();
   }
   
   public void handleArtifactClick(String type) {
     if (this.isDone.booleanValue()) {
       return;
     }
     this.activeArtifact = type;
     
     update();
   }
   
   public void handleArtifactTargetClick(String clickedType) {
     if (this.isDone.booleanValue()) {
       return;
     }
     if (this.activeArtifact.equals(clickedType)) {
       
       Main.getSoundsManager().playSound("taskStoreArtifacts_artifactDone", getPlayerInfo().getPlayer(), getPlayerInfo().getPlayer().getLocation());
       this.activeArtifact = "";
       this.artifactsState.put(clickedType, Boolean.valueOf(true));
       this.isDone = Boolean.valueOf(true);
       for (Boolean bol : this.artifactsState.values()) {
         if (!bol.booleanValue()) {
           this.isDone = Boolean.valueOf(false);
           break;
         } 
       } 
       update();
       checkDone();
     } 
   }
 
   
   public Boolean checkDone() {
     if (this.isDone.booleanValue()) {
       this.taskPlayer.taskDone();
       final TaskStoreArtifactsInv inv = this;
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
     final TaskStoreArtifactsInv inv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("storeArtifacts_info").getItem().getItem());
     
     ItemInfoContainer diamondItemInfo = Main.getItemsManager().getItem("storeArtifacts_diamond");
     ItemInfoContainer purpleItemInfo = Main.getItemsManager().getItem("storeArtifacts_purple");
     ItemInfoContainer skullItemInfo = Main.getItemsManager().getItem("storeArtifacts_skull");
     ItemInfoContainer leafItemInfo = Main.getItemsManager().getItem("storeArtifacts_leaf");
     
     if (!((Boolean)this.artifactsState.get("diamond")).booleanValue()) {
       Icon icon = new Icon(!this.activeArtifact.equals("diamond") ? diamondItemInfo.getItem().getItem() : Utils.enchantedItem(diamondItemInfo.getItem().getItem(), Enchantment.DURABILITY, 1));
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactClick("diamond");
             }
           });
       setIcon(1, icon);
       setIcon(2, icon);
       setIcon(10, icon);
       setIcon(11, icon);
       
       icon = new Icon(diamondItemInfo.getItem2().getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactTargetClick("diamond");
             }
           });
       setIcon(41, icon);
       setIcon(42, icon);
       setIcon(50, icon);
       setIcon(51, icon);
     } else {
       Icon icon = new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
       setIcon(1, icon);
       setIcon(2, icon);
       setIcon(10, icon);
       setIcon(11, icon);
       
       icon = new Icon(diamondItemInfo.getItem().getItem());
       setIcon(41, icon);
       setIcon(42, icon);
       setIcon(50, icon);
       setIcon(51, icon);
     } 
     
     if (!((Boolean)this.artifactsState.get("purple")).booleanValue()) {
       Icon icon = new Icon(!this.activeArtifact.equals("purple") ? purpleItemInfo.getItem().getItem() : Utils.enchantedItem(purpleItemInfo.getItem().getItem(), Enchantment.DURABILITY, 1));
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactClick("purple");
             }
           });
       setIcon(0, icon);
       setIcon(9, icon);
       setIcon(18, icon);
       
       icon = new Icon(purpleItemInfo.getItem2().getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactTargetClick("purple");
             }
           });
       setIcon(7, icon);
       setIcon(16, icon);
       setIcon(25, icon);
     } else {
       Icon icon = new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
       setIcon(0, icon);
       setIcon(9, icon);
       setIcon(18, icon);
       
       icon = new Icon(purpleItemInfo.getItem().getItem());
       setIcon(7, icon);
       setIcon(16, icon);
       setIcon(25, icon);
     } 
     
     if (!((Boolean)this.artifactsState.get("skull")).booleanValue()) {
       Icon icon = new Icon(!this.activeArtifact.equals("skull") ? skullItemInfo.getItem().getItem() : Utils.enchantedItem(skullItemInfo.getItem().getItem(), Enchantment.DURABILITY, 1));
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactClick("skull");
             }
           });
       setIcon(38, icon);
       setIcon(39, icon);
       setIcon(47, icon);
       setIcon(48, icon);
       
       icon = new Icon(skullItemInfo.getItem2().getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactTargetClick("skull");
             }
           });
       setIcon(34, icon);
       setIcon(35, icon);
       setIcon(43, icon);
       setIcon(44, icon);
     } else {
       
       Icon icon = new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
       setIcon(38, icon);
       setIcon(39, icon);
       setIcon(47, icon);
       setIcon(48, icon);
       
       icon = new Icon(skullItemInfo.getItem().getItem());
       setIcon(34, icon);
       setIcon(35, icon);
       setIcon(43, icon);
       setIcon(44, icon);
     } 
     
     if (!((Boolean)this.artifactsState.get("leaf")).booleanValue()) {
       Icon icon = new Icon(!this.activeArtifact.equals("leaf") ? leafItemInfo.getItem().getItem() : Utils.enchantedItem(leafItemInfo.getItem().getItem(), Enchantment.DURABILITY, 1));
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactClick("leaf");
             }
           });
       setIcon(27, icon);
       setIcon(28, icon);
       setIcon(36, icon);
       setIcon(37, icon);
       setIcon(45, icon);
       setIcon(46, icon);
       
       icon = new Icon(leafItemInfo.getItem2().getItem());
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               inv.handleArtifactTargetClick("leaf");
             }
           });
       setIcon(14, icon);
       setIcon(15, icon);
       setIcon(23, icon);
       setIcon(24, icon);
       setIcon(32, icon);
       setIcon(33, icon);
     } else {
       
       Icon icon = new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
       setIcon(27, icon);
       setIcon(28, icon);
       setIcon(36, icon);
       setIcon(37, icon);
       setIcon(45, icon);
       setIcon(46, icon);
       
       icon = new Icon(leafItemInfo.getItem().getItem());
       setIcon(14, icon);
       setIcon(15, icon);
       setIcon(23, icon);
       setIcon(24, icon);
       setIcon(32, icon);
       setIcon(33, icon);
     } 
   }
   
   public void invClosed() {}
 }


