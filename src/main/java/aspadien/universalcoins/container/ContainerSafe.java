/*     */ package aspadien.universalcoins.container;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.entity.player.ServerPlayerEntity;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.Inventory;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.ContainerType;
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.PacketBuffer;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IntArray;
import aspadien.universalcoins.init.ModContainerTypes;
/*     */ import aspadien.universalcoins.tileentity.TileSafe;
/*     */ import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UniversalAccounts;
/*     */ 
/*     */ public class ContainerSafe extends Container {
/*     */   private final TileSafe tileEntity;
/*     */   private final IIntArray data;
/*     */   
/*     */   public ContainerSafe(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/*  23 */     this(windowId, playerInv, (IInventory)new Inventory(2), (IIntArray)new IntArray(2), (TileSafe)playerInv.player.level
/*  24 */         .getBlockEntity(extraData.readBlockPos()));
/*     */   }
/*     */   
/*     */   public ContainerSafe(int windowId, PlayerInventory playerInv, IInventory inventory, IIntArray data, TileSafe te) {
/*  28 */     super((ContainerType)ModContainerTypes.SAFE.get(), windowId);
/*  29 */     this.tileEntity = te;
/*  30 */     this.data = data;
/*     */ 
/*     */     
/*  33 */     addSlot(new Slot(inventory, 0, 27, 37));
/*     */ 
/*     */     
/*  36 */     addSlot(new Slot(inventory, 1, 134, 37)
/*     */         {
/*     */           public ItemStack onTake(PlayerEntity player, ItemStack stack)
/*     */           {
/*  40 */             if (!player.level.isClientSide) {
/*  41 */               int val = CoinUtils.getCoinValue(stack);
/*  42 */               if (UniversalAccounts.getInstance().debitAccount(ContainerSafe.this.tileEntity.currentSessionAccount, val, false)) {
/*  43 */                 ContainerSafe.this.tileEntity.refreshAccountData();
/*  44 */                 ContainerSafe.this.syncClient(player);
/*     */               } else {
/*  46 */                 stack.setCount(0);
/*     */               } 
/*     */             } 
/*  49 */             return super.onTake(player, stack);
/*     */           } public boolean mayPlace(ItemStack stack) {
/*  51 */             return false;
/*     */           }
/*     */         });
/*  54 */     layoutPlayerInventorySlots(playerInv, 8, 70);
/*  55 */     addDataSlots(data);
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/*  60 */     ItemStack itemstack = ItemStack.EMPTY;
/*  61 */     Slot slot = this.slots.get(index);
/*     */     
/*  63 */     if (slot != null && slot.hasItem()) {
/*  64 */       ItemStack stackInSlot = slot.getItem();
/*  65 */       itemstack = stackInSlot.copy();
/*     */ 
/*     */       
/*  68 */       if (index == 1) {
/*  69 */         int coinVal = CoinUtils.getCoinValue(stackInSlot);
/*  70 */         int space = getSpaceForStack(player.inventory, stackInSlot);
/*  71 */         int maxAffordable = (int)(this.tileEntity.accountBalance / coinVal);
/*  72 */         int amountToTake = Math.min(Math.min(space, maxAffordable), 64);
/*     */         
/*  74 */         if (amountToTake > 0 && 
/*  75 */           !player.level.isClientSide) {
/*  76 */           long totalCost = coinVal * amountToTake;
/*  77 */           if (UniversalAccounts.getInstance().debitAccount(this.tileEntity.currentSessionAccount, totalCost, false)) {
/*  78 */             ItemStack resultStack = stackInSlot.copy();
/*  79 */             resultStack.setCount(amountToTake);
/*  80 */             if (moveItemStackTo(resultStack, 2, 38, true)) {
/*  81 */               slot.set(ItemStack.EMPTY);
/*  82 */               this.tileEntity.refreshAccountData();
/*  83 */               syncClient(player);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/*  88 */         return ItemStack.EMPTY;
/*     */       } 
/*     */       
/*  91 */       if (index == 0)
/*  92 */       { if (!moveItemStackTo(stackInSlot, 2, 38, true)) return ItemStack.EMPTY;
/*     */         
/*     */          }
/*     */       
/*  96 */       else if (CoinUtils.getCoinValue(stackInSlot) > 0)
/*  97 */       { if (!moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;  }
/*  98 */       else { return ItemStack.EMPTY; }
/*     */ 
/*     */       
/* 101 */       if (stackInSlot.isEmpty()) { slot.set(ItemStack.EMPTY); }
/* 102 */       else { slot.setChanged(); }
/*     */     
/* 104 */     }  return itemstack;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void syncClient(PlayerEntity player) {
/* 111 */     if (player instanceof ServerPlayerEntity)
/*     */     {
/* 113 */       ((ServerPlayerEntity)player).refreshContainer(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private int getSpaceForStack(PlayerInventory inv, ItemStack stack) {
/* 118 */     int space = 0;
/* 119 */     for (int i = 0; i < 36; i++) {
/* 120 */       ItemStack invStack = inv.getItem(i);
/* 121 */       if (invStack.isEmpty()) { space += 64; }
/* 122 */       else if (invStack.getItem() == stack.getItem() && ItemStack.tagMatches(invStack, stack))
/* 123 */       { space += 64 - invStack.getCount(); }
/*     */     
/*     */     } 
/* 126 */     return space;
/*     */   }
/*     */   
/*     */   public int getSyncedValue(int index) {
/* 130 */     return this.data.get(index);
/* 131 */   } public boolean stillValid(PlayerEntity player) { return this.tileEntity.stillValid(player); } public void removed(PlayerEntity player) {
/* 132 */     super.removed(player); if (!player.level.isClientSide) this.tileEntity.inUseCleanup(); 
/*     */   } private void layoutPlayerInventorySlots(PlayerInventory playerInventory, int xStart, int yStart) {
/* 134 */     for (int i = 0; i < 3; ) { for (int j = 0; j < 9; ) { addSlot(new Slot((IInventory)playerInventory, j + i * 9 + 9, xStart + j * 18, yStart + i * 18)); j++; }  i++; }
/* 135 */      for (int k = 0; k < 9; ) { addSlot(new Slot((IInventory)playerInventory, k, xStart + k * 18, yStart + 58)); k++; }
/*     */   
/*     */   } public TileSafe getTile() {
/* 138 */     return this.tileEntity;
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerSafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */