 package com.nktfh100.amongus.managers;
 
 import com.nktfh100.AmongUs.enums.GameState;
 import com.nktfh100.AmongUs.info.Arena;
 import com.nktfh100.AmongUs.info.DoorGroup;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.PlayerInfo;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Collections;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.ItemStack;
 
 
 
 public class DoorsManager
 {
   private Arena arena;
   private ArrayList<DoorGroup> doorGroups = new ArrayList<>();
 
   
   public DoorsManager(Arena arena) { this.arena = arena; }
 
   
   public void closeDoorGroup(Player player, Integer id) {
     if (this.arena.getGameState() == GameState.RUNNING) {
       DoorGroup doorGroup = this.doorGroups.get(id);
       if (doorGroup.getCloseTimer() == 0 && doorGroup.getCooldownTimer(player.getUniqueId().toString()) == 0) {
 
         
         int s_ = 9;
         for (DoorGroup dg : getDoorGroups()) {
           if (dg.getId() == id) {
             ItemStack item = getSabotageDoorItem(player, id);
             player.getInventory().setItem(s_, item);
             break;
           } 
           s_++;
         } 
         
         doorGroup.setCloseTimer(this.arena.getDoorCloseTime());
         for (PlayerInfo pInfo : this.arena.getGameImposters()) {
           if (pInfo.getPlayer() != player) {
             doorGroup.setCooldownTimer(pInfo.getPlayer().getUniqueId().toString(), this.arena.getDoorCloseTime());
           }
         } 
         doorGroup.setCooldownTimer(player.getUniqueId().toString(), this.arena.getDoorCooldown());
         doorGroup.closeDoors(Boolean.valueOf(true));
         if (this.arena.getSabotageManager().getSabotageCoolDownTimer(player) <= this.arena.getDoorCloseTime()) {
           this.arena.getSabotageManager().setSabotageCoolDownTimer(player.getUniqueId().toString(), this.arena.getDoorCloseTime());
         }
       } 
     } 
   }
 
 
 
 
 
 
   
   public ItemStack getSabotageDoorItem(Player player, Integer doorGroupId) {
     DoorGroup doorGroup = this.arena.getDoorsManager().getDoorGroup(doorGroupId);
     ItemInfoContainer doorItem = Main.getItemsManager().getItem("sabotage_door");
     
     Integer cooldownInt = doorGroup.getCooldownTimer(player.getUniqueId().toString());
     if (cooldownInt == null) {
       cooldownInt = this.arena.getDoorCooldown();
     }
     String cooldownStr = cooldownInt.toString();
     String locName = doorGroup.getLocName().getName();
     
     Material mat = (cooldownInt == 0) ? doorItem.getItem2().getMat() : doorItem.getItem().getMat();
     String title = (cooldownInt == 0) ? doorItem.getItem2().getTitle(locName, cooldownStr) : doorItem.getItem().getTitle(locName, cooldownStr);
     ArrayList<String> lore = (cooldownInt == 0) ? doorItem.getItem2().getLore(locName, cooldownStr) : doorItem.getItem().getLore(locName, cooldownStr);
     return Utils.createItem(mat, title, (cooldownInt > 0) ? cooldownInt : 1, lore);
   }
   
   public void openDoorsForce() {
     for (DoorGroup dg : this.doorGroups) {
       dg.openDoors(Boolean.valueOf(false));
       dg.setCloseTimer(Integer.valueOf(0));
       for (String uuid_ : dg.getCooldownTimer().keySet()) {
         dg.setCooldownTimer(uuid_, this.arena.getDoorCooldown());
       }
     } 
   }
   
   public void resetDoors() {
     for (DoorGroup dg : this.doorGroups) {
       if (dg.getCloseTimer() > 0) {
         dg.openDoors(Boolean.valueOf(false));
       }
       dg.getCooldownTimer().clear();
       dg.setCloseTimer(Integer.valueOf(0));
     } 
   }
   
   public void addImposter(String uuid) {
     for (DoorGroup dg : this.doorGroups) {
       dg.setCooldownTimer(uuid, Integer.valueOf(0));
     }
   }
   
   public void removeImposter(String uuid) {
     for (DoorGroup dg : this.doorGroups) {
       dg.getCooldownTimer().remove(uuid);
     }
   }
   
   public void addDoorGroup(DoorGroup dg) {
     this.doorGroups.add(dg);
     Collections.sort(this.doorGroups);
   }
 
   
   public DoorGroup getDoorGroup(Integer id) { return this.doorGroups.get(id); }
 
   
   public void delete() {
     for (DoorGroup dg : this.doorGroups) {
       dg.delete();
     }
     this.doorGroups = null;
     this.arena = null;
   }
 
   
   public Arena getArena() { return this.arena; }
 
 
   
   public ArrayList<DoorGroup> getDoorGroups() { return this.doorGroups; }
 }


