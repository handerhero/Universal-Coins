/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
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
/*    */ import aspadien.universalcoins.tileentity.TilePowerReceiver;
/*    */ 
/*    */ public class ContainerPowerReceiver extends Container {
/*    */   private final TilePowerReceiver tileEntity;
/*    */   
/*    */   public ContainerPowerReceiver(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/* 22 */     this(windowId, playerInv, (IInventory)new Inventory(3), (IIntArray)new IntArray(7), (TilePowerReceiver)playerInv.player.level
/* 23 */         .getBlockEntity(extraData.readBlockPos()));
/*    */   }
/*    */   private final IIntArray data;
/*    */   public ContainerPowerReceiver(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TilePowerReceiver te) {
/* 27 */     super((ContainerType)ModContainerTypes.POWER_RECEIVER.get(), windowId);
/* 28 */     this.tileEntity = te;
/* 29 */     this.data = data;
/*    */     
/* 31 */     addSlot(new UCSlotCard(inventory, 0, 14, 70));
/* 32 */     addSlot(new UCSlotCoinInput(inventory, 1, 32, 70));
/* 33 */     addSlot(new UCSlotOutput(inventory, 2, 148, 70));
/*    */     
/* 35 */     layoutPlayerInventorySlots(playerInv, 8, 103);
/* 36 */     addDataSlots(data);
/*    */   }
/*    */   public int getSyncedValue(int index) {
/* 39 */     return this.data.get(index);
/*    */   }
/*    */   private void syncClient(PlayerEntity player) {
/* 42 */     if (player instanceof ServerPlayerEntity)
/*    */     {
/* 44 */       ((ServerPlayerEntity)player).refreshContainer(this); } 
/*    */   }
/*    */   
/*    */   public boolean stillValid(PlayerEntity player) {
/* 48 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*    */   }
/*    */   
/*    */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/* 52 */     ItemStack itemstack = ItemStack.EMPTY;
/* 53 */     Slot slot = this.slots.get(index);
/* 54 */     if (slot != null && slot.hasItem()) {
/* 55 */       ItemStack itemstack1 = slot.getItem();
/* 56 */       itemstack = itemstack1.copy();
/* 57 */       if (index < 3)
/* 58 */       { if (!moveItemStackTo(itemstack1, 3, this.slots.size(), true)) { syncClient(player); return ItemStack.EMPTY; }
/*    */          }
/* 60 */       else if (!moveItemStackTo(itemstack1, 0, 2, false)) { syncClient(player); return ItemStack.EMPTY; }
/*    */       
/* 62 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 63 */       else { slot.setChanged(); }
/*    */     
/* 65 */     }  syncClient(player);
/* 66 */     return itemstack;
/*    */   }
/*    */ 
/*    */   
/*    */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/* 71 */     for (int i = 0; i < 3; i++) {
/* 72 */       for (int j = 0; j < 9; j++) {
/* 73 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*    */       }
/*    */     } 
/* 76 */     for (int k = 0; k < 9; k++)
/* 77 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58)); 
/*    */   }
/*    */   
/*    */   public TilePowerReceiver getTile() {
/* 81 */     return this.tileEntity;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerPowerReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */