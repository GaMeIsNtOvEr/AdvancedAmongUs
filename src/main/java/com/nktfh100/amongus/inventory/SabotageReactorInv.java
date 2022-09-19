 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.SabotageArena;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 
 public class SabotageReactorInv
   extends SabotageInvHolder
 {
   private Integer taskNum;
   private Boolean isActive = Boolean.valueOf(false);
   
   private Arena arena;
   private Player player;
   private String tempActiveTitle;
   private Boolean removePlayerOnClose = Boolean.valueOf(true);
 
   
   public SabotageReactorInv(SabotageArena saboArena, Integer taskNum, Player player) {
     super(54, Main.getMessagesManager().getGameMsg("sabotageReactorsInvTitle", saboArena.getArena(), Main.getMessagesManager().getTaskName(SabotageType.REACTOR_MELTDOWN.toString()), Main.getMessagesManager().getSabotageTitle(SabotageType.REACTOR_MELTDOWN)), saboArena.getArena(), saboArena);
     Utils.fillInv(this.inv);
     this.taskNum = taskNum;
     this.arena = saboArena.getArena();
     this.player = player;
     this.tempActiveTitle = "sabotageReactorsInvTitle";
     update();
   }
   
   public void handleClick(Player p) {
     if (!this.isActive.booleanValue()) {
       Main.getSoundsManager().playSound("sabotageReactorClick", p, p.getLocation());
       this.isActive = Boolean.valueOf(true);
       this.sabotageArena.addPlayerActive(p, this.taskNum);
       update();
     } 
   }
   
   private static final ArrayList<Integer> slots_ = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41) }));
 
 
   
   public void update() {
     this.inv.setItem(8, Main.getItemsManager().getItem("reactorSabotage_info").getItem().getItem());
     
     String activeTitle = this.tempActiveTitle;
     String newTitleKey = "sabotageReactorsInvTitle";
     
     ItemInfoContainer infoItem = Main.getItemsManager().getItem("reactorSabotage_topItem");
     Material infoMat = infoItem.getItem().getMat();
     String infoName = infoItem.getItem().getTitle();
     ArrayList<String> infoLore = infoItem.getItem().getLore();
     
     ItemInfoContainer handItem = Main.getItemsManager().getItem("reactorSabotage_hand");
     Material handMat = handItem.getItem().getMat();
     String handName = handItem.getItem().getTitle();
     ArrayList<String> handLore = handItem.getItem().getLore();
     if (this.isActive.booleanValue()) {
       if (this.sabotageArena.getTaskActive(Integer.valueOf((this.taskNum == 0) ? 1 : 0)).booleanValue()) {
         infoMat = infoItem.getItem3().getMat();
         infoName = infoItem.getItem3().getTitle();
         infoLore = infoItem.getItem3().getLore();
         
         handMat = handItem.getItem3().getMat();
         handName = handItem.getItem3().getTitle();
         handLore = handItem.getItem3().getLore();
         newTitleKey = "sabotageReactorsInvTitle2";
       } else {
         infoMat = infoItem.getItem2().getMat();
         infoName = infoItem.getItem2().getTitle();
         infoLore = infoItem.getItem2().getLore();
         
         handMat = handItem.getItem2().getMat();
         handName = handItem.getItem2().getTitle();
         handLore = handItem.getItem2().getLore();
         newTitleKey = "sabotageReactorsInvTitle1";
       } 
     }
     if (!activeTitle.equals(newTitleKey)) {
       this.removePlayerOnClose = Boolean.valueOf(false);
       changeTitle(Main.getMessagesManager().getGameMsg(newTitleKey, this.arena, Main.getMessagesManager().getTaskName(SabotageType.REACTOR_MELTDOWN.toString()), 
             Main.getMessagesManager().getSabotageTitle(SabotageType.REACTOR_MELTDOWN)));
       Utils.fillInv(this.inv);
       this.player.openInventory(this.inv);
       this.tempActiveTitle = newTitleKey;
       this.removePlayerOnClose = Boolean.valueOf(true);
     } 
     
     this.inv.setItem(4, Utils.createItem(infoMat, infoName, 1, infoLore));
     
     ItemStack item = Utils.createItem(handMat, handName, 1, handLore);
     
     final SabotageReactorInv reactorInv = this;
     ClickAction ca = new ClickAction()
       {
         public void execute(Player player) {
           reactorInv.handleClick(player);
         }
       };
     
     for (Integer slot : slots_) {
       Icon icon = new Icon(item);
       icon.addClickAction(ca);
       setIcon(slot, icon);
     } 
   }
 
   
   public void invClosed(Player player) {
     if (this.isActive.booleanValue() && this.removePlayerOnClose.booleanValue()) {
       this.sabotageArena.removePlayerActive(player, this.taskNum);
     }
   }
 
   
   public Boolean getIsActive() { return this.isActive; }
 
 
   
   public void setIsActive(Boolean isActive) { this.isActive = isActive; }
 
 
   
   public Integer getTaskNum() { return this.taskNum; }
 }


