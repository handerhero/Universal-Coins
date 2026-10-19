/*    */ package aspadien.universalcoins.container;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */ import net.minecraft.inventory.IInventory;
/*    */ import net.minecraft.inventory.Inventory;
/*    */ import net.minecraft.inventory.container.Container;
/*    */ import net.minecraft.inventory.container.ContainerType;
/*    */ import net.minecraft.inventory.container.Slot;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.util.IIntArray;
/*    */ import net.minecraft.util.IntArray;
/*    */ import aspadien.universalcoins.init.ModContainerTypes;
/*    */ import aspadien.universalcoins.tileentity.TileVendor;
/*    */ import aspadien.universalcoins.util.UCUtils;
/*    */ 
/*    */ public class ContainerVendorSell extends Container {
/*    */   private final TileVendor tileEntity;
/*    */   private final IIntArray data;
/*    */   
/*    */   public ContainerVendorSell(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/* 22 */     this(windowId, playerInv, (IInventory)new Inventory(17), (IIntArray)new IntArray(20), (TileVendor)playerInv.player.level
/* 23 */         .getBlockEntity(extraData.readBlockPos()));
/*    */   }
/*    */   
/*    */   public ContainerVendorSell(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileVendor te) {
/* 27 */     super((ContainerType)ModContainerTypes.VENDOR_SELL.get(), windowId);
/* 28 */     this.tileEntity = te;
/* 29 */     this.data = data;
/*    */ 
/*    */     
/* 32 */     addSlot(new UCSlotTradeBuyer(inventory, 9, 8, 24));
/*    */     
/* 34 */     addSlot(new Slot(inventory, 11, 26, 24));
/*    */     
/* 36 */     addSlot(new UCSlotCard(inventory, 16, 43, 64));
/*    */     
/* 38 */     addSlot(new UCSlotOutput(inventory, 13, 152, 64));
/*    */     
/* 40 */     layoutPlayerInventorySlots(playerInv, 8, 117);
/* 41 */     addDataSlots(data);
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/* 46 */     ItemStack itemstack = ItemStack.EMPTY;
/* 47 */     Slot slot = this.slots.get(index);
/* 48 */     if (slot != null && slot.hasItem()) {
/* 49 */       ItemStack itemstack1 = slot.getItem();
/* 50 */       itemstack = itemstack1.copy();
/* 51 */       if (index < 4) {
/* 52 */         if (index == 0) return ItemStack.EMPTY; 
/* 53 */         if (!moveItemStackTo(itemstack1, 4, this.slots.size(), true)) return ItemStack.EMPTY;
/*    */       
/* 55 */       } else if (UCUtils.isCard(itemstack1)) {
/* 56 */         if (!moveItemStackTo(itemstack1, 2, 3, false)) return ItemStack.EMPTY; 
/*    */       } else {
/* 58 */         ItemStack sample = this.tileEntity.getItem(9);
/* 59 */         if (!sample.isEmpty() && UCUtils.isSameItem(itemstack1, sample))
/* 60 */         { if (!moveItemStackTo(itemstack1, 1, 2, false)) return ItemStack.EMPTY;  }
/* 61 */         else { return ItemStack.EMPTY; }
/*    */       
/*    */       } 
/* 64 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 65 */       else { slot.setChanged(); }
/*    */     
/* 67 */     }  return itemstack;
/*    */   }
/*    */   
/*    */   private void layoutPlayerInventorySlots(PlayerInventory playerInv, int x, int y) {
/* 71 */     for (int i = 0; i < 3; i++) {
/* 72 */       for (int j = 0; j < 9; j++)
/* 73 */         addSlot(new Slot((IInventory)playerInv, j + i * 9 + 9, x + j * 18, y + i * 18)); 
/* 74 */     }  for (int k = 0; k < 9; k++)
/* 75 */       addSlot(new Slot((IInventory)playerInv, k, x + k * 18, y + 58)); 
/*    */   }
/*    */   
/* 78 */   public boolean stillValid(PlayerEntity p) { return (this.tileEntity != null && this.tileEntity.stillValid(p)); }
/* 79 */   public int getSyncedValue(int i) { return this.data.get(i); } public TileVendor getTile() {
/* 80 */     return this.tileEntity;
/*    */   }
/*    */   
/*    */   public void removed(PlayerEntity playerIn) {
/* 84 */     super.removed(playerIn);
/*    */     
/* 86 */     if (this.tileEntity != null && !playerIn.level.isClientSide)
/* 87 */       this.tileEntity.inUseCleanup(); 
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerVendorSell.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */