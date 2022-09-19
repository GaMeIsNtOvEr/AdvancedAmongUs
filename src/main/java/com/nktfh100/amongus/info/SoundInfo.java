 package com.nktfh100.amongus.info;
 
 import com.comphenix.protocol.events.PacketContainer;
 import com.nktfh100.AmongUs.utils.Packets;
 import com.nktfh100.AmongUs.utils.Utils;
 import org.bukkit.Location;
 import org.bukkit.Sound;
 
 
 
 public class SoundInfo
 {
   private Sound sound;
   private float volume;
   private float pitch;
   private int delay;
   private float volume2 = -1.0F;
   private float pitch2 = -1.0F;
   private int delay2 = -1;
   
   public SoundInfo(Sound sound, float volume, float pitch, int delay, float volume2, float pitch2, int delay2) {
     this.sound = sound;
     this.volume = volume;
     this.pitch = pitch;
     this.delay = delay;
     this.volume2 = volume2;
     this.pitch2 = pitch2;
     this.delay2 = delay2;
   }
 
   
   public PacketContainer getPacket(Location loc) { return Packets.NAMED_SOUND(loc, this.sound, Float.valueOf(getVolume()), Float.valueOf(getPitch())); }
 
 
   
   public Sound getSound() { return this.sound; }
 
   
   public float getVolume() {
     if (this.volume2 == -1.0F) {
       return this.volume;
     }
     return Utils.getRandomFloat(this.volume, this.volume2);
   }
 
   
   public float getPitch() {
     if (this.pitch2 == -1.0F) {
       return this.pitch;
     }
     return Utils.getRandomFloat(this.pitch, this.pitch2);
   }
 
   
   public int getDelay() {
     if (this.delay2 == -1.0F) {
       return this.delay;
     }
     return Utils.getRandomNumberInRange(this.delay, this.delay2);
   }
 }


