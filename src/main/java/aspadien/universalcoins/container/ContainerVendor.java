/*     */ package aspadien.universalcoins.container;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.Inventory;
/*     */ import net.minecraft.inventory.container.ClickType;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.ContainerType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.PacketBuffer;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IntArray;
/*     */ import aspadien.universalcoins.init.ModContainerTypes;
/*     */ import aspadien.universalcoins.tileentity.TileVendor;
/*     */ import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UCUtils;
/*     */ 
/*     */ public class ContainerVendor extends Container {
/*     */   private final TileVendor tileEntity;
/*     */   
/*     */   public ContainerVendor(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  23 */     this(windowId, playerInv, (IInventory)new Inventory(17), (IIntArray)new IntArray(20), (TileVendor)playerInv.player.level
/*  24 */         .getBlockEntity(extraData.readBlockPos()));
/*     */   }
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerVendor(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileVendor te) {
/*  29 */     super((ContainerType)ModContainerTypes.VENDOR.get(), windowId);
/*  30 */     this.tileEntity = te;
/*  31 */     this.data = data;
/*     */ 
/*     */     
/*  34 */     checkContainerSize(inventory, 17);
/*     */ 
/*     */     
/*  37 */     for (int i = 0; i < 9; i++) {
/*  38 */       addSlot(new Slot(inventory, i, 8 + i * 18, 96));
/*     */     }
/*     */ 
/*     */     
/*  42 */     addSlot(new UCSlotTradeOwner(inventory, 9, 9, 17));
/*     */ 
/*     */     
/*  45 */     addSlot(new UCSlotCoinInput(inventory, 14, 35, 55));
/*     */ 
/*     */     
/*  48 */     addSlot(new UCSlotCard(inventory, 10, 17, 55));
/*     */ 
/*     */     
/*  51 */     addSlot(new UCSlotOutput(inventory, 13, 152, 55));
/*     */ 
/*     */     
/*  54 */     layoutPlayerInventorySlots(playerInv, 8, 119);
/*     */ 
/*     */     
/*  57 */     addDataSlots(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSyncedValue(int index) {
/*  62 */     return this.data.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/*  67 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
/*  72 */     if (slotId >= 0 && slotId < this.slots.size()) {
/*  73 */       Slot slot = this.slots.get(slotId);
/*  74 */       if (slot instanceof UCSlotTradeOwner) {
/*  75 */         ItemStack cursorStack = player.inventory.getCarried();
/*  76 */         if (this.tileEntity != null) {
/*  77 */           this.tileEntity.setGhostSlot(cursorStack);
/*     */           
/*  79 */           slot.set(this.tileEntity.getItem(9));
/*     */         } 
/*  81 */         return cursorStack;
/*     */       } 
/*     */     } 
/*  84 */     return super.clicked(slotId, dragType, clickTypeIn, player);
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  89 */     ItemStack itemstack = ItemStack.EMPTY;
/*  90 */     Slot slot = this.slots.get(index);
/*     */     
/*  92 */     if (slot != null && slot.hasItem()) {
/*  93 */       ItemStack itemstack1 = slot.getItem();
/*  94 */       itemstack = itemstack1.copy();
/*     */ 
/*     */       
/*  97 */       if (index == 9) return ItemStack.EMPTY;
/*     */       
/*  99 */       if (index < 13)
/* 100 */       { if (!moveItemStackTo(itemstack1, 13, this.slots.size(), true)) return ItemStack.EMPTY;
/*     */          }
/*     */       
/* 103 */       else if (UCUtils.isCard(itemstack1))
/* 104 */       { if (!moveItemStackTo(itemstack1, 11, 12, false)) return ItemStack.EMPTY;
/*     */          }
/*     */       
/* 107 */       else if (CoinUtils.getCoinValue(itemstack1) > 0)
/* 108 */       { if (!moveItemStackTo(itemstack1, 10, 11, false)) return ItemStack.EMPTY;
/*     */         
/*     */          }
/*     */       
/* 112 */       else if (!moveItemStackTo(itemstack1, 0, 9, false)) { return ItemStack.EMPTY; }
/*     */ 
/*     */ 
/*     */       
/* 116 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 117 */       else { slot.setChanged(); }
/*     */     
/* 119 */     }  return itemstack;
/*     */   }
/*     */   
/*     */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/* 123 */     for (int i = 0; i < 3; i++) {
/* 124 */       for (int j = 0; j < 9; j++) {
/* 125 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*     */       }
/*     */     } 
/* 128 */     for (int k = 0; k < 9; k++) {
/* 129 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removed(PlayerEntity playerIn) {
/* 135 */     super.removed(playerIn);
/*     */     
/* 137 */     if (this.tileEntity != null && !playerIn.level.isClientSide)
/* 138 */       this.tileEntity.inUseCleanup(); 
/*     */   }
/*     */   
/*     */   public TileVendor getTile() {
/* 142 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerVendor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */