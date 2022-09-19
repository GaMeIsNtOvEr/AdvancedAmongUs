 package com.nktfh100.amongus.inventory;
 
 import com.nktfh100.AmongUs.enums.SabotageType;
 import com.nktfh100.AmongUs.info.ItemInfoContainer;
 import com.nktfh100.AmongUs.info.SabotageArena;
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import org.bukkit.Material;
 import org.bukkit.entity.Player;
 import org.bukkit.inventory.Inventory;
 import org.bukkit.scheduler.BukkitRunnable;
 
 
 public class SabotageLightsInv
   extends SabotageInvHolder
 {
   private ArrayList<Integer> lightsSwitches = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1) }));
   
   private HashMap<Player, Boolean> canClick = new HashMap<>();
 
   
   public SabotageLightsInv(SabotageArena saboArena) {
     super(27, Main.getMessagesManager().getGameMsg("sabotageLightsInvTitle", saboArena.getArena(), Main.getMessagesManager().getTaskName(SabotageType.LIGHTS.toString()), Main.getMessagesManager().getSabotageTitle(SabotageType.LIGHTS)), saboArena.getArena(), saboArena);
     Utils.fillInv(this.inv);
     this.lightsSwitches = Utils.generateLights();
     update();
     for (Player player : this.arena.getPlayers()) {
       this.canClick.put(player, Boolean.valueOf(true));
     }
   }
   
   public void LightSwitchClick(final Player player, final Integer i) {
     final SabotageLightsInv lightsInv = this;
     if (this.lightsSwitches.get(i) != null && (
       (Boolean)this.canClick.get(player)).booleanValue()) {
       this.canClick.put(player, Boolean.valueOf(false));
       (new BukkitRunnable()
         {
           public void run() {
             lightsInv.getCanClick().put(player, Boolean.valueOf(true));
             Main.getSoundsManager().playSound("sabotageLightsClick", player, player.getLocation());
             lightsInv.getLightsSwitches().set(i, Integer.valueOf((((Integer)lightsInv.getLightsSwitches().get(i)) == 0) ? 1 : 0));
             lightsInv.update();
             for (Integer light_ : lightsInv.getLightsSwitches()) {
               if (light_ == 0) {
                 return;
               }
             } 
             lightsInv.getSabotageArena().taskDone(player);
           }
         }).runTaskLater(Main.getPlugin(), 5L);
     } 
   }
 
 
 
   
   public Inventory getInventory() { return this.inv; }
 
 
 
   
   public void update() {
     this.inv.setItem(8, Main.getItemsManager().getItem("lightsSabotage_info").getItem().getItem());
     
     ItemInfoContainer switchItem = Main.getItemsManager().getItem("lightsSabotage_switch");
     final SabotageLightsInv lightsInv = this;
     int i = 11;
     int lightI = 0;
     for (Integer light_ : getLightsSwitches()) {
       Material mat = (light_ == 0) ? switchItem.getItem().getMat() : switchItem.getItem2().getMat();
       String name = (light_ == 0) ? switchItem.getItem().getTitle() : switchItem.getItem2().getTitle();
       ArrayList<String> lore = (light_ == 0) ? switchItem.getItem().getLore() : switchItem.getItem2().getLore();
       Icon icon = new Icon(Utils.createItem(mat, name, 1, lore));
       final Integer lightI_ = Integer.valueOf(lightI);
       icon.addClickAction(new ClickAction()
           {
             public void execute(Player player) {
               lightsInv.LightSwitchClick(player, lightI_);
             }
           });
       setIcon(i, icon);
       i++;
       lightI++;
     } 
   }
 
 
   
   public void invClosed(Player player) {}
 
   
   public ArrayList<Integer> getLightsSwitches() { return this.lightsSwitches; }
 
 
   
   public HashMap<Player, Boolean> getCanClick() { return this.canClick; }
 }


