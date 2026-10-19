/*    */ package aspadien.universalcoins.util;
/*    */ 
/*    */

/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */
/*    */ import net.minecraft.world.server.ServerWorld;
/*    */ import net.minecraft.world.storage.WorldSavedData;
/*    */ import net.minecraftforge.fml.server.ServerLifecycleHooks;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

/*    */
/*    */ 
/*    */ public class UCWorldData
/*    */   extends WorldSavedData
/*    */ {
/* 15 */   private CompoundNBT nbt = new CompoundNBT();
/*    */   
/*    */   public UCWorldData() {
/* 18 */     super(MOD_ID);
/*    */   }

    /*    */
/*    */   
/*    */   public static UCWorldData getInstance() {
/* 28 */     ServerWorld world = ServerLifecycleHooks.getCurrentServer().overworld();
/*    */     
/* 30 */     return world.getDataStorage().computeIfAbsent(UCWorldData::new, MOD_ID);
/*    */   }
/*    */ 
/*    */
/*    */   public void load(CompoundNBT compound) {
/* 39 */     if (compound.contains(MOD_ID)) {
/* 40 */       this.nbt = compound.getCompound(MOD_ID);
/*    */     } else {
/* 42 */       this.nbt = new CompoundNBT();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public CompoundNBT save(CompoundNBT compound) {
/* 49 */     compound.put(MOD_ID, this.nbt);
/*    */     
/* 51 */     return compound;
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundNBT getData() {
/* 56 */     return this.nbt;
/*    */   }
/*    */ 
/*    */   
/*    */   public void markDirty() {
/* 61 */     setDirty();
/*    */   }
/*    */ }
