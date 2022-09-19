 package com.nktfh100.amongus.utils;
 
 import com.comphenix.protocol.PacketType;
 import com.comphenix.protocol.events.PacketContainer;
 import com.comphenix.protocol.reflect.IntEnum;
 import com.comphenix.protocol.utility.MinecraftReflection;
 import com.comphenix.protocol.wrappers.WrappedChatComponent;
 import java.util.Collection;
 import java.util.List;
 import org.bukkit.ChatColor;
 
 
 
 public class WrapperPlayServerScoreboardTeam
   extends AbstractPacket
 {
   public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;
   
   public WrapperPlayServerScoreboardTeam() {
     super(new PacketContainer(TYPE), TYPE);
     this.handle.getModifier().writeDefaults();
   }
 
   
   public WrapperPlayServerScoreboardTeam(PacketContainer packet) { super(packet, TYPE); }
 
   
   public static class Mode
     extends IntEnum
   {
     public static final int TEAM_CREATED = 0;
     
     public static final int TEAM_REMOVED = 1;
     
     public static final int TEAM_UPDATED = 2;
     
     public static final int PLAYERS_ADDED = 3;
     
     public static final int PLAYERS_REMOVED = 4;
     private static final Mode INSTANCE = new Mode();
 
     
     public static Mode getInstance() { return INSTANCE; }
   }
 
 
 
 
 
 
 
 
 
   
   public String getName() { return (String)this.handle.getStrings().read(0); }
 
 
 
 
 
 
 
   
   public void setName(String value) { this.handle.getStrings().write(0, value); }
 
 
 
 
 
 
 
 
 
   
   public WrappedChatComponent getDisplayName() { return (WrappedChatComponent)this.handle.getChatComponents().read(0); }
 
 
 
 
 
 
 
   
   public void setDisplayName(WrappedChatComponent value) { this.handle.getChatComponents().write(0, value); }
 
 
 
 
 
 
 
 
 
 
   
   public WrappedChatComponent getPrefix() { return (WrappedChatComponent)this.handle.getChatComponents().read(1); }
 
 
 
 
 
 
 
   
   public void setPrefix(WrappedChatComponent value) { this.handle.getChatComponents().write(1, value); }
 
 
 
 
 
 
 
 
 
 
   
   public WrappedChatComponent getSuffix() { return (WrappedChatComponent)this.handle.getChatComponents().read(2); }
 
 
 
 
 
 
 
   
   public void setSuffix(WrappedChatComponent value) { this.handle.getChatComponents().write(2, value); }
 
 
 
 
 
 
 
 
 
 
   
   public String getNameTagVisibility() { return (String)this.handle.getStrings().read(1); }
 
 
 
 
 
 
 
   
   public void setNameTagVisibility(String value) { this.handle.getStrings().write(1, value); }
 
 
 
 
 
 
 
 
 
   
   public ChatColor getColor() { return (ChatColor)this.handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0); }
 
 
 
 
 
 
 
   
   public void setColor(ChatColor value) { this.handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value); }
 
 
 
 
 
 
 
   
   public String getCollisionRule() { return (String)this.handle.getStrings().read(2); }
 
 
 
 
 
 
   
   public void setCollisionRule(String value) { this.handle.getStrings().write(2, value); }
 
 
 
 
 
 
 
 
 
 
   
   public List<String> getPlayers() {
     return (List<String>)this.handle.getSpecificModifier(Collection.class)
       .read(0);
   }
 
 
 
 
 
 
   
   public void setPlayers(List<String> value) { this.handle.getSpecificModifier(Collection.class).write(0, value); }
 
 
 
 
 
 
 
 
 
 
 
   
   public int getMode() { return ((Integer)this.handle.getIntegers().read(0)); }
 
 
 
 
 
 
 
   
   public void setMode(int value) { this.handle.getIntegers().write(0, Integer.valueOf(value)); }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public int getPackOptionData() { return ((Integer)this.handle.getIntegers().read(1)); }
 
 
 
 
 
 
 
 
   
   public void setPackOptionData(int value) { this.handle.getIntegers().write(1, Integer.valueOf(value)); }
 }


