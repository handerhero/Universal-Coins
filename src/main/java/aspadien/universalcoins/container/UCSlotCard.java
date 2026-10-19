/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import aspadien.universalcoins.init.ModItems;
/*    */ 
/*    */ public class UCSlotCard extends Slot {
/*    */   public UCSlotCard(IInventory inventoryIn, int index, int xPosition, int yPosition) {
/* 11 */     super(inventoryIn, index, xPosition, yPosition);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack stack) {
/* 16 */     Item item = stack.getItem();
/* 17 */     return (item == ModItems.UC_CARD.get() || item == ModItems.ENDER_CARD.get());
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/UCSlotCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */