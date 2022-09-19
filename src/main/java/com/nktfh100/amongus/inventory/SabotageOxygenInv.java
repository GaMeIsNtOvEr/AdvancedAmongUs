 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.info.SabotageArena;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 
 public class SabotageOxygenInv
   extends SabotageInvHolder
 {
   private Integer taskNum;
   private ArrayList<Integer> code = new ArrayList<>();
   private ArrayList<Integer> activeCode = new ArrayList<>();
   private Boolean canClick = Boolean.valueOf(true);
 
   
   public SabotageOxygenInv(SabotageArena saboArena, Integer taskNum, ArrayList<Integer> code) {
     super(54, Main.getMessagesManager().getGameMsg("sabotageOxygenInvTitle", saboArena.getArena(), Main.getMessagesManager().getTaskName(SabotageType.OXYGEN.toString()), Main.getMessagesManager().getSabotageTitle(SabotageType.OXYGEN), codeToStr(code), ""), saboArena.getArena(), saboArena);
     Utils.fillInv(this.inv);
     this.taskNum = taskNum;
     this.code = code;
     update();
   }
   
   private static String codeToStr(ArrayList<Integer> code_) {
     String c = "";
     for (Integer i : code_) {
       c = String.valueOf(c) + i.toString();
     }
     return c;
   }
   
   public void handleNumClick(Player p, Integer num) {
     if (!this.canClick.booleanValue()) {
       return;
     }
     if (this.activeCode.size() < 5) {
       this.activeCode.add(num);
       Main.getSoundsManager().playSound("sabotageOxygenNumberClick", p, p.getLocation());
     } 
     changeTitle(Main.getMessagesManager().getGameMsg("sabotageOxygenInvTitle", this.arena, Main.getMessagesManager().getTaskName(SabotageType.OXYGEN.toString()), 
           Main.getMessagesManager().getSabotageTitle(SabotageType.OXYGEN), codeToStr(this.code), codeToStr(this.activeCode)));
     Utils.fillInv(this.inv);
     update();
     p.openInventory(getInventory());
   }
   
   public void handleVClick(Player p) {
     if (!this.canClick.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("sabotageOxygenAcceptClick", p, p.getLocation());
     if (this.activeCode.size() == 5) {
       Boolean isOk = Boolean.valueOf(true);
       for (int i = 0; i < this.code.size(); i++) {
         if (this.activeCode.get(i) != this.code.get(i)) {
           isOk = Boolean.valueOf(false);
           break;
         } 
       } 
       if (isOk.booleanValue()) {
         this.canClick = Boolean.valueOf(false);
         getSabotageArena().taskDone(getTaskNum(), p);
         p.closeInventory();
 
 
 
 
         
         return;
       } 
     } 
 
 
 
     
     this.activeCode.clear();
     changeTitle(Main.getMessagesManager().getGameMsg("sabotageOxygenInvTitle", this.arena, Main.getMessagesManager().getTaskName(SabotageType.OXYGEN.toString()), 
           Main.getMessagesManager().getSabotageTitle(SabotageType.OXYGEN), codeToStr(this.code), codeToStr(this.activeCode)));
     Utils.fillInv(this.inv);
     update();
     p.openInventory(getInventory());
   }
   
   public void handleXClick(Player p) {
     if (!this.canClick.booleanValue()) {
       return;
     }
     Main.getSoundsManager().playSound("sabotageOxygenCancelClick", p, p.getLocation());
     this.activeCode.clear();
     changeTitle(Main.getMessagesManager().getGameMsg("sabotageOxygenInvTitle", this.arena, Main.getMessagesManager().getTaskName(SabotageType.OXYGEN.toString()), 
           Main.getMessagesManager().getSabotageTitle(SabotageType.OXYGEN), codeToStr(this.code), codeToStr(this.activeCode)));
     Utils.fillInv(this.inv);
     update();
     p.openInventory(getInventory());
   }
   
   private static ArrayList<Integer> slots_ = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(49) }));
 
   
   public void update() {
     final SabotageOxygenInv oxygenInv = this;
     
     this.inv.setItem(8, Main.getItemsManager().getItem("oxygenSabotage_info").getItem().getItem());
     
     ItemInfo codeItem = Main.getItemsManager().getItem("oxygenSabotage_code").getItem();
     for (int i = 0; i < 5; i++) {
       if (this.activeCode.size() > i) {
         this.inv.setItem(2 + i, Main.getItemsManager().getItem("oxygenSabotage_button" + this.activeCode.get(i)).getItem().getItem());
       } else {
         this.inv.setItem(2 + i, codeItem.getItem("_", null));
       } 
     } 
     
     int i = 1;
     for (Integer slot : slots_) {
       ItemInfo buttonItem = Main.getItemsManager().getItem("oxygenSabotage_button" + i).getItem();
       ItemStack item = buttonItem.getItem((new StringBuilder(String.valueOf(i))).toString(), null);
       item.setAmount((i == 0) ? 1 : i);
       
       Icon icon = new Icon(item);
       final Integer num = Integer.valueOf(i);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               oxygenInv.handleNumClick(player, num);
             }
           });
       
       setIcon(slot, icon);
       i++;
       if (i == 10) {
         i = 0;
       }
     } 
     
     ItemStack cancelItemS = Main.getItemsManager().getItem("oxygenSabotage_cancel").getItem().getItem();
     Icon icon = new Icon(cancelItemS);
     icon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             oxygenInv.handleXClick(player);
           }
         });
     setIcon(48, icon);
     
     ItemStack acceptItemS = Main.getItemsManager().getItem("oxygenSabotage_accept").getItem().getItem();
     icon = new Icon(acceptItemS);
     icon.addClickAction(new ClickAction()
         {
           public void execute(Player player) {
             oxygenInv.handleVClick(player);
           }
         });
     setIcon(50, icon);
   }
 
 
   
   public void invClosed(Player player) {}
 
   
   public Integer getTaskNum() { return this.taskNum; }
 
 
   
   public ArrayList<Integer> getCode() { return this.code; }
 
 
   
   public ArrayList<Integer> getActiveCode() { return this.activeCode; }
 
 
   
   public void setActiveCode(ArrayList<Integer> activeCode) { this.activeCode = activeCode; }
 }


