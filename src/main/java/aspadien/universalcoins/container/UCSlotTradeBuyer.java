/*    */ package aspadien.universalcoins.container;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class UCSlotTradeBuyer extends Slot {
/*    */   public UCSlotTradeBuyer(IInventory inv, int index, int x, int y) {
/*  9 */     super(inv, index, x, y);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack stack) {
/* 14 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPickup(PlayerEntity player) {
/* 19 */     return false;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/UCSlotTradeBuyer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */