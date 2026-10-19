/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.ItemStack;
/*    */ 
/*    */ public class UCSlotCoinInput
/*    */   extends Slot {
/*    */   public UCSlotCoinInput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
/* 10 */     super(inventoryIn, index, xPosition, yPosition);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack stack) {
/* 16 */     if (stack.isEmpty()) return false;
/*    */ 
/*    */     
/* 19 */     return stack.getItem() instanceof aspadien.universalcoins.item.ItemCoin;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/UCSlotCoinInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */