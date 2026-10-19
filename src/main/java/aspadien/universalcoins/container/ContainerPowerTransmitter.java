/*    */ package aspadien.universalcoins.container;
/*    */ 
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
/*    */ import aspadien.universalcoins.tileentity.TilePowerTransmitter;
/*    */ 
/*    */ public class ContainerPowerTransmitter extends Container {
/*    */   private final TilePowerTransmitter tileEntity;
/*    */   private final IIntArray data;
/*    */   
/*    */   public ContainerPowerTransmitter(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/* 22 */     this(windowId, playerInv, (IInventory)new Inventory(2), (IIntArray)new IntArray(5), (TilePowerTransmitter)playerInv.player.level
/* 23 */         .getBlockEntity(extraData.readBlockPos()));
/*    */   }
/*    */ 
/*    */   
/*    */   public ContainerPowerTransmitter(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TilePowerTransmitter te) {
/* 28 */     super((ContainerType)ModContainerTypes.POWER_TRANSMITTER.get(), windowId);
/* 29 */     this.tileEntity = te;
/* 30 */     this.data = data;
/*    */     
/* 32 */     addSlot(new UCSlotCard(inventory, 0, 22, 42));
/* 33 */     addSlot(new UCSlotOutput(inventory, 1, 138, 42));
/*    */     
/* 35 */     layoutPlayerInventorySlots(playerInv, 8, 75);
/* 36 */     addDataSlots(data);
/*    */   }
/*    */   public int getSyncedValue(int index) {
/* 39 */     return this.data.get(index);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean stillValid(PlayerEntity player) {
/* 44 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*    */   }
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/* 49 */     ItemStack itemstack = ItemStack.EMPTY;
/* 50 */     Slot slot = this.slots.get(index);
/* 51 */     if (slot != null && slot.hasItem()) {
/* 52 */       ItemStack itemstack1 = slot.getItem();
/* 53 */       itemstack = itemstack1.copy();
/* 54 */       if (index < 2)
/* 55 */       { if (!moveItemStackTo(itemstack1, 2, this.slots.size(), true)) return ItemStack.EMPTY;
/*    */          }
/* 57 */       else if (!moveItemStackTo(itemstack1, 0, 1, false)) { return ItemStack.EMPTY; }
/*    */       
/* 59 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 60 */       else { slot.setChanged(); }
/*    */     
/* 62 */     }  return itemstack;
/*    */   }
/*    */   
/*    */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/* 66 */     for (int i = 0; i < 3; i++) {
/* 67 */       for (int j = 0; j < 9; j++) {
/* 68 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*    */       }
/*    */     } 
/* 71 */     for (int k = 0; k < 9; k++)
/* 72 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58)); 
/*    */   }
/*    */   
/*    */   public TilePowerTransmitter getTile() {
/* 76 */     return this.tileEntity;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerPowerTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */