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
/*     */ import aspadien.universalcoins.tileentity.TileATM;
/*     */ 
/*     */ public class ContainerATM extends Container {
/*     */   private final TileATM tileEntity;
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerATM(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  23 */     this(windowId, playerInv, (IInventory)new Inventory(2), (IIntArray)new IntArray(10), (TileATM)playerInv.player.level
/*  24 */         .getBlockEntity(extraData.readBlockPos()));
/*     */   }
/*     */ 
/*     */   
/*     */   public ContainerATM(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileATM te) {
/*  29 */     super((ContainerType)ModContainerTypes.ATM.get(), windowId);
/*  30 */     this.tileEntity = te;
/*  31 */     this.data = data;
/*     */ 
/*     */     
/*  34 */     addSlot(new UCSlotCoinInput(inventory, 0, 172, 40));
/*     */     
/*  36 */     addSlot(new UCSlotCard(inventory, 1, 172, 60));
/*     */ 
/*     */     
/*  39 */     layoutPlayerInventorySlots(playerInv, 18, 119);
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
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  52 */     ItemStack itemstack = ItemStack.EMPTY;
/*  53 */     Slot slot = this.slots.get(index);
/*     */     
/*  55 */     if (slot != null && slot.hasItem()) {
/*  56 */       ItemStack itemstack1 = slot.getItem();
/*  57 */       itemstack = itemstack1.copy();
/*     */       
/*  59 */       if (index < 2) {
/*  60 */         if (!moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
/*  61 */           return ItemStack.EMPTY;
/*     */         
/*     */         }
/*     */       }
/*  65 */       else if (itemstack1.getItem() instanceof aspadien.universalcoins.item.ItemCoin) {
/*  66 */         if (!moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY; 
/*  67 */       } else if (itemstack1.getItem() == ModItems.UC_CARD.get() || itemstack1.getItem() == ModItems.ENDER_CARD.get()) {
/*  68 */         if (!moveItemStackTo(itemstack1, 1, 2, false)) return ItemStack.EMPTY; 
/*     */       } else {
/*  70 */         return ItemStack.EMPTY;
/*     */       } 
/*     */ 
/*     */       
/*  74 */       if (itemstack1.isEmpty()) {
/*  75 */         slot.set(ItemStack.EMPTY);
/*     */       } else {
/*  77 */         slot.setChanged();
/*     */       } 
/*     */     } 
/*  80 */     return itemstack;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSyncedValue(int index) {
/*  85 */     return this.data.get(index);
/*     */   }
/*     */   
/*     */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/*  89 */     for (int i = 0; i < 3; i++) {
/*  90 */       for (int j = 0; j < 9; j++) {
/*  91 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*     */       }
/*     */     } 
/*  94 */     for (int k = 0; k < 9; k++) {
/*  95 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removed(PlayerEntity player) {
/* 101 */     super.removed(player);
/* 102 */     if (this.tileEntity != null) this.tileEntity.inUseCleanup(); 
/*     */   }
/*     */   public TileATM getTile() {
/* 105 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerATM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */