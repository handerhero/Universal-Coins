/*     */ package aspadien.universalcoins.container;
/*     */ 
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.ContainerType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.PacketBuffer;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import aspadien.universalcoins.init.ModContainerTypes;
/*     */ import aspadien.universalcoins.tileentity.TileTradeStation;
/*     */ import aspadien.universalcoins.util.UCUtils;
/*     */ 
/*     */ public class ContainerTradeStation extends Container {
/*     */   private final TileTradeStation tileEntity;
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerTradeStation(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  22 */     this(windowId, playerInv, extraData.readBlockPos());
/*     */   }
/*     */ 
/*     */   
/*     */   private ContainerTradeStation(int windowId, PlayerInventory playerInv, BlockPos pos) {
/*  27 */     this(windowId, playerInv, (TileTradeStation)playerInv.player.level
/*  28 */         .getBlockEntity(pos));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ContainerTradeStation(int windowId, PlayerInventory playerInv, TileTradeStation te) {
/*  34 */     this(windowId, playerInv, (IInventory)te, te.tradeData, te);
/*     */   }
/*     */ 
/*     */   
/*     */   public ContainerTradeStation(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileTradeStation te) {
/*  39 */     super((ContainerType)ModContainerTypes.TRADE_STATION.get(), windowId);
/*  40 */     this.tileEntity = te;
/*  41 */     this.data = data;
/*     */ 
/*     */     
/*  44 */     addSlot(new UCSlotCard(inventory, 2, 12, 27));
/*  45 */     addSlot(new Slot(inventory, 0, 31, 27));
/*  46 */     addSlot(new UCSlotOutput(inventory, 1, 155, 27));
/*     */ 
/*     */     
/*  49 */     layoutPlayerInventorySlots(playerInv, 12, 119);
/*     */ 
/*     */     
/*  52 */     addDataSlots(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/*  57 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  62 */     ItemStack itemstack = ItemStack.EMPTY;
/*  63 */     Slot slot = this.slots.get(index);
/*     */     
/*  65 */     if (slot != null && slot.hasItem()) {
/*  66 */       ItemStack itemstack1 = slot.getItem();
/*  67 */       itemstack = itemstack1.copy();
/*     */       
/*  69 */       if (index < 3)
/*  70 */       { if (!moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
/*  71 */           return ItemStack.EMPTY;
/*     */         
/*     */         } }
/*     */       
/*  75 */       else if (UCUtils.isCard(itemstack1))
/*  76 */       { if (!moveItemStackTo(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
/*     */         
/*     */          }
/*     */       
/*  80 */       else if (!moveItemStackTo(itemstack1, 1, 2, false)) { return ItemStack.EMPTY; }
/*     */ 
/*     */ 
/*     */       
/*  84 */       if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
/*  85 */       else { slot.setChanged(); }
/*     */     
/*  87 */     }  return itemstack;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSyncedValue(int index) {
/*  92 */     return this.data.get(index);
/*     */   }
/*     */   
/*     */   private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/*  96 */     for (int i = 0; i < 3; i++) {
/*  97 */       for (int j = 0; j < 9; j++) {
/*  98 */         addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18));
/*     */       }
/*     */     } 
/* 101 */     for (int k = 0; k < 9; k++) {
/* 102 */       addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removed(PlayerEntity player) {
/* 108 */     super.removed(player);
/* 109 */     if (this.tileEntity != null) this.tileEntity.inUseCleanup(); 
/*     */   }
/*     */   public TileTradeStation getTile() {
/* 112 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerTradeStation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */