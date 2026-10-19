/*     */ package aspadien.universalcoins.container;
/*     */ 
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.Inventory;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.ContainerType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.PacketBuffer;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IntArray;
/*     */ import aspadien.universalcoins.init.ModContainerTypes;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.tileentity.TilePackager;
/*     */ 
/*     */ public class ContainerPackager
/*     */   extends Container {
/*     */   private final TilePackager tileEntity;
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerPackager(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  24 */     this(windowId, playerInv, (IInventory)new Inventory(12), (IIntArray)new IntArray(9), (TilePackager)playerInv.player.level
/*  25 */         .getBlockEntity(extraData.readBlockPos()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ContainerPackager(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TilePackager te) {
/*  30 */     super((ContainerType)ModContainerTypes.PACKAGER.get(), windowId);
/*  31 */     this.tileEntity = te;
/*  32 */     this.data = data;
/*     */ 
/*     */     
/*  35 */     for (int i = 0; i < 4; i++) {
/*  36 */       for (int j = 0; j < 2; j++) {
/*  37 */         int slotIndex = TilePackager.itemPackageSlots[i * 2 + j];
/*  38 */         addSlot(new Slot(inventory, slotIndex, 8 + i * 18, 22 + j * 18));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  43 */     addSlot(new UCSlotCard(inventory, 8, 8, 73));
/*  44 */     addSlot(new UCSlotCoinInput(inventory, 9, 26, 73));
/*  45 */     addSlot(new UCSlotOutput(inventory, 10, 152, 73));
/*     */     
/*  47 */     addSlot(new UCSlotPackage(inventory, 11, 172, 22));
/*     */ 
/*     */     
/*  50 */     layoutPlayerInventorySlots(playerInv, 8, 108);
/*     */ 
/*     */     
/*  53 */     addDataSlots(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/*  58 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  63 */     ItemStack itemstack = ItemStack.EMPTY;
/*  64 */     Slot slot = this.slots.get(index);
/*     */     
/*  66 */     if (slot != null && slot.hasItem()) {
/*  67 */       ItemStack itemstack1 = slot.getItem();
/*  68 */       itemstack = itemstack1.copy();
/*     */       
/*  70 */       if (index < 12) {
/*  71 */         if (!moveItemStackTo(itemstack1, 12, this.slots.size(), true)) {
/*  72 */           return ItemStack.EMPTY;
/*     */         }
/*     */       } else {
/*  75 */         int packageSize = this.data.get(2);
/*  76 */         int isSendMode = this.data.get(3);
/*     */ 
/*     */         
/*  79 */         if (itemstack1.getItem() instanceof aspadien.universalcoins.item.ItemCoin) {
/*  80 */           if (!moveItemStackTo(itemstack1, 9, 10, false))
/*     */           {
/*  82 */             if (isSendMode == 0)
/*  83 */             { if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY;  }
/*  84 */             else { return ItemStack.EMPTY; }
/*     */ 
/*     */           
/*     */           }
/*  88 */         } else if (isSendMode == 1 && itemstack1.getItem() == ModItems.UC_PACKAGE.get()) {
/*  89 */           if (!moveItemStackTo(itemstack1, 11, 12, false)) return ItemStack.EMPTY;
/*     */         
/*     */         }
/*  92 */         else if (slot.mayPlace(itemstack1) && index >= 12) {
/*  93 */           if (!moveItemStackTo(itemstack1, 8, 9, false))
/*     */           {
/*  95 */             if (isSendMode == 0)
/*     */             
/*  97 */             { if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY;  }
/*  98 */             else { return ItemStack.EMPTY; }
/*     */           
/*     */           }
/* 101 */         } else if (isSendMode == 0) {
/* 102 */           if (!moveIntoPackingSlots(itemstack1, packageSize)) return ItemStack.EMPTY; 
/*     */         } else {
/* 104 */           return ItemStack.EMPTY;
/*     */         } 
/*     */       } 
/*     */       
/* 108 */       if (itemstack1.isEmpty()) {
/* 109 */         slot.set(ItemStack.EMPTY);
/*     */       } else {
/* 111 */         slot.setChanged();
/*     */       } 
/*     */     } 
/* 114 */     return itemstack;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean moveIntoPackingSlots(ItemStack stack, int size) {
/* 125 */     int startSlot = (size == 0) ? 4 : ((size == 1) ? 2 : 0);
/* 126 */     return moveItemStackTo(stack, startSlot, 8, false);
/*     */   }
/*     */   
/*     */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/* 130 */     for (int i = 0; i < 3; i++) {
/* 131 */       for (int j = 0; j < 9; j++) {
/* 132 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*     */       }
/*     */     } 
/* 135 */     for (int k = 0; k < 9; k++) {
/* 136 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removed(PlayerEntity player) {
/* 142 */     super.removed(player);
/* 143 */     if (this.tileEntity != null && !player.level.isClientSide)
/* 144 */       this.tileEntity.inUseCleanup(); 
/*     */   }
/*     */   
/*     */   public int getSyncedValue(int index) {
/* 148 */     return this.data.get(index);
/*     */   }
/*     */   public TilePackager getTile() {
/* 151 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerPackager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */