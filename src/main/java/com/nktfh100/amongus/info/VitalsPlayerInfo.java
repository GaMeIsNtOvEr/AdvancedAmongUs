 package com.nktfh100.amongus.info;
 
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 public class VitalsPlayerInfo
   implements Comparable<VitalsPlayerInfo>
 {
   private Player player;
   private Integer joinedId;
   private ColorInfo color;
   private ItemStack headItem;
   private Boolean isDead;
   private Boolean isDC;
   
   public VitalsPlayerInfo(PlayerInfo pInfo) {
     this.player = pInfo.getPlayer();
     this.joinedId = pInfo.getJoinedId();
     if (pInfo.getHead() != null) {
       this.headItem = pInfo.getHead().clone();
     } else {
       this.headItem = Utils.getHead(pInfo.getPlayer().getName());
     } 
     this.isDead = pInfo.isGhost();
     this.color = pInfo.getColor();
     this.isDC = Boolean.valueOf(false);
   }
 
   
   public Player getPlayer() { return this.player; }
 
 
   
   public ItemStack getHeadItem() { return this.headItem; }
 
 
   
   public void setHeadItem(ItemStack headItem) { this.headItem = headItem; }
 
 
   
   public Boolean getIsDead() { return this.isDead; }
 
 
   
   public void setIsDead(Boolean isDead) { this.isDead = isDead; }
 
 
   
   public Boolean getIsDC() { return this.isDC; }
 
 
   
   public void setIsDC(Boolean isDC) { this.isDC = isDC; }
 
 
   
   public ColorInfo getColor() { return this.color; }
 
 
   
   public void setColor(ColorInfo color) { this.color = color; }
 
 
 
   
   public int compareTo(VitalsPlayerInfo o) { return this.joinedId.compareTo(o.getJoinedId()); }
 
 
   
   public Integer getJoinedId() { return this.joinedId; }
 }


