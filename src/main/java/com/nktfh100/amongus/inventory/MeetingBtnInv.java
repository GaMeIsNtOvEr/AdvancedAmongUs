 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 
 
 public class MeetingBtnInv
   extends CustomHolder
 {
   private Arena arena;
   private PlayerInfo pInfo;
   
   public MeetingBtnInv(Arena arena, PlayerInfo pInfo) {
     super(Integer.valueOf(45), Main.getMessagesManager().getGameMsg("meetingButtonInvTitle", arena, null));
     this.arena = arena;
     this.pInfo = pInfo;
     Utils.fillInv(this.inv);
     update();
   }
   
   private static ArrayList<Integer> slotsBW = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(39), Integer.valueOf(38), Integer.valueOf(29), Integer.valueOf(20), Integer.valueOf(11), Integer.valueOf(2), Integer.valueOf(3), Integer.valueOf(4), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(15), Integer.valueOf(24), Integer.valueOf(33), Integer.valueOf(42), Integer.valueOf(41) }));
   private static ArrayList<Integer> slotsBtn = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32) }));
   
   public void update() {
     Integer color = Integer.valueOf(0);
     for (Integer slot : slotsBW) {
       Material mat = (color == 0) ? Material.YELLOW_CONCRETE : Material.BLACK_CONCRETE;
       this.inv.setItem(slot, Utils.createItem(mat, " "));
       color = Integer.valueOf((color == 0) ? 1 : 0);
     } 
     ItemInfoContainer infoItem = Main.getItemsManager().getItem("meetingButton_info");
     String value = this.pInfo.getPlayer().getName();
     Integer integer1 = this.pInfo.getMeetingsLeft();
     Integer integer2 = this.arena.getMeetingManager().getMeetingCooldownTimer();
     Integer amount = this.arena.getMeetingManager().getMeetingCooldownTimer();
     if (amount == 0) {
       amount = Integer.valueOf(1);
     }
     if (this.arena.getMeetingManager().getMeetingCooldownTimer() > 0) {
       this.inv.setItem(40, Utils.createItem(infoItem.getItem3().getMat(), infoItem.getItem3().getTitle(value, "" + integer1, "" + integer2, null, null), amount, infoItem.getItem3().getLore(value, "" + integer1, "" + integer2, null, null)));
     }
     else if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue()) {
       this.inv.setItem(40, 
           Utils.createItem(infoItem.getItem2().getMat(), infoItem.getItem2().getTitle(value, "" + integer1, "" + integer2, null, null), amount, infoItem.getItem2().getLore(value, "" + integer1, "" + integer2, null, null)));
     } else {
       this.inv.setItem(40, Utils.createItem(infoItem.getItem().getMat(), infoItem.getItem().getTitle(value, "" + integer1, "" + integer2, null, null), amount, infoItem.getItem().getLore(value, "" + integer1, "" + integer2, null, null)));
     } 
 
     
     ItemInfoContainer buttonItem = Main.getItemsManager().getItem("meetingButton_button");
     Icon icon = new Icon(Utils.createItem(this.arena.canPlayerUseButton(this.pInfo).booleanValue() ? buttonItem.getItem2().getMat() : buttonItem.getItem().getMat(), 
           this.arena.canPlayerUseButton(this.pInfo).booleanValue() ? buttonItem.getItem2().getTitle() : buttonItem.getItem().getTitle(), 1, 
           this.arena.canPlayerUseButton(this.pInfo).booleanValue() ? buttonItem.getItem2().getLore() : buttonItem.getItem().getLore()));
     if (this.arena.canPlayerUseButton(this.pInfo).booleanValue()) {
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               MeetingBtnInv.this.arena.getMeetingManager().callMeeting(player, Boolean.valueOf(false), null);
             }
           });
     }
     for (Integer slot : slotsBtn) {
       setIcon(slot, icon);
     }
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public PlayerInfo getpInfo() { return this.pInfo; }
 }


