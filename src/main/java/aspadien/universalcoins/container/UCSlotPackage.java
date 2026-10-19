/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import aspadien.universalcoins.init.ModItems;
/*    */ 
/*    */ public class UCSlotPackage extends Slot {
/*    */   public UCSlotPackage(IInventory inventoryIn, int index, int xPosition, int yPosition) {
/* 10 */     super(inventoryIn, index, xPosition, yPosition);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean mayPlace(ItemStack stack) {
/* 15 */     return (stack.getItem() == ModItems.UC_PACKAGE.get());
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/UCSlotPackage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */