 package aspadien.universalcoins.util;

 import net.minecraft.nbt.CompoundTag;

 public class UniversalPower
 {
   private static final UniversalPower INSTANCE = new UniversalPower();

   public static UniversalPower getInstance() {
     return INSTANCE;
   }

   public long getFeLevel() {
     return getWorldLong("power");
   }

   public long extractEnergy(int maxSend, boolean simulate) {
     long powerLevel = getWorldLong("power");
     long toSend = Math.min(maxSend, powerLevel);
     if (!simulate) {
       setWorldLong("power", powerLevel - toSend);
     }
     return toSend;
   }

   public long receiveEnergy(int maxReceive, boolean simulate) {
     long powerLevel = getWorldLong("power");
     long canReceive = Long.MAX_VALUE - powerLevel;
     long toReceive = Math.min(maxReceive, canReceive);

     if (!simulate) {
       setWorldLong("power", powerLevel + toReceive);
     }
     return toReceive;
   }

   private void setWorldLong(String tag, long data) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     wdTag.putLong(tag, data);
     wData.markDirty();
   }

   private long getWorldLong(String tag) {
     UCWorldData wData = UCWorldData.getInstance();
     CompoundTag wdTag = wData.getData();
     return wdTag.contains(tag) ? wdTag.getLong(tag) : 0L;
   }
 }
