 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.info.ItemInfo;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.VitalsPlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.managers.VitalsManager;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 
 public class VitalsInv
   extends CustomHolder
 {
   private static final Integer pageSize = Integer.valueOf(7);
   private static final ArrayList<ArrayList<Integer>> playersSlots = new ArrayList<>();
   static  {
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(10), Integer.valueOf(19), Integer.valueOf(28), Integer.valueOf(37) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(2), Integer.valueOf(11), Integer.valueOf(20), Integer.valueOf(29), Integer.valueOf(38) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(3), Integer.valueOf(12), Integer.valueOf(21), Integer.valueOf(30), Integer.valueOf(39) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(4), Integer.valueOf(13), Integer.valueOf(22), Integer.valueOf(31), Integer.valueOf(40) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(5), Integer.valueOf(14), Integer.valueOf(23), Integer.valueOf(32), Integer.valueOf(41) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(6), Integer.valueOf(15), Integer.valueOf(24), Integer.valueOf(33), Integer.valueOf(42) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(7), Integer.valueOf(16), Integer.valueOf(25), Integer.valueOf(34), Integer.valueOf(43) })));
     playersSlots.add(new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(8), Integer.valueOf(16), Integer.valueOf(26), Integer.valueOf(35), Integer.valueOf(44) })));
   }
   
   private VitalsManager vitalsManager;
   private Integer currentPage = Integer.valueOf(1);
   
   public VitalsInv(VitalsManager vitalsManager) {
     super(Integer.valueOf(54), Main.getMessagesManager().getGameMsg("vitalsInvTitle", null, null));
     this.vitalsManager = vitalsManager;
     Utils.fillInv(this.inv);
     update();
   }
   
   public void update() {
     final VitalsInv inv_ = this;
     clearInv();
     Utils.fillInv(this.inv);
     
     if (this.vitalsManager.getPlayers().size() == 0) {
       return;
     }
     
     Integer totalItems = Integer.valueOf(this.vitalsManager.getPlayers().size());
     Integer totalPages = Integer.valueOf((int)Math.ceil((double) totalItems / (double) pageSize));
     
     if (totalPages > 1) {
       if (this.currentPage > 1) {
         Icon icon = new Icon(Main.getItemsManager().getItem("vitals_prevPage").getItem().getItem());
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv_.setCurrentPage(Integer.valueOf(inv_.getCurrentPage() - 1));
                 inv_.update();
               }
             });
         setIcon(45, icon);
       } 
       if (this.currentPage < totalPages) {
         Icon icon = new Icon(Main.getItemsManager().getItem("vitals_nextPage").getItem().getItem());
         icon.addClickAction(new ClickAction()
             {
               public void execute(Player player) {
                 inv_.setCurrentPage(Integer.valueOf(inv_.getCurrentPage() + 1));
                 inv_.update();
               }
             });
         setIcon(53, icon);
       } 
     } 
     
     Integer startIndex = Integer.valueOf((this.currentPage - 1) * pageSize);
     Integer endIndex = Integer.valueOf(Math.min(startIndex + pageSize - 1, totalItems - 1));
     ArrayList<VitalsPlayerInfo> players_ = this.vitalsManager.getPlayers();
     
     ItemInfoContainer headItemInfo = Main.getItemsManager().getItem("vitals_playerHead");
     ItemInfoContainer playerItemInfo = Main.getItemsManager().getItem("vitals_player");
     
     Integer slot_ = Integer.valueOf(46);
     Integer i1 = Integer.valueOf(0);
     for (int i = startIndex; i <= endIndex; i++) {
       VitalsPlayerInfo vpi = players_.get(i);
       ItemStack item = vpi.getHeadItem().clone();
       ItemInfo headItem = headItemInfo.getItem();
       ItemStack playerItem = playerItemInfo.getItem().getItem();
       if (vpi.getIsDC().booleanValue()) {
         headItem = headItemInfo.getItem3();
         playerItem = playerItemInfo.getItem3().getItem();
       } else if (vpi.getIsDead().booleanValue()) {
         headItem = headItemInfo.getItem2();
         playerItem = playerItemInfo.getItem2().getItem();
       } 
       Utils.setItemName(item, headItem.getTitle(vpi.getPlayer().getName(), vpi.getColor().getName(), "" + vpi.getColor().getChatColor(), null, null), 
           headItem.getLore(vpi.getPlayer().getName(), vpi.getColor().getName(),"" + vpi.getColor().getChatColor(), null, null));
       this.inv.setItem(slot_, item);
       
       for (Integer slot : playersSlots.get(i1)) {
         this.inv.setItem(slot, playerItem);
       }
       
       i1 = Integer.valueOf(i1 + 1);
       slot_ = Integer.valueOf(slot_ + 1);
     } 
   }
 
 
   
   public Integer getCurrentPage() { return this.currentPage; }
 
 
   
   public void setCurrentPage(Integer currentPage) { this.currentPage = currentPage; }
 }


