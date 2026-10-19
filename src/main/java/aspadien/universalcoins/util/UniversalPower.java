/*    */ package aspadien.universalcoins.util;
/*    */ 
/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */ 
/*    */ public class UniversalPower
/*    */ {
/*  7 */   private static final UniversalPower INSTANCE = new UniversalPower();
/*    */   
/*    */   public static UniversalPower getInstance() {
/* 10 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long getFeLevel() {
/* 16 */     return getWorldLong("power");
/*    */   }
/*    */   
/*    */   public long extractEnergy(int maxSend, boolean simulate) {
/* 20 */     long powerLevel = getWorldLong("power");
/* 21 */     long toSend = Math.min(maxSend, powerLevel);
/* 22 */     if (!simulate) {
/* 23 */       setWorldLong("power", powerLevel - toSend);
/*    */     }
/* 25 */     return toSend;
/*    */   }
/*    */   
/*    */   public long receiveEnergy(int maxReceive, boolean simulate) {
/* 29 */     long powerLevel = getWorldLong("power");
/* 30 */     long canReceive = Long.MAX_VALUE - powerLevel;
/* 31 */     long toReceive = Math.min(maxReceive, canReceive);
/*    */     
/* 33 */     if (!simulate) {
/* 34 */       setWorldLong("power", powerLevel + toReceive);
/*    */     }
/* 36 */     return toReceive;
/*    */   }
/*    */   
/*    */   private void setWorldLong(String tag, long data) {
/* 40 */     UCWorldData wData = UCWorldData.getInstance();
/* 41 */     CompoundNBT wdTag = wData.getData();
/* 42 */     wdTag.putLong(tag, data);
/* 43 */     wData.markDirty();
/*    */   }
/*    */   
/*    */   private long getWorldLong(String tag) {
/* 47 */     UCWorldData wData = UCWorldData.getInstance();
/* 48 */     CompoundNBT wdTag = wData.getData();
/* 49 */     return wdTag.contains(tag) ? wdTag.getLong(tag) : 0L;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/util/UniversalPower.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */