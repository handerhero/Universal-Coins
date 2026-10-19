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
/*    */ import aspadien.universalcoins.util.CoinUtils;
/*    */ import aspadien.universalcoins.util.UCUtils;
/*    */ 
/*    */ public class ContainerVendorBuy extends Container {
/*    */   private final TileVendor tileEntity;
/*    */   private final IIntArray data;
/*    */   
/*    */   public ContainerVendorBuy(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/* 23 */     this(windowId, playerInv, (IInventory)new Inventory(17), (IIntArray)new IntArray(20), (TileVendor)playerInv.player.level
/* 24 */         .getBlockEntity(extraData.readBlockPos()));
/*    */   }
/*    */ 
/*    */   
/*    */   public ContainerVendorBuy(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileVendor te) {
/* 29 */     super((ContainerType)ModContainerTypes.VENDOR_BUY.get(), windowId);
/* 30 */     this.tileEntity = te;
/* 31 */     this.data = data;
/*    */ 
/*    */     
/* 34 */     addSlot(new UCSlotTradeBuyer(inventory, 9, 35, 24));
/*    */     
/* 36 */     addSlot(new UCSlotOutput(inventory, 12, 152, 24));
/*    */     
/* 38 */     addSlot(new UCSlotCard(inventory, 16, 17, 57));
/*    */     
/* 40 */     addSlot(new UCSlotOutput(inventory, 13, 152, 57));
/*    */     
/* 42 */     addSlot(new UCSlotCoinInput(inventory, 15, 35, 57));
/*    */     
/* 44 */     layoutPlayerInventorySlots(playerInv, 8, 108);
/* 45 */     addDataSlots(data);
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/* 50 */     ItemStack itemstack = ItemStack.EMPTY;
/* 51 */     Slot slot = this.slots.get(index);
/* 52 */     if (slot != null && slot.hasItem()) {
/* 53 */       ItemStack itemstack1 = slot.getItem();
/* 54 */       itemstack = itemstack1.copy();
/*    */       
/* 56 */       if (index < 5)
/* 57 */       { if (index == 0) return ItemStack.EMPTY; 
/* 58 */         if (!moveItemStackTo(itemstack1, 5, this.slots.size(), true)) return ItemStack.EMPTY;
/*    */          }
/* 60 */       else if (UCUtils.isCard(itemstack1))
/* 61 */       { if (!moveItemStackTo(itemstack1, 2, 3, false)) return ItemStack.EMPTY;  }
/* 62 */       else if (CoinUtils.getCoinValue(itemstack1) > 0)
/* 63 */       { if (!moveItemStackTo(itemstack1, 4, 5, false)) return ItemStack.EMPTY;  }
/* 64 */       else { return ItemStack.EMPTY; }
/*    */ 
/*    */       
/* 67 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 68 */       else { slot.setChanged(); }
/*    */     
/* 70 */     }  return itemstack;
/*    */   }
/*    */   
/*    */   private void layoutPlayerInventorySlots(PlayerInventory playerInv, int x, int y) {
/* 74 */     for (int i = 0; i < 3; i++) {
/* 75 */       for (int j = 0; j < 9; j++)
/* 76 */         addSlot(new Slot((IInventory)playerInv, j + i * 9 + 9, x + j * 18, y + i * 18)); 
/* 77 */     }  for (int k = 0; k < 9; k++)
/* 78 */       addSlot(new Slot((IInventory)playerInv, k, x + k * 18, y + 58)); 
/*    */   }
/*    */   
/* 81 */   public boolean stillValid(PlayerEntity p) { return (this.tileEntity != null && this.tileEntity.stillValid(p)); }
/* 82 */   public int getSyncedValue(int i) { return this.data.get(i); } public TileVendor getTile() {
/* 83 */     return this.tileEntity;
/*    */   }
/*    */   public void removed(PlayerEntity playerIn) {
/* 86 */     super.removed(playerIn);
/*    */     
/* 88 */     if (this.tileEntity != null && !playerIn.level.isClientSide)
/* 89 */       this.tileEntity.inUseCleanup(); 
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerVendorBuy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */