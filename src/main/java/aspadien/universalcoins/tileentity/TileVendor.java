package aspadien.universalcoins.tileentity;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import aspadien.universalcoins.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerVendor;
import aspadien.universalcoins.container.ContainerVendorBuy;
import aspadien.universalcoins.container.ContainerVendorSell;
import aspadien.universalcoins.init.ModBlocks;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCVendorServerMessage;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCItemPricer;
import aspadien.universalcoins.util.UCUtils;

public abstract class TileVendor extends TileProtected implements IInventory, ITickableTileEntity, INamedContainerProvider {
/*  36 */   protected NonNullList<ItemStack> inventory = NonNullList.withSize(17, ItemStack.EMPTY); public static final int itemStorageSlot1 = 0; public static final int itemStorageSlot9 = 8; public static final int itemTradeSlot = 9; public static final int itemCardSlot = 10;
  public static final int itemOutputSlot = 12;
  public static final int itemSellSlot = 11;
  public static final int itemCoinOutputSlot = 13;
  public static final int itemCoinInputSlot = 14;
  public static final int itemUserCoinInputSlot = 15;
  public static final int itemUserCardSlot = 16;
  public static final int idSellButton = 10;
  public static final int idBuyButton = 11;
/*  45 */   public int coinSum = 0; public int userCoinSum = 0; public int itemPrice = 0; public int textColor = 0;

  public boolean infiniteMode = false, sellMode = true, ooStockWarning = false, ooCoinsWarning = false, inventoryFullWarning = false;
  public boolean buyButtonActive = false;
  public boolean sellButtonActive = false;
  public boolean ironCoinBtnActive;
  public boolean goldCoinBtnActive;
  public boolean emeraldCoinBtnActive;
/*  53 */   private BlockPos remoteStoragePos = null; public boolean diamondCoinBtnActive;
  public boolean obsidianCoinBtnActive;
  public boolean uironCoinBtnActive;
  public boolean ugoldCoinBtnActive;
/*  57 */   private static final List<Item> BLACKLIST = Arrays.asList(Items.BEDROCK, Items.BARRIER, Items.COMMAND_BLOCK, Items.STRUCTURE_BLOCK, Items.STRUCTURE_VOID, Items.JIGSAW, Items.SPAWNER, ModItems.IRON_COIN.get(), ModItems.GOLD_COIN.get(), ModItems.EMERALD_COIN.get(), ModItems.DIAMOND_COIN.get(), ModItems.OBSIDIAN_COIN.get(), ModItems.NETHERITE_COIN.get());

  public boolean uemeraldCoinBtnActive;
  public boolean udiamondCoinBtnActive;
  public boolean uobsidianCoinBtnActive;
  private boolean needsShopInit = false;

/*  64 */   public final IIntArray wrenchData = new IIntArray() {
/*  65 */       public int get(int index) { return TileVendor.this.infiniteMode ? 1 : 0; }
/*  66 */       public void set(int index, int value) { TileVendor.this.infiniteMode = (value == 1); } public int getCount() {
/*  67 */         return 1;
      }
    };

/*  71 */   public final IIntArray vendorData = new IIntArray() {
      public int get(int i) {
/*  73 */         switch (i) { case 0:
/*  74 */             return TileVendor.this.coinSum;case 1: return TileVendor.this.userCoinSum;case 2: return TileVendor.this.itemPrice;
/*  75 */           case 3: return TileVendor.this.infiniteMode ? 1 : 0;case 4: return TileVendor.this.sellMode ? 1 : 0;
/*  76 */           case 5: return TileVendor.this.ooStockWarning ? 1 : 0;case 6: return TileVendor.this.ooCoinsWarning ? 1 : 0;
/*  77 */           case 7: return TileVendor.this.inventoryFullWarning ? 1 : 0;case 8: return TileVendor.this.buyButtonActive ? 1 : 0;
/*  78 */           case 9: return TileVendor.this.sellButtonActive ? 1 : 0;
/*  79 */           case 10: return TileVendor.this.ironCoinBtnActive ? 1 : 0;case 11: return TileVendor.this.goldCoinBtnActive ? 1 : 0;
/*  80 */           case 12: return TileVendor.this.emeraldCoinBtnActive ? 1 : 0;case 13: return TileVendor.this.diamondCoinBtnActive ? 1 : 0;
/*  81 */           case 14: return TileVendor.this.obsidianCoinBtnActive ? 1 : 0;
/*  82 */           case 15: return TileVendor.this.uironCoinBtnActive ? 1 : 0;case 16: return TileVendor.this.ugoldCoinBtnActive ? 1 : 0;
/*  83 */           case 17: return TileVendor.this.uemeraldCoinBtnActive ? 1 : 0;case 18: return TileVendor.this.udiamondCoinBtnActive ? 1 : 0;
/*  84 */           case 19: return TileVendor.this.uobsidianCoinBtnActive ? 1 : 0; }
/*  85 */          return 0;
      }

      public void set(int i, int v) {
/*  89 */         switch (i) { case 0:
/*  90 */             TileVendor.this.coinSum = v; break;case 1: TileVendor.this.userCoinSum = v; break;case 2: TileVendor.this.itemPrice = v; break;
/*  91 */           case 3: TileVendor.this.infiniteMode = (v == 1); break;case 4: TileVendor.this.sellMode = (v == 1);
            break; }
         } public int getCount() {
/*  94 */         return 20;
      }
    }; public TileVendor(TileEntityType<?> type) {
/*  97 */     super(type);
  }

  public void tick() {
/* 101 */     if (this.level == null || this.level.isClientSide)
      return;
/* 103 */     if (this.needsShopInit && 
/* 104 */       !this.level.hasNeighborSignal(this.worldPosition)) {
/* 105 */       this.needsShopInit = false;
/* 106 */       randomizeShop();
/* 107 */       setChanged();
    }



/* 112 */     if (this.inUse && this.level.getGameTime() % 40L == 0L)
    {
/* 114 */       if (this.level.getEntitiesOfClass(PlayerEntity.class, (new AxisAlignedBB(this.worldPosition)).inflate(8.0D)).isEmpty()) {
/* 115 */         this.inUse = false;
      }
    }

/* 119 */     absorbCoins();

/* 121 */     ItemStack tradeItem = getItem(9);
/* 122 */     if (!tradeItem.isEmpty() && this.itemPrice <= 0) {
/* 123 */       this.itemPrice = UCItemPricer.getInstance().getItemPrice(tradeItem);
    }

/* 126 */     updateLogic();

/* 128 */     if (this.ooStockWarning && this.remoteStoragePos != null) checkRemoteStorage();

/* 130 */     if (this.level.getGameTime() % 10L == 0L) setChanged();

  }


  private void absorbCoins() {
/* 136 */     absorbFromSlot(14, true);
/* 137 */     absorbFromSlot(15, false);
/* 138 */     setChanged();
/* 139 */     updateTE();
  }

  private void absorbFromSlot(int slot, boolean isOwner) {
/* 143 */     ItemStack stack = (ItemStack)this.inventory.get(slot);
/* 144 */     int val = CoinUtils.getCoinValue(stack);
/* 145 */     if (val > 0) {
/* 146 */       long total = val * stack.getCount();
/* 147 */       if (isOwner) { this.coinSum = (int)(this.coinSum + total); }
/* 148 */       else { this.userCoinSum = (int)(this.userCoinSum + total); }

/* 150 */       this.inventory.set(slot, ItemStack.EMPTY);
    }
  }

  private void updateLogic() {
/* 155 */     ItemStack tradeItem = getItem(9);
/* 156 */     if (tradeItem.isEmpty()) {
/* 157 */       this.buyButtonActive = this.sellButtonActive = false; this.ooStockWarning = false;
      return;
    }
/* 160 */     long uFunds = UCUtils.getCardBalance(getItem(16)) + this.userCoinSum;
/* 161 */     if (uFunds < 0L) uFunds = this.userCoinSum; 
/* 162 */     this.ooStockWarning = (!this.infiniteMode && !UCUtils.hasStock(this.inventory, tradeItem, tradeItem.getCount()));
/* 163 */     this.buyButtonActive = (!this.ooStockWarning && uFunds >= this.itemPrice && this.itemPrice > 0);

/* 165 */     long oFunds = UCUtils.getCardBalance(getItem(10)) + this.coinSum;
/* 166 */     if (oFunds < 0L) oFunds = this.coinSum; 
/* 167 */     this.sellButtonActive = (UCUtils.isSameItem(getItem(11), tradeItem) && oFunds >= this.itemPrice && (this.infiniteMode || hasInventorySpace()));

/* 169 */     updateCoinButtons();
  }

  private void updateCoinButtons() {
/* 173 */     this.ironCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[0]);
/* 174 */     this.goldCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[1]);
/* 175 */     this.emeraldCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[2]);
/* 176 */     this.diamondCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[3]);
/* 177 */     this.obsidianCoinBtnActive = (this.coinSum >= UniversalCoins.coinValues[4]);

/* 179 */     this.uironCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[0]);
/* 180 */     this.ugoldCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[1]);
/* 181 */     this.uemeraldCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[2]);
/* 182 */     this.udiamondCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[3]);
/* 183 */     this.uobsidianCoinBtnActive = (this.userCoinSum >= UniversalCoins.coinValues[4]);
  }




  public void onButtonPressed(int id, boolean shift, int mouseButton) {
/* 190 */     if (this.level == null || this.level.isClientSide)
      return;
/* 192 */     if (id == 0) { this.sellMode = !this.sellMode; }
/* 193 */     else if (id == 2) { sendServerUpdateMessage(); }
/* 194 */     else if (id == 8) { if (this.textColor > 0) this.textColor--;  }
/* 195 */     else if (id == 9) { if (this.textColor < 15) this.textColor++;
       }
/* 197 */     else if (id == 10) { onSellPressed(shift ? getItem(11).getCount() : 1); }
/* 198 */     else if (id == 11) { if (shift) { onBuyMaxPressed(); } else { onBuyPressed(1); }
       }
/* 200 */     else if (isCoinButton(id)) { onRetrieveButtonsPressed(id, shift); }

/* 202 */     setChanged();
/* 203 */     updateTE();
  }
  private boolean isCoinButton(int id) {
/* 206 */     return ((id >= 3 && id <= 7) || (id >= 12 && id <= 16));
  }
  public void onRetrieveButtonsPressed(int id, boolean shift) {
/* 209 */     int idx = (id >= 12) ? (id - 12) : (id - 3);
/* 210 */     if (id < 10) { this.coinSum = (int)UCUtils.withdrawCoins(this.coinSum, this, 13, idx, shift); }
/* 211 */     else { this.userCoinSum = (int)UCUtils.withdrawCoins(this.userCoinSum, this, 13, idx, shift); }

  }


  public void onBuyPressed(int amount) {
/* 217 */     if (this.level == null || this.level.isClientSide || amount <= 0)
      return;
/* 219 */     ItemStack tradeItem = getItem(9);
/* 220 */     if (tradeItem.isEmpty() || this.itemPrice <= 0)
      return;
/* 222 */     int totalItemsToGive = tradeItem.getCount() * amount;
/* 223 */     int totalCost = this.itemPrice * amount;


/* 226 */     if (!this.infiniteMode && !UCUtils.hasStock(this.inventory, tradeItem, totalItemsToGive)) {
/* 227 */       this.ooStockWarning = true;
/* 228 */       setChanged();

      return;
    }

/* 233 */     ItemStack outputSlotStack = (ItemStack)this.inventory.get(12);
/* 234 */     if (!outputSlotStack.isEmpty() && (
/* 235 */       !UCUtils.isSameItem(outputSlotStack, tradeItem) || outputSlotStack
/* 236 */       .getCount() + totalItemsToGive > tradeItem.getMaxStackSize())) {
      return;
    }



/* 242 */     long cardBalance = UCUtils.getCardBalance(getItem(16));
/* 243 */     if (cardBalance < 0L) cardBalance = 0L; 
/* 244 */     long totalUserFunds = cardBalance + this.userCoinSum;

/* 246 */     if (totalUserFunds >= totalCost) {
/* 247 */       long remainingToPay = totalCost;


/* 250 */       if (this.userCoinSum > 0) {
/* 251 */         int takeFromCash = (int)Math.min(this.userCoinSum, remainingToPay);
/* 252 */         this.userCoinSum -= takeFromCash;
/* 253 */         remainingToPay -= takeFromCash;
      }


/* 257 */       if (remainingToPay > 0L) {
/* 258 */         UCUtils.debitCard(getItem(16), remainingToPay);
      }


/* 262 */       if (!UCUtils.creditCard(getItem(10), totalCost)) {
/* 263 */         this.coinSum += totalCost;
      }


/* 267 */       if (!this.infiniteMode) {
/* 268 */         UCUtils.decreaseStock(this.inventory, tradeItem, totalItemsToGive);
      }


/* 272 */       if (((ItemStack)this.inventory.get(12)).isEmpty()) {
/* 273 */         ItemStack result = tradeItem.copy();
/* 274 */         result.setCount(totalItemsToGive);
/* 275 */         this.inventory.set(12, result);
      } else {
/* 277 */         ((ItemStack)this.inventory.get(12)).grow(totalItemsToGive);
      }

/* 280 */       setChanged();
/* 281 */       updateTE();
/* 282 */       this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
/* 283 */       this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }
  }

  public void onSellPressed(int amount) {
/* 288 */     if (this.level == null || this.level.isClientSide || amount <= 0)
      return;
/* 290 */     ItemStack sample = getItem(9);
/* 291 */     ItemStack incoming = getItem(11);


/* 294 */     if (incoming.isEmpty() || !UCUtils.isSameItem(incoming, sample))
/* 295 */       return;  if (incoming.getCount() < amount)
      return;
/* 297 */     int totalPay = this.itemPrice * amount;


/* 300 */     boolean ownerPaid = false;
/* 301 */     long ownerBal = UCUtils.getCardBalance(getItem(10));
/* 302 */     if (ownerBal >= totalPay) {
/* 303 */       ownerPaid = UCUtils.debitCard(getItem(10), totalPay);
/* 304 */     } else if (this.coinSum >= totalPay) {
/* 305 */       this.coinSum -= totalPay;
/* 306 */       ownerPaid = true;
    }

/* 309 */     if (ownerPaid) {

/* 311 */       if (!this.infiniteMode && !hasInventorySpace()) {
        return;
      }
/* 314 */       if (!UCUtils.creditCard(getItem(16), totalPay)) {
/* 315 */         this.userCoinSum += totalPay;
      }


/* 319 */       if (!this.infiniteMode) {
/* 320 */         ItemStack toStore = incoming.copy();
/* 321 */         toStore.setCount(amount);
/* 322 */         UCUtils.storeItems(this.inventory, toStore);
      }

/* 325 */       incoming.shrink(amount);
/* 326 */       if (incoming.isEmpty()) this.inventory.set(11, ItemStack.EMPTY);

/* 328 */       setChanged();
/* 329 */       updateTE();
/* 330 */       this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }
  }

  public void onBuyMaxPressed() {
/* 335 */     ItemStack tradeItem = getItem(9);
/* 336 */     if (tradeItem.isEmpty() || this.itemPrice <= 0)
/* 337 */       return;  long uFunds = UCUtils.getCardBalance(getItem(16)) + this.userCoinSum;
/* 338 */     if (uFunds < 0L) uFunds = this.userCoinSum; 
/* 339 */     int amount = (int)(uFunds / this.itemPrice);

/* 341 */     if (!this.infiniteMode) {
/* 342 */       int found = 0;
/* 343 */       for (int i = 0; i <= 8; ) { if (UCUtils.isSameItem(getItem(i), tradeItem)) found += getItem(i).getCount();  i++; }
/* 344 */        amount = Math.min(amount, found / tradeItem.getCount());
    }
/* 346 */     amount = Math.min(amount, 64 / tradeItem.getCount());
/* 347 */     if (amount > 0) onBuyPressed(amount); 
  }

  public void onSellMaxPressed() {
/* 351 */     ItemStack userStack = getItem(11);
/* 352 */     if (userStack.isEmpty() || this.itemPrice <= 0)
      return;
/* 354 */     long oFunds = UCUtils.getCardBalance(getItem(10)) + this.coinSum;
/* 355 */     if (oFunds < 0L) oFunds = this.coinSum; 
/* 356 */     int maxByMoney = (int)(oFunds / this.itemPrice);
/* 357 */     int amount = Math.min(userStack.getCount(), maxByMoney);

/* 359 */     if (amount > 0) onSellPressed(amount);

  }



  public void setGhostSlot(ItemStack s) {
/* 366 */     if (!s.isEmpty()) {
/* 367 */       ItemStack g = s.copy(); g.setCount(1); this.inventory.set(9, g);
/* 368 */       this.itemPrice = UCItemPricer.getInstance().getItemPrice(g);
    } else {
/* 370 */       this.inventory.set(9, ItemStack.EMPTY);
/* 371 */       this.itemPrice = 0;
    }
/* 373 */     setChanged(); updateTE();
/* 374 */     if (this.level != null) this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);

  }

  public void setRemoteStorage(int[] coords) {
/* 379 */     this.remoteStoragePos = new BlockPos(coords[0], coords[1], coords[2]);
/* 380 */     setChanged();
  }

  public void checkRemoteStorage() {
/* 384 */     if (this.remoteStoragePos == null || !this.level.isLoaded(this.remoteStoragePos))
/* 385 */       return;  TileEntity te = this.level.getBlockEntity(this.remoteStoragePos);
/* 386 */     if (te instanceof IInventory) {
/* 387 */       IInventory chest = (IInventory)te;
/* 388 */       ItemStack tradeItem = getItem(9);
/* 389 */       for (int i = 0; i < chest.getContainerSize(); i++) {
/* 390 */         if (UCUtils.isSameItem(chest.getItem(i), tradeItem)) {
/* 391 */           UCUtils.storeItems(this.inventory, chest.getItem(i));
/* 392 */           chest.setItem(i, ItemStack.EMPTY);
/* 393 */           setChanged();
          return;
        }
      }
    }
  }
  public boolean hasInventorySpace() {
/* 400 */     ItemStack tradeItem = getItem(9);
/* 401 */     for (int i = 0; i <= 8; i++) {
/* 402 */       ItemStack s = (ItemStack)this.inventory.get(i);
/* 403 */       if (s.isEmpty() || (UCUtils.isSameItem(s, tradeItem) && s.getCount() < s.getMaxStackSize())) return true; 
    }
/* 405 */     return false;
  }

  public int getContainerSize() {
/* 409 */     return 17;
/* 410 */   } public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); } public ItemStack getItem(int i) {
/* 411 */     return (ItemStack)this.inventory.get(i);
  }
  public void setItem(int i, ItemStack s) {
/* 414 */     if (i == 9)
/* 415 */       return;  this.inventory.set(i, s);
/* 416 */     setChanged();
  }

/* 419 */   public ItemStack removeItem(int i, int c) { ItemStack s = ItemStackHelper.removeItem((List)this.inventory, i, c); setChanged(); return s; }
/* 420 */   public ItemStack removeItemNoUpdate(int i) { return ItemStackHelper.takeItem((List)this.inventory, i); }
/* 421 */   public void clearContent() { this.inventory.clear(); setChanged(); } public boolean stillValid(PlayerEntity p) {
/* 422 */     return (p.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D);
  }
  public boolean canPlaceItem(int i, ItemStack s) {
/* 425 */     if (i == 11) {
/* 426 */       ItemStack sample = getItem(9);


/* 429 */       return (!sample.isEmpty() && (s.isEmpty() || UCUtils.isSameItem(s, sample)));
    }
/* 431 */     if (i == 9) return false; 
/* 432 */     if (i == 14 || i == 15) return (CoinUtils.getCoinValue(s) > 0); 
/* 433 */     return (i <= 8 || i == 12 || i == 10 || i == 16);
  }


  public void load(BlockState s, CompoundNBT t) {
/* 438 */     super.load(s, t);
/* 439 */     this.inventory = NonNullList.withSize(17, ItemStack.EMPTY);
/* 440 */     ItemStackHelper.loadAllItems(t, this.inventory);
/* 441 */     this.coinSum = t.getInt("CoinSum"); this.userCoinSum = t.getInt("UserSum"); this.itemPrice = t.getInt("Price");
/* 442 */     this.infiniteMode = t.getBoolean("Inf"); this.sellMode = t.getBoolean("Mode");
/* 443 */     if (t.contains("RX")) this.remoteStoragePos = new BlockPos(t.getInt("RX"), t.getInt("RY"), t.getInt("RZ")); 
/* 444 */     if (t.contains("NeedsShopInit")) {
/* 445 */       this.needsShopInit = t.getBoolean("NeedsShopInit");
    }
  }

  public CompoundNBT save(CompoundNBT t) {
    super.save(t);
    ItemStackHelper.saveAllItems(t, this.inventory);
    t.putInt("CoinSum", this.coinSum); t.putInt("UserSum", this.userCoinSum); t.putInt("Price", this.itemPrice);
    t.putBoolean("Inf", this.infiniteMode); t.putBoolean("Mode", this.sellMode);
    if (this.remoteStoragePos != null) { t.putInt("RX", this.remoteStoragePos.getX()); t.putInt("RY", this.remoteStoragePos.getY()); t.putInt("RZ", this.remoteStoragePos.getZ()); }
     if (this.needsShopInit) {
      t.putBoolean("NeedsShopInit", true);
    }
    return t;
  }
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(ModBlocks.VENDOR.get().getDescriptionId());
  }
  public void sendServerUpdateMessage() {
    if (this.level != null && this.level.isClientSide)
      ModPacketHandler.sendToServer(new UCVendorServerMessage(this.worldPosition, this.itemPrice, this.blockOwner, this.infiniteMode));
  }

  @Nullable
  public Container createMenu(int id, PlayerInventory inv, PlayerEntity p) {
    this.inUse = true;

    boolean isOwner = p.getUUID().toString().equals(this.blockOwner);
    if (isOwner) {
      return new ContainerVendor(id, inv, this, this.vendorData, this);
    }
    return this.sellMode ? new ContainerVendorBuy(id, inv, this, this.vendorData, this) : new ContainerVendorSell(id, inv, this, this.vendorData, this);
  }





  public void inUseCleanup() {
    this.inUse = false;
    setChanged();

    if (this.level != null && !this.level.isClientSide) {
      this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
    }
  }


  private void randomizeShop() {
    this.infiniteMode = true;
    this.sellMode = true;

      assert this.level != null;
      Random random = this.level.getRandom();

    int max = Math.max(UniversalCoins.shopMaxPrice, UniversalCoins.shopMinPrice + 1);
    int priceModifier = random.nextInt(max - UniversalCoins.shopMinPrice) + UniversalCoins.shopMinPrice;

    ItemStack stack = ItemStack.EMPTY;
    for (int i = 0; i < 500; i++) {
        ItemStack candidate = UCItemPricer.getInstance().getRandomPricedStack();
        if (candidate != null && !candidate.isEmpty() && !BLACKLIST.contains(candidate.getItem())) {
            stack = candidate;
            break;
        }
    }

    // Проверяем, нашли ли мы предмет
    if (stack != null && !stack.isEmpty()) {
        ItemStack ghostStack = stack.copy();
        ghostStack.setCount(1);
        this.inventory.set(9, ghostStack);

        int basePrice = UCItemPricer.getInstance().getItemPrice(stack);
        this.itemPrice = Math.max(1, basePrice * priceModifier / 100);
    } else {
        // ЕСЛИ ПРЕДМЕТ НЕ НАЙДЕН — ВЫВОДИМ ОШИБКУ В ЧАТ
        if (this.level != null && !this.level.isClientSide && this.level.getServer() != null) {
            String errorText = "[UniversalCoins] Ошибка: Магазин на координатах X:" + this.worldPosition.getX() +
                    " Y:" + this.worldPosition.getY() +
                    " Z:" + this.worldPosition.getZ() +
                    " не смог найти предмет для продажи (в списке только монеты?)";

            this.level.getServer().getPlayerList().broadcastMessage(
                    new net.minecraft.util.text.StringTextComponent(errorText),
                    net.minecraft.util.text.ChatType.SYSTEM,
                    net.minecraft.util.Util.NIL_UUID
            );
        }
    }
    updateTE();
    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
    this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
  }
}
