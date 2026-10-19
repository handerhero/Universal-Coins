package aspadien.universalcoins.tileentity;
import java.util.List;
import aspadien.universalcoins.util.EmissionThrottle;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerTradeStation;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.init.ModTileEntities;
import aspadien.universalcoins.item.ItemCoin;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCItemPricer;
import aspadien.universalcoins.util.UniversalAccounts;

public class TileTradeStation extends TileProtected implements ISidedInventory, ITickableTileEntity, INamedContainerProvider {
/*  31 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

  public static final int itemInputSlot = 0;
  public static final int itemOutputSlot = 1;
  public static final int itemCardSlot = 2;
  public static final int itemCoinSlot = 3;
/*  37 */   public int coinSum = 0;
/*  38 */   public int itemPrice = 0;
  public boolean buyButtonActive = false;
  public boolean sellButtonActive = false;
  public boolean autoModeButtonActive = true;
  public boolean publicAccess = true;
/*  43 */   public int autoMode = 0;
/*  44 */   public int coinMode = 0;
/*  45 */   private int autoDelayCounter = 0; public boolean ironCoinBtnActive;
  public boolean goldCoinBtnActive;
  public boolean emeraldCoinBtnActive;
  public boolean diamondCoinBtnActive;
  public boolean obsidianCoinBtnActive;

/*  51 */   public final IIntArray tradeData = new IIntArray()
    {
      public int get(int index) {
/*  54 */         switch (index) { case 0:
/*  55 */             return TileTradeStation.this.coinSum;
/*  56 */           case 1: return TileTradeStation.this.itemPrice;
/*  57 */           case 2: return TileTradeStation.this.autoMode;
/*  58 */           case 3: return TileTradeStation.this.coinMode;
/*  59 */           case 4: return TileTradeStation.this.publicAccess ? 1 : 0;
/*  60 */           case 5: return TileTradeStation.this.buyButtonActive ? 1 : 0;
/*  61 */           case 6: return TileTradeStation.this.sellButtonActive ? 1 : 0;
/*  62 */           case 7: return TileTradeStation.this.ironCoinBtnActive ? 1 : 0;
/*  63 */           case 8: return TileTradeStation.this.goldCoinBtnActive ? 1 : 0;
/*  64 */           case 9: return TileTradeStation.this.emeraldCoinBtnActive ? 1 : 0;
/*  65 */           case 10: return TileTradeStation.this.diamondCoinBtnActive ? 1 : 0;
/*  66 */           case 11: return TileTradeStation.this.obsidianCoinBtnActive ? 1 : 0; }
/*  67 */          return 0;
      }


      public void set(int index, int value) {
/*  72 */         switch (index) { case 0:
/*  73 */             TileTradeStation.this.coinSum = value; break;
/*  74 */           case 1: TileTradeStation.this.itemPrice = value; break;
/*  75 */           case 2: TileTradeStation.this.autoMode = value; break;
/*  76 */           case 3: TileTradeStation.this.coinMode = value; break;
/*  77 */           case 4: TileTradeStation.this.publicAccess = (value == 1); break;
/*  78 */           case 5: TileTradeStation.this.buyButtonActive = (value == 1); break;
/*  79 */           case 6: TileTradeStation.this.sellButtonActive = (value == 1); break;
/*  80 */           case 7: TileTradeStation.this.ironCoinBtnActive = (value == 1); break;
/*  81 */           case 8: TileTradeStation.this.goldCoinBtnActive = (value == 1); break;
/*  82 */           case 9: TileTradeStation.this.emeraldCoinBtnActive = (value == 1); break;
/*  83 */           case 10: TileTradeStation.this.diamondCoinBtnActive = (value == 1); break;
/*  84 */           case 11: TileTradeStation.this.obsidianCoinBtnActive = (value == 1);
            break; }

      } public int getCount() {
/*  88 */         return 12;
      }
    };
  public TileTradeStation() {
/*  92 */     super((TileEntityType)ModTileEntities.TRADE_STATION.get());
  }

  private boolean isEnderCard(ItemStack stack) {
/*  96 */     return (!stack.isEmpty() && stack.getItem() == ModItems.ENDER_CARD.get());
  }

  public long getAccountBalance() {
/* 100 */     ItemStack card = getItem(2);
/* 101 */     if (card.hasTag()) {
/* 102 */       return UniversalAccounts.getInstance().getAccountBalance(card.getTag().getString("Account"));
    }
/* 104 */     return 0L;
  }

  private void activateBuySellButtons() {
/* 108 */     ItemStack input = getItem(0);
/* 109 */     if (input.isEmpty()) {
/* 110 */       this.itemPrice = 0;
/* 111 */       this.buyButtonActive = false;
/* 112 */       this.sellButtonActive = false;
    } else {
/* 114 */       this.itemPrice = UCItemPricer.getInstance().getItemPrice(input);
/* 115 */       if (this.itemPrice <= 0) {
/* 116 */         this.itemPrice = 0;
/* 117 */         this.buyButtonActive = false;
/* 118 */         this.sellButtonActive = false;
      } else {
/* 120 */         this.sellButtonActive = (Integer.MAX_VALUE - this.coinSum >= this.itemPrice);


/* 123 */         boolean hasFunds = (this.coinSum >= this.itemPrice || getAccountBalance() >= this.itemPrice);


/* 126 */         boolean canStack = (getItem(1).isEmpty() || (getItem(1).sameItem(input) && getItem(1).getCount() < input.getMaxStackSize()));

/* 128 */         this.buyButtonActive = (hasFunds && canStack);
      }
    }
  }


  public boolean creditAccount(int amount) {
/* 135 */     ItemStack card = getItem(2);
/* 136 */     if (card.isEmpty() || !card.hasTag()) return false;

/* 138 */     String acc = card.getTag().getString("Account");
/* 139 */     return UniversalAccounts.getInstance().creditAccount(acc, amount, false);
  }
/*     */
public void onSellPressed(int amount) {
    if (this.level == null || this.level.isClientSide || amount <= 0)
        return;
    ItemStack input = getItem(0);
    if (input.isEmpty() || input.getCount() < amount)
        return;
    int price = UCItemPricer.getInstance().getItemPrice(input);
    if (price <= 0)
        return;
    if (input.isDamaged()) {
        price = price * (input.getMaxDamage() - input.getDamageValue()) / input.getMaxDamage();
    }

    int coinFromSale = (int)((price * amount) * UniversalCoins.itemSellRatio);
    if (coinFromSale <= 0) coinFromSale = 1;

    ItemStack card = getItem(2);
    if (!card.isEmpty() && card.hasTag()) {
        long referenceBalance = getAccountBalance();          // баланс ДО зачисления
        int throttledAmount = EmissionThrottle.apply(coinFromSale, referenceBalance);
        if (creditAccount(throttledAmount)) {
            input.shrink(amount);
        }
    } else if (this.coinSum + coinFromSale <= 2147483647L) {
        int throttledAmount = EmissionThrottle.apply(coinFromSale, this.coinSum);
        if (this.coinSum + throttledAmount <= 2147483647L) {
            this.coinSum += throttledAmount;
            input.shrink(amount);
        }
    }

    setChanged();
    updateTE();
}

  public void onSellMaxPressed() {
/* 178 */     ItemStack input = getItem(0);
/* 179 */     if (input.isEmpty() || this.itemPrice <= 0)
      return;
/* 181 */     int sellPrice = (int)(this.itemPrice * UniversalCoins.itemSellRatio);
/* 182 */     int amountToSell = input.getCount();


/* 185 */     if (getItem(2).isEmpty()) {
/* 186 */       long spaceInBlock = 2147483647L - this.coinSum;
/* 187 */       int maxByBlock = (int)(spaceInBlock / sellPrice);
/* 188 */       amountToSell = Math.min(amountToSell, maxByBlock);
    }

/* 191 */     if (amountToSell > 0) onSellPressed(amountToSell); 
  }

  private boolean isSameItem(ItemStack stackA, ItemStack stackB) {
/* 195 */     if (stackA.isEmpty() || stackB.isEmpty()) return false;

/* 197 */     return (stackA.getItem() == stackB.getItem() && 
/* 198 */       ItemStack.tagMatches(stackA, stackB) && stackA
/* 199 */       .getDamageValue() == stackB.getDamageValue());
  }

  public boolean debitAccount(int amount) {
/* 203 */     ItemStack card = getItem(2);
/* 204 */     if (card.isEmpty() || !card.hasTag()) return false;

/* 206 */     String acc = card.getTag().getString("Account");

/* 208 */     return UniversalAccounts.getInstance().debitAccount(acc, amount, false);
  }

  public void onBuyPressed(int amount) {
/* 212 */     if (this.level == null || this.level.isClientSide || amount <= 0)
      return;
/* 214 */     ItemStack template = getItem(0);
/* 215 */     if (template.isEmpty())
      return;
/* 217 */     int price = UCItemPricer.getInstance().getItemPrice(template);
/* 218 */     if (price <= 0)
      return;
/* 220 */     long totalCost = price * amount;


/* 223 */     boolean useCard = false;
/* 224 */     long cardBalance = getAccountBalance();

/* 226 */     if (cardBalance >= totalCost) {

/* 228 */       useCard = true;
/* 229 */     } else if (this.coinSum >= totalCost) {

/* 231 */       useCard = false;
    } else {
      return;
    }



/* 238 */     ItemStack output = getItem(1);
/* 239 */     if (!output.isEmpty() && (
/* 240 */       !isSameItem(template, output) || output.getCount() + amount > output.getMaxStackSize())) {
      return;
    }



/* 246 */     if (useCard)
/* 247 */     { if (!debitAccount((int)totalCost))
        return;  }
/* 249 */     else { this.coinSum -= (int)totalCost; }



/* 253 */     ItemStack result = template.copy();
/* 254 */     result.setCount(amount);
/* 255 */     if (output.isEmpty()) {
/* 256 */       this.inventory.set(1, result);
    } else {
/* 258 */       output.grow(amount);
    }

/* 261 */     setChanged();
/* 262 */     updateTE();
  }

  public void onBuyMaxPressed() {
/* 266 */     ItemStack input = getItem(0);
/* 267 */     if (input.isEmpty() || this.itemPrice <= 0) {
      return;
    }
/* 270 */     long cardBal = getAccountBalance();
/* 271 */     long funds = (cardBal >= this.itemPrice) ? cardBal : this.coinSum;

/* 273 */     int maxByMoney = (int)(funds / this.itemPrice);

/* 275 */     ItemStack output = getItem(1);
/* 276 */     int maxBySpace = input.getMaxStackSize();
/* 277 */     if (!output.isEmpty()) {
/* 278 */       if (!isSameItem(input, output))
/* 279 */         return;  maxBySpace = output.getMaxStackSize() - output.getCount();
    }

/* 282 */     int amountToBuy = Math.min(maxByMoney, maxBySpace);
/* 283 */     if (amountToBuy > 0) onBuyPressed(amountToBuy);

  }



  public void tick() {
/* 290 */     if (this.level != null && !this.level.isClientSide) {

/* 292 */       processCoinAbsorption();


/* 295 */       ItemStack input = getItem(0);
/* 296 */       this.itemPrice = input.isEmpty() ? 0 : UCItemPricer.getInstance().getItemPrice(input);

/* 298 */       activateBuySellButtons();
/* 299 */       activateRetrieveButtons();


/* 302 */       if (this.autoMode > 0) {
/* 303 */         this.autoDelayCounter++;
/* 304 */         if (this.autoDelayCounter >= 20) {
/* 305 */           this.autoDelayCounter = 0;
/* 306 */           if (this.autoMode == 1) {
/* 307 */             onBuyMaxPressed();
/* 308 */           } else if (this.autoMode == 2) {
/* 309 */             onSellMaxPressed();
          }
        }
      }


/* 315 */       if (this.coinMode > 0 && this.level.getGameTime() % 10L == 0L) {
/* 316 */         onRetrieveButtonsPressed(2 + this.coinMode - 1, true);
      }


/* 320 */       setChanged();
    }
  }


  private void processCoinAbsorption() {
/* 326 */     int[] slotsToProcess = { 0, 3 };
/* 327 */     for (int slotID : slotsToProcess) {
/* 328 */       ItemStack stack = getItem(slotID);
/* 329 */       int coinValue = CoinUtils.getCoinValue(stack);
/* 330 */       if (coinValue > 0) {
/* 331 */         long totalToAdd = coinValue * stack.getCount();

/* 333 */         if (this.coinSum + totalToAdd < 2147483647L) {
/* 334 */           this.coinSum += (int)totalToAdd;

/* 336 */           setItem(slotID, ItemStack.EMPTY);
/* 337 */           setChanged();
        }
      }
    }
  }

  private void activateRetrieveButtons() {
/* 344 */     this.ironCoinBtnActive = canWithdrawCoin(0);
/* 345 */     this.goldCoinBtnActive = canWithdrawCoin(1);
/* 346 */     this.emeraldCoinBtnActive = canWithdrawCoin(2);
/* 347 */     this.diamondCoinBtnActive = canWithdrawCoin(3);
/* 348 */     this.obsidianCoinBtnActive = canWithdrawCoin(4);
  }

  private boolean canWithdrawCoin(int index) {
/* 352 */     int val = UniversalCoins.coinValues[index];
/* 353 */     ItemStack output = getItem(1);
/* 354 */     ItemCoin itemCoin = ModItems.getCoinByIndex(index);
/* 355 */     return (this.coinSum >= val && (output.isEmpty() || (output.getItem() == itemCoin && output.getCount() < 64)));
  }


  public void onButtonPressed(int buttonId, boolean shiftPressed, int mouseButton) {
/* 360 */     if (this.level == null || this.level.isClientSide)
      return;
/* 362 */     if (buttonId == 0) {
/* 363 */       if (shiftPressed) { onBuyMaxPressed(); } else { onBuyPressed(1); }

/* 365 */     } else if (buttonId == 1) {
/* 366 */       if (shiftPressed) { onSellMaxPressed(); } else { onSellPressed(1); }

/* 368 */     } else if (buttonId == 8) {
/* 369 */       this.autoMode = (this.autoMode + 1) % 3;
    }
/* 371 */     else if (buttonId == 7) {
/* 372 */       if (mouseButton == 1) {
/* 373 */         this.coinMode = (this.coinMode == 0) ? 5 : (this.coinMode - 1);
      } else {
/* 375 */         this.coinMode = (this.coinMode + 1) % 6;
      }

/* 378 */     } else if (buttonId >= 2 && buttonId < 7) {
/* 379 */       onRetrieveButtonsPressed(buttonId, shiftPressed);
    }
/* 381 */     else if (buttonId == 9) {
/* 382 */       this.publicAccess = !this.publicAccess;
    }

/* 385 */     setChanged();
/* 386 */     updateTE();
  }


  public void onRetrieveButtonsPressed(int buttonId, boolean shiftPressed) {
/* 391 */     int index = buttonId - 2;
/* 392 */     if (index < 0 || index > 4)
      return;
/* 394 */     int val = UniversalCoins.coinValues[index];
/* 395 */     if (this.coinSum < val)
      return;
/* 397 */     ItemStack output = getItem(1);
/* 398 */     ItemCoin itemCoin = ModItems.getCoinByIndex(index);

/* 400 */     if (output.isEmpty() || (output.getItem() == itemCoin && output.getCount() < 64)) {
/* 401 */       int count = shiftPressed ? Math.min(this.coinSum / val, 64 - output.getCount()) : 1;
/* 402 */       if (count <= 0)
        return;
/* 404 */       if (output.isEmpty()) {
/* 405 */         this.inventory.set(1, new ItemStack(itemCoin, count));
      } else {
/* 407 */         output.grow(count);
      }
/* 409 */       this.coinSum -= count * val;

/* 411 */       setChanged();
/* 412 */       updateTE();

/* 414 */       this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
    }
  }



  public void setItem(int index, ItemStack stack) {
/* 421 */     this.inventory.set(index, stack);

/* 423 */     if (this.level != null && !this.level.isClientSide) {
/* 424 */       if (index == 0 && !stack.isEmpty())
      {
/* 426 */         this.itemPrice = UCItemPricer.getInstance().getItemPrice(stack);
      }
/* 428 */       setChanged();

/* 430 */       this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
    }
  }


  public int[] getSlotsForFace(Direction side) {
/* 436 */     return new int[] { 0, 1, 3 }; }
/* 437 */   public boolean canPlaceItemThroughFace(int i, ItemStack s, Direction d) { return (i == 0 || (i == 3 && s.getItem() instanceof ItemCoin)); } public boolean canTakeItemThroughFace(int i, ItemStack s, Direction d) {
/* 438 */     return (i == 1);
  }

/* 441 */   public int getContainerSize() { return this.inventory.size(); }
/* 442 */   public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); } public ItemStack getItem(int index) {
/* 443 */     return (ItemStack)this.inventory.get(index);
  } public ItemStack removeItem(int index, int count) {
/* 445 */     ItemStack s = ItemStackHelper.removeItem((List)this.inventory, index, count);
/* 446 */     setChanged(); return s;
  }
/* 448 */   public ItemStack removeItemNoUpdate(int index) { return ItemStackHelper.takeItem(this.inventory, index); }
/* 449 */   public void clearContent() { this.inventory.clear(); } public boolean stillValid(PlayerEntity p) {
/* 450 */     return (p.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D);
  }


  public ITextComponent getDisplayName() {
/* 455 */     return new TranslationTextComponent("block.universalcoins.tradestation");
  } public Container createMenu(int id, PlayerInventory inv, PlayerEntity p) {
/* 457 */     return new ContainerTradeStation(id, inv, this, this.tradeData, this);
  }


  public void load(BlockState state, CompoundNBT nbt) {
/* 462 */     super.load(state, nbt);
/* 463 */     ItemStackHelper.loadAllItems(nbt, this.inventory);
/* 464 */     this.coinSum = nbt.getInt("CoinSum");
/* 465 */     this.autoMode = nbt.getInt("AutoMode");
/* 466 */     this.coinMode = nbt.getInt("CoinMode");
/* 467 */     this.publicAccess = nbt.getBoolean("PublicAccess");
  }


  public CompoundNBT save(CompoundNBT nbt) {
/* 472 */     super.save(nbt);
/* 473 */     ItemStackHelper.saveAllItems(nbt, this.inventory);
/* 474 */     nbt.putInt("CoinSum", this.coinSum);
/* 475 */     nbt.putInt("AutoMode", this.autoMode);
/* 476 */     nbt.putInt("CoinMode", this.coinMode);
/* 477 */     nbt.putBoolean("PublicAccess", this.publicAccess);
/* 478 */     return nbt;
  }
}
