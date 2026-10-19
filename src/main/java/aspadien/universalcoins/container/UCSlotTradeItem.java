/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class UCSlotTradeItem
/*    */   extends Slot {
/*    */   public UCSlotTradeItem(IInventory inventoryIn, int index, int xPosition, int yPosition) {
/* 11 */     super(inventoryIn, index, xPosition, yPosition);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPickup(PlayerEntity playerIn) {
/* 16 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack stack) {
/* 23 */     return this.container.canPlaceItem(getSlotIndex(), stack);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/UCSlotTradeItem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */