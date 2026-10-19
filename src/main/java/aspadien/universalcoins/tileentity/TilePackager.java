/*     */ package aspadien.universalcoins.tileentity;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.item.ItemEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.entity.player.ServerPlayerEntity;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.ItemStackHelper;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.INamedContainerProvider;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.nbt.INBT;
/*     */ import net.minecraft.nbt.ListNBT;
/*     */ import net.minecraft.tileentity.ITickableTileEntity;
/*     */ import net.minecraft.tileentity.TileEntityType;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.Util;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
/*     */ import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.container.ContainerPackager;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCPackagerServerMessage;
import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UCUtils;
/*     */ 
/*     */ public class TilePackager extends TileProtected implements IInventory, INamedContainerProvider, ITickableTileEntity {
/*  35 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(12, ItemStack.EMPTY);
/*     */   
/*  37 */   public static final int[] itemPackageSlots = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }; public static final int itemCardSlot = 8; public static final int itemCoinSlot = 9;
/*     */   public static final int itemOutputSlot = 10;
/*     */   public static final int itemPackageInputSlot = 11;
/*  40 */   public long coinSum = 0L;
/*  41 */   public int packageSize = 0;
/*     */   public boolean sendMode = false;
/*     */   public boolean buyButtonActive = false;
/*     */   public boolean coinButtonActive = false;
/*  45 */   public String packageTarget = "";
/*  46 */   public int[] packageCost = new int[] { 10, 20, 40 };
/*     */   
/*  48 */   protected final IIntArray packagerData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  51 */         switch (index) { case 0:
/*  52 */             return (int)(TilePackager.this.coinSum & 0xFFFFFFFFL);
/*  53 */           case 1: return (int)(TilePackager.this.coinSum >> 32L);
/*  54 */           case 2: return TilePackager.this.packageSize;
/*  55 */           case 3: return TilePackager.this.sendMode ? 1 : 0;
/*  56 */           case 4: return TilePackager.this.buyButtonActive ? 1 : 0;
/*  57 */           case 5: return TilePackager.this.coinButtonActive ? 1 : 0;
/*  58 */           case 6: return TilePackager.this.packageCost[0];
/*  59 */           case 7: return TilePackager.this.packageCost[1];
/*  60 */           case 8: return TilePackager.this.packageCost[2]; }
/*  61 */          return 0;
/*     */       }
/*     */ 
/*     */       
/*     */       public void set(int index, int value) {
/*  66 */         switch (index) { case 0:
/*  67 */             TilePackager.this.coinSum = TilePackager.this.coinSum & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; break;
/*  68 */           case 1: TilePackager.this.coinSum = TilePackager.this.coinSum & 0xFFFFFFFFL | value << 32L; break;
/*  69 */           case 2: TilePackager.this.packageSize = value; break;
/*  70 */           case 3: TilePackager.this.sendMode = (value == 1); break;
/*  71 */           case 4: TilePackager.this.buyButtonActive = (value == 1); break;
/*  72 */           case 5: TilePackager.this.coinButtonActive = (value == 1);
/*     */             break; }
/*     */          } public int getCount() {
/*  75 */         return 9;
/*     */       }
/*     */     };
/*     */   public TilePackager() {
/*  79 */     super((TileEntityType)ModTileEntities.PACKAGER.get());
/*  80 */     this.packageCost[0] = UniversalCoins.smallPackagePrice;
/*  81 */     this.packageCost[1] = UniversalCoins.medPackagePrice;
/*  82 */     this.packageCost[2] = UniversalCoins.largePackagePrice;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick() {
/*  87 */     if (this.level == null || this.level.isClientSide)
/*  88 */       return;  absorbCoins();
/*  89 */     updateButtonStates();
/*  90 */     if (this.level.getGameTime() % 10L == 0L) setChanged(); 
/*     */   }
/*     */   
/*     */   private void absorbCoins() {
/*  94 */     ItemStack coinStack = getItem(9);
/*  95 */     int val = CoinUtils.getCoinValue(coinStack);
/*  96 */     if (val > 0) {
/*  97 */       this.coinSum += val * coinStack.getCount();
/*  98 */       coinStack.setCount(0);
/*  99 */       setItem(9, ItemStack.EMPTY);
/*     */ 
/*     */       
/* 102 */       setChanged();
/* 103 */       updateTE();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateButtonStates() {
/* 108 */     this.coinButtonActive = (this.coinSum > 0L);
/* 109 */     if (!this.sendMode) {
/* 110 */       long funds = UCUtils.getCardBalance(getItem(8));
/* 111 */       if (funds < 0L) funds = 0L; 
/* 112 */       funds += this.coinSum;
/* 113 */       this.buyButtonActive = (funds >= this.packageCost[this.packageSize] && getItem(10).isEmpty());
/*     */     } else {
/* 115 */       this.buyButtonActive = (!getItem(11).isEmpty() && !this.packageTarget.isEmpty());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void onButtonPressed(int id, boolean shift) {
/* 120 */     if (this.level == null || this.level.isClientSide)
/*     */       return; 
/* 122 */     if (id == 0) {
/* 123 */       if (this.sendMode) { sendPackage(this.packageTarget); } else { createPackage(); } 
/* 124 */     } else if (id == 1) {
/* 125 */       fillOutputSlot();
/* 126 */     } else if (id >= 2 && id <= 4) {
/* 127 */       int newSize = id - 2;
/*     */       
/* 129 */       if (newSize < this.packageSize) {
/* 130 */         ejectRedundantSlots(newSize);
/*     */       }
/* 132 */       this.packageSize = newSize;
/* 133 */     } else if (id == 5) {
/* 134 */       this.sendMode = !this.sendMode;
/*     */       
/* 136 */       if (this.sendMode) {
/* 137 */         ejectRedundantSlots(-1);
/*     */       }
/*     */     } 
/*     */     
/* 141 */     setChanged();
/* 142 */     updateTE();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ejectRedundantSlots(int newSize) {
/* 150 */     if (this.level == null || this.level.isClientSide || this.level.getServer() == null) {
/*     */       return;
/*     */     }
/* 153 */     int firstVisibleSlot = (newSize == 0) ? 4 : ((newSize == 1) ? 2 : ((newSize == 2) ? 0 : 8));
/*     */     
/* 155 */     for (int i = 0; i < firstVisibleSlot; i++) {
/* 156 */       ItemStack stack = getItem(i);
/* 157 */       if (!stack.isEmpty()) {
/*     */         
/* 159 */         ServerPlayerEntity serverPlayerEntity = this.level.getServer().getPlayerList().getPlayerByName(this.playerName);
/*     */         
/* 161 */         if (serverPlayerEntity != null && ((PlayerEntity)serverPlayerEntity).inventory.add(stack.copy())) {
/*     */           
/* 163 */           stack.setCount(0);
/*     */ 
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 170 */           ItemEntity itemEntity = new ItemEntity(this.level, this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 1.0D, this.worldPosition.getZ() + 0.5D, stack.copy());
/* 171 */           this.level.addFreshEntity((Entity)itemEntity);
/* 172 */           stack.setCount(0);
/*     */         } 
/*     */         
/* 175 */         this.inventory.set(i, ItemStack.EMPTY);
/*     */       } 
/*     */     } 
/* 178 */     setChanged();
/* 179 */     updateTE();
/*     */   }
/*     */   
/*     */   private void createPackage() {
/* 183 */     if (!getItem(10).isEmpty())
/*     */       return; 
/* 185 */     int cost = this.packageCost[this.packageSize];
/* 186 */     if (!pay(cost))
/*     */       return; 
/* 188 */     ListNBT list = new ListNBT();
/*     */     
/* 190 */     int startSlot = (this.packageSize == 0) ? 4 : ((this.packageSize == 1) ? 2 : 0);
/*     */     
/* 192 */     for (int i = startSlot; i < 8; i++) {
/* 193 */       ItemStack s = getItem(i);
/* 194 */       if (!s.isEmpty()) {
/* 195 */         list.add(s.save(new CompoundNBT()));
/* 196 */         s.setCount(0);
/* 197 */         this.inventory.set(i, ItemStack.EMPTY);
/*     */       } 
/*     */     } 
/*     */     
/* 201 */     if (!list.isEmpty()) {
/* 202 */       ItemStack pkg = new ItemStack((IItemProvider)ModItems.UC_PACKAGE.get());
/* 203 */       pkg.getOrCreateTag().put("Inventory", (INBT)list);
/* 204 */       this.inventory.set(10, pkg);
/*     */     } else {
/*     */       
/* 207 */       this.coinSum += cost;
/*     */     } 
/* 209 */     setChanged();
/* 210 */     updateTE();
/*     */   }
/*     */   
/*     */   private boolean pay(int cost) {
/* 214 */     if (this.coinSum >= cost) {
/* 215 */       this.coinSum -= cost;
/* 216 */       return true;
/*     */     } 
/* 218 */     ItemStack card = getItem(8);
/* 219 */     return (!card.isEmpty() && UCUtils.debitCard(card, cost));
/*     */   }
/*     */   
/*     */   public void sendPackage(String targetName) {
/* 223 */     ItemStack pkg = getItem(11);
/* 224 */     ServerPlayerEntity serverPlayerEntity = this.level.getServer().getPlayerList().getPlayerByName(targetName);
/* 225 */     if (serverPlayerEntity != null && !pkg.isEmpty()) {
/* 226 */       if (!((PlayerEntity)serverPlayerEntity).inventory.add(pkg.copy())) serverPlayerEntity.drop(pkg.copy(), false); 
/* 227 */       serverPlayerEntity.sendMessage((ITextComponent)(new StringTextComponent(this.playerName)).withStyle(TextFormatting.GREEN)
/* 228 */           .append((ITextComponent)new TranslationTextComponent("packager.message.sent")), Util.NIL_UUID);
/* 229 */       this.inventory.set(11, ItemStack.EMPTY);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fillOutputSlot() {
/* 234 */     if (getItem(10).isEmpty() && this.coinSum > 0L) {
/* 235 */       for (int i = 4; i >= 0; i--) {
/* 236 */         int val = UniversalCoins.coinValues[i];
/* 237 */         if (this.coinSum >= val) {
/* 238 */           ItemStack coins = new ItemStack((IItemProvider)ModItems.getCoinByIndex(i), (int)Math.min(this.coinSum / val, 64L));
/* 239 */           setItem(10, coins);
/* 240 */           this.coinSum -= coins.getCount() * val;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void playerLookup(String name, boolean tab) {
/* 248 */     if (tab)
/* 249 */     { List<ServerPlayerEntity> players = this.level.getServer().getPlayerList().getPlayers();
/* 250 */       for (ServerPlayerEntity p : players) {
/* 251 */         if (p.getName().getString().toLowerCase().startsWith(name.toLowerCase())) {
/* 252 */           this.packageTarget = p.getName().getString(); break;
/*     */         } 
/*     */       }  }
/* 255 */     else { this.packageTarget = name; }
/* 256 */      setChanged(); updateTE();
/*     */   }
/*     */   
/*     */   public void sendServerUpdateMessage(String target, boolean tab) {
/* 260 */     ModPacketHandler.sendToServer(new UCPackagerServerMessage(this.worldPosition, target, tab));
/*     */   }
/*     */   
/*     */   public int getContainerSize() {
/* 264 */     return 12;
/* 265 */   } public ItemStack getItem(int i) { return (ItemStack)this.inventory.get(i); }
/* 266 */   public void setItem(int i, ItemStack s) { this.inventory.set(i, s); setChanged(); }
/* 267 */   public boolean stillValid(PlayerEntity p) { return (p.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D); }
/* 268 */   public void load(BlockState s, CompoundNBT t) { super.load(s, t); ItemStackHelper.loadAllItems(t, this.inventory); this.coinSum = t.getLong("coinSum"); this.packageSize = t.getInt("size"); this.sendMode = t.getBoolean("mode"); }
/* 269 */   public CompoundNBT save(CompoundNBT t) { super.save(t); ItemStackHelper.saveAllItems(t, this.inventory); t.putLong("coinSum", this.coinSum); t.putInt("size", this.packageSize); t.putBoolean("mode", this.sendMode); return t; }
/* 270 */   public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); }
/* 271 */   public ItemStack removeItem(int i, int c) { return ItemStackHelper.removeItem((List)this.inventory, i, c); }
/* 272 */   public ItemStack removeItemNoUpdate(int i) { return ItemStackHelper.takeItem((List)this.inventory, i); }
/* 273 */   public void clearContent() { this.inventory.clear(); }
/* 274 */   public ITextComponent getDisplayName() { return (ITextComponent)new TranslationTextComponent("block.universalcoins.packager"); } @Nullable
/* 275 */   public Container createMenu(int id, PlayerInventory inv, PlayerEntity p) { return (Container)new ContainerPackager(id, inv, this, this.packagerData, this); }
/*     */ 
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TilePackager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */