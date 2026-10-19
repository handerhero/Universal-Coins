/*     */ package aspadien.universalcoins.tileentity;
/*     */ 
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.ItemStackHelper;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.INamedContainerProvider;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.tileentity.ITickableTileEntity;
/*     */ import net.minecraft.tileentity.TileEntityType;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.container.ContainerSafe;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UniversalAccounts;
/*     */ 
/*     */ public class TileSafe
/*     */   extends TileProtected
/*     */   implements INamedContainerProvider, ITickableTileEntity
/*     */ {
/*  32 */   private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
/*     */ 
/*     */   
/*  35 */   public String currentSessionAccount = "none";
/*  36 */   public long accountBalance = 0L;
/*     */ 
/*     */   
/*  39 */   public String blockOwner = "nobody";
/*     */ 
/*     */   
/*  42 */   protected final IIntArray safeData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  45 */         switch (index) {
/*     */           case 0:
/*  47 */             return (int)(TileSafe.this.accountBalance & 0xFFFFFFFFL);
/*  48 */           case 1: return (int)(TileSafe.this.accountBalance >> 32L);
/*  49 */         }  return 0;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void set(int index, int value) {
/*  55 */         if (index == 0) TileSafe.this.accountBalance = TileSafe.this.accountBalance & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; 
/*  56 */         if (index == 1) TileSafe.this.accountBalance = TileSafe.this.accountBalance & 0xFFFFFFFFL | value << 32L; 
/*     */       } public int getCount() {
/*  58 */         return 2;
/*     */       }
/*     */     };
/*     */   public TileSafe() {
/*  62 */     super((TileEntityType)ModTileEntities.SAFE.get());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick() {
/*  68 */     if (this.level == null || this.level.isClientSide) {
/*     */       return;
/*     */     }
/*  71 */     if (this.currentSessionAccount.equals("none")) {
/*  72 */       if (!((ItemStack)this.items.get(1)).isEmpty()) {
/*  73 */         this.items.set(1, ItemStack.EMPTY);
/*  74 */         setChanged();
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  80 */     ItemStack inputStack = (ItemStack)this.items.get(0);
/*  81 */     if (!inputStack.isEmpty()) {
/*  82 */       int coinValue = CoinUtils.getCoinValue(inputStack);
/*  83 */       if (coinValue > 0) {
/*  84 */         long totalToDeposit = coinValue * inputStack.getCount();
/*     */         
/*  86 */         if (Long.MAX_VALUE - this.accountBalance >= totalToDeposit && 
/*  87 */           UniversalAccounts.getInstance().creditAccount(this.currentSessionAccount, totalToDeposit, false)) {
/*  88 */           inputStack.setCount(0);
/*  89 */           this.items.set(0, ItemStack.EMPTY);
/*  90 */           refreshAccountData();
/*  91 */           setChanged();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  98 */     if (((ItemStack)this.items.get(1)).isEmpty() && this.accountBalance > 0L) {
/*  99 */       fillOutputSlot();
/*     */     }
/*     */ 
/*     */     
/* 103 */     if (this.level.getGameTime() % 20L == 0L) {
/* 104 */       refreshAccountData();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fillOutputSlot() {
/* 113 */     if (this.accountBalance <= 0L || !((ItemStack)this.items.get(1)).isEmpty())
/* 114 */       return;  for (int i = 5; i >= 0; i--) {
/* 115 */       int coinVal = UniversalCoins.coinValues[i];
/* 116 */       if (this.accountBalance >= coinVal) {
/*     */         
/* 118 */         this.items.set(1, new ItemStack((IItemProvider)ModItems.getCoinByIndex(i), 1));
/* 119 */         setChanged();
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void refreshAccountData() {
/* 128 */     if (this.level != null && !this.level.isClientSide && !this.currentSessionAccount.equals("none")) {
/* 129 */       this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.currentSessionAccount);
/* 130 */       setChanged();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/* 138 */     if (this.level.getBlockEntity(this.worldPosition) != this) return false; 
/* 139 */     return (player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void inUseCleanup() {
/* 147 */     if (this.level == null || this.level.isClientSide)
/* 148 */       return;  this.currentSessionAccount = "none";
/* 149 */     this.accountBalance = 0L;
/* 150 */     this.items.set(1, ItemStack.EMPTY);
/* 151 */     this.inUse = false;
/* 152 */     setChanged();
/* 153 */     updateTE();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
/* 163 */     this.currentSessionAccount = UniversalAccounts.getInstance().getOrCreatePlayerAccount(player.getUUID().toString());
/* 164 */     refreshAccountData();
/* 165 */     this.inUse = true;
/*     */ 
/*     */ 
/*     */     
/* 169 */     IInventory proxy = new IInventory() {
/* 170 */         public int getContainerSize() { return 2; }
/* 171 */         public boolean isEmpty() { return TileSafe.this.items.stream().allMatch(ItemStack::isEmpty); } public ItemStack getItem(int i) {
/* 172 */           return (ItemStack)TileSafe.this.items.get(i);
/*     */         } public ItemStack removeItem(int i, int c) {
/* 174 */           ItemStack s = ItemStackHelper.removeItem((List)TileSafe.this.items, i, c);
/* 175 */           setChanged();
/* 176 */           return s;
/*     */         }
/* 178 */         public ItemStack removeItemNoUpdate(int i) { return ItemStackHelper.takeItem((List)TileSafe.this.items, i); }
/* 179 */         public void setItem(int i, ItemStack s) { TileSafe.this.items.set(i, s); setChanged(); }
/* 180 */         public void setChanged() { TileSafe.this.setChanged(); }
/* 181 */         public boolean stillValid(PlayerEntity p) { return TileSafe.this.stillValid(p); } public void clearContent() {
/* 182 */           TileSafe.this.items.clear(); setChanged();
/*     */         }
/*     */       };
/* 185 */     return (Container)new ContainerSafe(id, playerInv, proxy, this.safeData, this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void load(BlockState state, CompoundNBT tag) {
/* 190 */     super.load(state, tag);
/* 191 */     this.items.clear();
/* 192 */     ItemStackHelper.loadAllItems(tag, this.items);
/* 193 */     if (tag.contains("blockOwner")) this.blockOwner = tag.getString("blockOwner");
/*     */   
/*     */   }
/*     */   
/*     */   public CompoundNBT save(CompoundNBT tag) {
/* 198 */     super.save(tag);
/* 199 */     ItemStackHelper.saveAllItems(tag, this.items);
/* 200 */     tag.putString("blockOwner", this.blockOwner);
/* 201 */     return tag;
/*     */   }
/*     */ 
/*     */   
/*     */   public ITextComponent getDisplayName() {
/* 206 */     return (ITextComponent)new TranslationTextComponent("block.universalcoins.safe");
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TileSafe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */