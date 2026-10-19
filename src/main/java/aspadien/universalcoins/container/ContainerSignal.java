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
/*     */ import aspadien.universalcoins.tileentity.TileSignal;
/*     */ 
/*     */ public class ContainerSignal extends Container {
/*     */   private final TileSignal tileEntity;
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerSignal(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  22 */     this(windowId, playerInv, (IInventory)new Inventory(1), (IIntArray)new IntArray(5), (TileSignal)playerInv.player.level
/*  23 */         .getBlockEntity(extraData.readBlockPos()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ContainerSignal(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileSignal te) {
/*  28 */     super((ContainerType)ModContainerTypes.SIGNAL.get(), windowId);
/*  29 */     this.tileEntity = te;
/*  30 */     this.data = data;
/*     */ 
/*     */     
/*  33 */     checkContainerSize(inventory, 1);
/*     */ 
/*     */     
/*  36 */     addSlot(new UCSlotOutput(inventory, 0, 148, 73));
/*     */ 
/*     */     
/*  39 */     layoutPlayerInventorySlots(playerInv, 8, 108);
/*     */ 
/*     */     
/*  42 */     addDataSlots(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/*  47 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*     */   }
/*     */   
/*     */   public int getSyncedValue(int index) {
/*  51 */     return this.data.get(index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  59 */     ItemStack itemstack = ItemStack.EMPTY;
/*  60 */     Slot slot = this.slots.get(index);
/*     */     
/*  62 */     if (slot != null && slot.hasItem()) {
/*  63 */       ItemStack itemstack1 = slot.getItem();
/*  64 */       itemstack = itemstack1.copy();
/*     */       
/*  66 */       if (index == 0) {
/*  67 */         if (!moveItemStackTo(itemstack1, 1, this.slots.size(), true)) {
/*  68 */           return ItemStack.EMPTY;
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/*  73 */         return ItemStack.EMPTY;
/*     */       } 
/*     */       
/*  76 */       if (itemstack1.isEmpty()) {
/*  77 */         slot.set(ItemStack.EMPTY);
/*     */       } else {
/*  79 */         slot.setChanged();
/*     */       } 
/*     */     } 
/*  82 */     return itemstack;
/*     */   }
/*     */   
/*     */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/*  86 */     for (int i = 0; i < 3; i++) {
/*  87 */       for (int j = 0; j < 9; j++) {
/*  88 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*     */       }
/*     */     } 
/*  91 */     for (int k = 0; k < 9; k++) {
/*  92 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removed(PlayerEntity player) {
/*  98 */     super.removed(player);
/*     */     
/* 100 */     if (!player.level.isClientSide && this.tileEntity != null) {
/* 101 */       this.tileEntity.inUseCleanup();
/*     */     }
/*     */   }
/*     */   
/*     */   public TileSignal getTile() {
/* 106 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerSignal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */