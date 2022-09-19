 package com.nktfh100.amongus.info;
 
 import com.nktfh100.AmongUs.main.Main;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.Location;
 import org.bukkit.block.Block;
 import org.bukkit.block.Sign;
 import org.bukkit.block.data.BlockData;
 import org.bukkit.block.data.Directional;
 
 
 public class JoinSign
 {
   Arena arena;
   Location loc;
   
   public JoinSign(Arena arena, Location loc) {
     this.arena = arena;
     this.loc = loc;
   }
   
   public void update() {
     Sign sign = (Sign)getBlock().getState();
     for (int ii = 0; ii < 4; ii++) {
       sign.setLine(ii, Main.getMessagesManager().getSignLine(ii, this.arena));
     }
     sign.update();
     if (getBlock().getType().toString().contains("SIGN")) {
       BlockData data = getBlock().getBlockData();
       if (data instanceof Directional) {
         Directional directional = (Directional)data;
         Block blockBehind = getBlock().getRelative(directional.getFacing().getOppositeFace());
         blockBehind.setType(Utils.getStateBlock(this.arena.getGameState()), true);
       } 
     } 
   }
 
 
   
   public Location getLocation() { return this.loc; }
 
 
   
   public Block getBlock() { return this.loc.getBlock(); }
 
 
   
   public Arena getArena() { return this.arena; }
 }


