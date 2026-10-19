/*     */ package aspadien.universalcoins.tileentity;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.ItemStackHelper;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.INamedContainerProvider;
/*     */ import net.minecraft.item.Item;
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
/*     */ import aspadien.universalcoins.container.ContainerATM;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.net.ATMWithdrawalMessage;
/*     */ import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UniversalAccounts;
/*     */ 
/*     */ public class TileATM extends TileProtected implements IInventory, INamedContainerProvider, ITickableTileEntity {
/*  30 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
/*     */   public static final int itemCoinSlot = 0;
/*     */   public static final int itemCardSlot = 1;
/*  33 */   public String playerUID = "";
/*  34 */   public String cardOwner = "";
/*     */   public boolean depositCoins = false;
/*     */   public boolean withdrawCoins = false;
/*     */   public boolean accountError = false;
/*  38 */   public int coinWithdrawalAmount = 0;
/*  39 */   public String accountNumber = "none";
/*  40 */   public long accountBalance = 0L;
/*     */   
/*  42 */   private final IIntArray atmData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  45 */         switch (index) { case 0:
/*  46 */             return TileATM.this.inUse ? 1 : 0;
/*  47 */           case 1: return TileATM.this.depositCoins ? 1 : 0;
/*  48 */           case 2: return TileATM.this.withdrawCoins ? 1 : 0;
/*  49 */           case 3: return TileATM.this.coinWithdrawalAmount;
/*  50 */           case 4: return (int)(TileATM.this.accountBalance & 0xFFFFFFFFL);
/*  51 */           case 5: return (int)(TileATM.this.accountBalance >> 32L);
/*  52 */           case 6: return TileATM.this.accountError ? 1 : 0;
/*  53 */           case 7: return (TileATM.this.playerUID != null && TileATM.this.playerUID.equals(TileATM.this.cardOwner)) ? 1 : 0; }
/*  54 */          return 0;
/*     */       }
/*     */ 
/*     */       
/*     */       public void set(int index, int value) {
/*  59 */         switch (index) { case 0:
/*  60 */             TileATM.this.inUse = (value == 1); break;
/*  61 */           case 1: TileATM.this.depositCoins = (value == 1); break;
/*  62 */           case 2: TileATM.this.withdrawCoins = (value == 1); break;
/*  63 */           case 3: TileATM.this.coinWithdrawalAmount = value; break;
/*  64 */           case 4: TileATM.this.accountBalance = TileATM.this.accountBalance & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; break;
/*  65 */           case 5: TileATM.this.accountBalance = TileATM.this.accountBalance & 0xFFFFFFFFL | value << 32L; break;
/*  66 */           case 6: TileATM.this.accountError = (value == 1);
/*     */             break; }
/*     */       
/*     */       } public int getCount() {
/*  70 */         return 8;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public void tick() {
/*  76 */     if (this.level == null || this.level.isClientSide)
/*  77 */       return;  if (this.inUse) {
/*     */       
/*  79 */       if (this.depositCoins && !getItem(0).isEmpty()) {
/*  80 */         processDeposit();
/*     */       }
/*     */ 
/*     */       
/*  84 */       if (this.withdrawCoins && this.coinWithdrawalAmount > 0 && 
/*  85 */         getItem(0).isEmpty()) {
/*  86 */         fillCoinSlot();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  91 */       if (this.level.getGameTime() % 10L == 0L && !this.accountNumber.equals("none")) {
/*  92 */         long realBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/*  93 */         if (this.accountBalance != realBalance) {
/*  94 */           this.accountBalance = realBalance;
/*  95 */           setChanged();
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processDeposit() {
/* 102 */     ItemStack stack = getItem(0);
/* 103 */     int coinValue = CoinUtils.getCoinValue(stack);
/* 104 */     if (coinValue > 0 && !this.accountNumber.equals("none")) {
/* 105 */       long space = Long.MAX_VALUE - this.accountBalance;
/* 106 */       int depositAmount = (int)Math.min(stack.getCount(), space / coinValue);
/*     */       
/* 108 */       if (depositAmount > 0) {
/*     */         
/* 110 */         UniversalAccounts.getInstance().creditAccount(this.accountNumber, (long) depositAmount * coinValue, false);
/* 111 */         this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/*     */ 
/*     */         
/* 114 */         stack.shrink(depositAmount);
/* 115 */         if (stack.isEmpty()) {
/* 116 */           this.inventory.set(0, ItemStack.EMPTY);
/*     */         }
/*     */ 
/*     */
/* 120 */         setChanged();
/* 121 */         updateTE();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setItem(int index, ItemStack stack) {
/* 129 */     this.inventory.set(index, stack);
/* 130 */     if (this.level == null || this.level.isClientSide)
/* 131 */       return;  if (index == 1) {
/* 132 */       if (!stack.isEmpty() && stack.hasTag()) {
/* 133 */
                  assert stack.getTag() != null;
                  this.accountNumber = stack.getTag().getString("Account");
/* 134 */         this.cardOwner = stack.getTag().getString("Owner");
/* 135 */         this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/*     */       } else {
/*     */         
/* 138 */         String personalAcc = UniversalAccounts.getInstance().getPlayerAccount(this.playerUID);
/* 139 */         if (!personalAcc.isEmpty()) {
/* 140 */           this.accountNumber = personalAcc;
/* 141 */           this.cardOwner = this.playerUID;
/* 142 */           this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/*     */         } else {
/* 144 */           this.accountNumber = "none";
/* 145 */           this.cardOwner = "";
/* 146 */           this.accountBalance = 0L;
/*     */         } 
/* 148 */         this.coinWithdrawalAmount = 0;
/*     */       } 
/* 150 */       setChanged();
/* 151 */       updateTE();
/*     */     } 
/*     */   }
/*     */   
/*     */   public TileATM() {
/* 156 */     super(ModTileEntities.ATM.get());
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendServerUpdatePacket(int withdrawalAmount) {
/* 161 */     ModPacketHandler.sendToServer(new ATMWithdrawalMessage(this.worldPosition, withdrawalAmount));
/*     */   }
/*     */   
/*     */   public void startCoinWithdrawal(int amount) {
/* 165 */     if (this.level == null || this.level.isClientSide)
/* 166 */       return;  this.accountError = false;
/*     */     
/* 168 */     if (UniversalAccounts.getInstance().debitAccount(this.accountNumber, amount, true)) {
/* 169 */       this.coinWithdrawalAmount = amount;
/* 170 */       fillCoinSlot();
/*     */     } else {
/* 172 */       this.accountError = true;
/*     */     } 
/* 174 */     setChanged();
/*     */   }
/*     */ 
/*     */   
/*     */   public void inUseCleanup() {
/* 179 */     if (this.level == null || this.level.isClientSide)
/* 180 */       return;  this.inUse = false;
/* 181 */     this.withdrawCoins = false;
/* 182 */     this.depositCoins = false;
/* 183 */     this.coinWithdrawalAmount = 0;
/* 184 */     this.accountNumber = "none";
/* 185 */     this.accountBalance = 0L;
/* 186 */     updateTE();
/*     */   }
/*     */   
/*     */   public int getContainerSize() {
/* 190 */     return 2;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 194 */     for (ItemStack stack : this.inventory) { if (!stack.isEmpty()) return false;  }
/* 195 */      return true;
/*     */   }
/*     */   
/*     */   public ItemStack getItem(int index) {
/* 199 */     return this.inventory.get(index);
/*     */   }
/*     */   
/*     */   public ItemStack removeItem(int index, int count) {
/* 203 */     ItemStack stack = ItemStackHelper.removeItem(this.inventory, index, count);
/* 204 */     if (!stack.isEmpty()) {
/* 205 */       setChanged();
/*     */     }
/* 207 */     return stack;
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack removeItemNoUpdate(int index) {
/* 212 */     return ItemStackHelper.takeItem(this.inventory, index);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/* 217 */     return (player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public void load(BlockState state, CompoundNBT tag) {
/* 222 */     super.load(state, tag);
/* 223 */     this.cardOwner = tag.getString("cardOwner");
/* 224 */     this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
/* 225 */     ItemStackHelper.loadAllItems(tag, this.inventory);
/* 226 */     this.playerUID = tag.getString("playerUID");
/* 227 */     this.depositCoins = tag.getBoolean("depositCoins");
/* 228 */     this.withdrawCoins = tag.getBoolean("withdrawCoins");
/* 229 */     this.coinWithdrawalAmount = tag.getInt("coinWithdrawalAmount");
/* 230 */     this.accountNumber = tag.getString("accountNumber");
/* 231 */     this.accountBalance = tag.getLong("accountBalance");
/* 232 */     this.accountError = tag.getBoolean("accountError");
/*     */   }
/*     */ 
/*     */   
/*     */   public CompoundNBT save(CompoundNBT tag) {
/* 237 */     super.save(tag);
/* 238 */     ItemStackHelper.saveAllItems(tag, this.inventory);
/* 239 */     tag.putString("cardOwner", this.cardOwner);
/* 240 */     tag.putString("playerUID", this.playerUID);
/* 241 */     tag.putBoolean("depositCoins", this.depositCoins);
/* 242 */     tag.putBoolean("withdrawCoins", this.withdrawCoins);
/* 243 */     tag.putInt("coinWithdrawalAmount", this.coinWithdrawalAmount);
/* 244 */     tag.putString("accountNumber", this.accountNumber);
/* 245 */     tag.putLong("accountBalance", this.accountBalance);
/* 246 */     tag.putBoolean("accountError", this.accountError);
/* 247 */     return tag; } public void onButtonPressed(int functionId) { long currentBal; String oldAccount, storedAcc; ItemStack currentStack;
/*     */     Item targetCardItem;
/*     */     ItemStack newCard;
/*     */     CompoundNBT nbt;
/* 251 */     if (this.level == null || this.level.isClientSide)
/* 252 */       return;  UniversalAccounts accounts = UniversalAccounts.getInstance();
/* 253 */     this.accountError = false;
/* 254 */     switch (functionId) {
/*     */       case 1:
/* 256 */         this.accountNumber = accounts.getOrCreatePlayerAccount(this.playerUID);
/* 257 */         currentBal = accounts.getAccountBalance(this.accountNumber);
/* 258 */         currentStack = getItem(1);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 263 */         targetCardItem = ModItems.UC_CARD.get();
/* 264 */         if (!currentStack.isEmpty() && currentStack.getItem() == ModItems.ENDER_CARD.get()) {
/* 265 */           targetCardItem = ModItems.ENDER_CARD.get();
/*     */         }
/*     */         
/* 268 */         newCard = new ItemStack((IItemProvider)targetCardItem);
/* 269 */         nbt = newCard.getOrCreateTag();
/* 270 */         nbt.putString("Name", this.playerName);
/* 271 */         nbt.putString("Owner", this.playerUID);
/* 272 */         nbt.putString("Account", this.accountNumber);
/*     */         
/* 274 */         this.inventory.set(1, newCard);
/* 275 */         this.accountBalance = currentBal;
/* 276 */         this.cardOwner = this.playerUID;
/*     */         break;
/*     */ 
/*     */       
/*     */       case 2:
/* 281 */         oldAccount = accounts.getPlayerAccount(this.playerUID);
/* 282 */         if (!oldAccount.isEmpty()) {
/* 283 */           accounts.transferPlayerAccount(this.playerUID);
/* 284 */           String newAccountNumber = accounts.getPlayerAccount(this.playerUID);
/* 285 */           long l = accounts.getAccountBalance(newAccountNumber);
/*     */           
/* 287 */           ItemStack itemStack1 = getItem(1);
/*     */ 
/*     */           
/* 290 */           Item item = ModItems.UC_CARD.get();
/* 291 */           if (!itemStack1.isEmpty() && itemStack1.getItem() == ModItems.ENDER_CARD.get()) {
/* 292 */             item = ModItems.ENDER_CARD.get();
/*     */           }
/*     */           
/* 295 */           ItemStack transferCard = new ItemStack((IItemProvider)item);
/* 296 */           CompoundNBT tNbt = transferCard.getOrCreateTag();
/* 297 */           tNbt.putString("Name", this.playerName);
/* 298 */           tNbt.putString("Owner", this.playerUID);
/* 299 */           tNbt.putString("Account", newAccountNumber);
/*     */           
/* 301 */           this.inventory.set(1, transferCard);
/* 302 */           this.accountNumber = newAccountNumber;
/* 303 */           this.accountBalance = l;
/* 304 */           this.cardOwner = this.playerUID;
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case 3:
/* 310 */         this.depositCoins = true;
/* 311 */         this.withdrawCoins = false;
/*     */         
/* 313 */         if (this.accountNumber.equals("none") && !getItem(1).isEmpty()) {
/* 314 */           this.accountNumber = getItem(1).getTag().getString("Account");
/*     */         }
/*     */         break;
/*     */       
/*     */       case 4:
/* 319 */         this.withdrawCoins = true;
/* 320 */         this.depositCoins = false;
/* 321 */         fillCoinSlot();
/*     */         break;
/*     */       case 5:
/* 324 */         storedAcc = accounts.getPlayerAccount(this.playerUID);
/* 325 */         if (!storedAcc.isEmpty()) {
/* 326 */           this.accountNumber = storedAcc;
/* 327 */           this.accountBalance = accounts.getAccountBalance(this.accountNumber);
/* 328 */           this.cardOwner = this.playerUID;
/*     */         } 
/*     */         break;
/*     */ 
/*     */       
/*     */       case 6:
/* 334 */         this.inventory.set(1, ItemStack.EMPTY);
/* 335 */         inUseCleanup();
/*     */         break;
/*     */     } 
/*     */
/* 342 */     updateTE();
/* 343 */     setChanged(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void fillCoinSlot() {
/* 348 */     if (this.level == null || this.level.isClientSide || this.accountNumber.equals("none")) return;
                if (getItem(0).isEmpty() && this.coinWithdrawalAmount > 0) {
/* 350 */       for (int i = UniversalCoins.coinValues.length - 1; i >= 0; i--) {
/* 351 */         int coinVal = UniversalCoins.coinValues[i];
/*     */         
/* 353 */         if (this.coinWithdrawalAmount >= coinVal) {
/* 354 */           int count = Math.min(this.coinWithdrawalAmount / coinVal, 64);
/* 355 */           int totalToDebit = count * coinVal;
/*     */ 
/*     */           
/* 358 */           if (UniversalAccounts.getInstance().debitAccount(this.accountNumber, totalToDebit, false)) {
/* 359 */             ItemStack payoutStack = new ItemStack(ModItems.getCoinByIndex(i), count);
/*     */             
/* 361 */             this.inventory.set(0, payoutStack);
/*     */             
/* 363 */             this.coinWithdrawalAmount -= totalToDebit;
/* 364 */             this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/*     */ 
/*     */             
/* 367 */             setChanged();
/* 368 */             updateTE();
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 375 */     if (this.coinWithdrawalAmount <= 0) {
/* 376 */       this.withdrawCoins = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearContent() {
/* 382 */     this.inventory.clear();
/*     */   }
/*     */   
/*     */   public ITextComponent getDisplayName() {
/* 386 */     return new TranslationTextComponent("block.universalcoins.atm");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
/* 393 */     refreshDataOnOpen();
/* 394 */     return new ContainerATM(windowId, playerInv, this, this.atmData, this);
/*     */   }
/*     */   
/*     */   private void refreshDataOnOpen() {
/* 398 */     ItemStack card = getItem(1);
/* 399 */     if (!card.isEmpty() && card.hasTag()) {
/*     */       
/* 401 */       this.accountNumber = card.getTag().getString("Account");
/* 402 */       this.cardOwner = card.getTag().getString("Owner");
/*     */     } else {
/*     */       
/* 405 */       String personalAcc = UniversalAccounts.getInstance().getPlayerAccount(this.playerUID);
/* 406 */       if (!personalAcc.isEmpty()) {
/* 407 */         this.accountNumber = personalAcc;
/* 408 */         this.cardOwner = this.playerUID;
/*     */       } else {
/* 410 */         this.accountNumber = "none";
/* 411 */         this.cardOwner = "";
/*     */       } 
/*     */     } 
/* 414 */     this.accountBalance = UniversalAccounts.getInstance().getAccountBalance(this.accountNumber);
/* 415 */     setChanged();
/*     */   }
/*     */ }
