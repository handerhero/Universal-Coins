/*     */ package aspadien.universalcoins.tileentity;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
/*     */ import net.minecraft.inventory.container.Container;
/*     */ import net.minecraft.inventory.container.INamedContainerProvider;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.tileentity.ITickableTileEntity;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.tileentity.TileEntityType;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import net.minecraftforge.common.capabilities.Capability;
/*     */ import net.minecraftforge.common.util.LazyOptional;
/*     */ import net.minecraftforge.energy.CapabilityEnergy;
/*     */ import net.minecraftforge.energy.IEnergyStorage;
/*     */ import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.container.ContainerPowerReceiver;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UniversalAccounts;
/*     */ import aspadien.universalcoins.util.UniversalPower;
/*     */ 
/*     */ public class TilePowerReceiver extends TileProtected implements ITickableTileEntity, ISidedInventory, IEnergyStorage, INamedContainerProvider {
/*  35 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY); public static final int itemCardSlot = 0;
/*     */   public static final int itemCoinSlot = 1;
/*     */   public static final int itemOutputSlot = 2;
/*  38 */   public long coinSum = 0L;
/*  39 */   public int feLevel = 0;
/*  40 */   public int feOutput = 0;
/*  41 */   public long wfeLevel = 0L;
/*  42 */   public Direction orientation = null;
/*     */   public boolean publicAccess = false;
/*     */   
/*  45 */   protected final IIntArray receiverData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  48 */         switch (index) { case 0:
/*  49 */             return (int)(TilePowerReceiver.this.coinSum & 0xFFFFFFFFL);
/*  50 */           case 1: return (int)(TilePowerReceiver.this.coinSum >> 32L);
/*  51 */           case 2: return TilePowerReceiver.this.feLevel;
/*  52 */           case 3: return TilePowerReceiver.this.feOutput;
/*  53 */           case 4: return (int)(TilePowerReceiver.this.wfeLevel & 0xFFFFFFFFL);
/*  54 */           case 5: return (int)(TilePowerReceiver.this.wfeLevel >> 32L);
/*  55 */           case 6: return TilePowerReceiver.this.publicAccess ? 1 : 0; }
/*  56 */          return 0;
/*     */       }
/*     */       
/*     */       public void set(int index, int value)
/*     */       {
/*  61 */         switch (index) { case 0:
/*  62 */             TilePowerReceiver.this.coinSum = TilePowerReceiver.this.coinSum & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; break;
/*  63 */           case 1: TilePowerReceiver.this.coinSum = TilePowerReceiver.this.coinSum & 0xFFFFFFFFL | value << 32L; break;
/*  64 */           case 2: TilePowerReceiver.this.feLevel = value; break;
/*  65 */           case 3: TilePowerReceiver.this.feOutput = value; break;
/*  66 */           case 4: TilePowerReceiver.this.wfeLevel = TilePowerReceiver.this.wfeLevel & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; break;
/*  67 */           case 5: TilePowerReceiver.this.wfeLevel = TilePowerReceiver.this.wfeLevel & 0xFFFFFFFFL | value << 32L; break;
/*  68 */           case 6: TilePowerReceiver.this.publicAccess = (value == 1);
/*     */             break; }
/*     */          } public int getCount() {
/*  71 */         return 7;
/*     */       }
/*     */     }; private final LazyOptional<IEnergyStorage> energyHolder;
/*  74 */   public TilePowerReceiver() { super((TileEntityType)ModTileEntities.POWER_RECEIVER.get());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     this.energyHolder = LazyOptional.of(() -> this); }
/*     */   public void tick() { if (this.level != null && !this.level.isClientSide) { absorbCoinsFromSlot(); buyPower(); sendPower(); setChanged(); }  }
/*     */   private void absorbCoinsFromSlot() { ItemStack stack = (ItemStack)this.inventory.get(1); if (!stack.isEmpty()) { int val = CoinUtils.getCoinValue(stack); if (val > 0) { this.coinSum += val * stack.getCount(); this.inventory.set(1, ItemStack.EMPTY); setChanged(); this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3); }  }  }
/*     */   protected void buyPower() { this.wfeLevel = UniversalPower.getInstance().getFeLevel(); if (this.feLevel == 0 && UniversalPower.getInstance().extractEnergy(10, true) > 0L) { int rate = UniversalCoins.rfRetailRate; if (debitAccount(rate)) { UniversalPower.getInstance().extractEnergy(10, false); this.feLevel += 10000; updateTE(); } else if (this.coinSum >= rate) { this.coinSum -= rate; UniversalPower.getInstance().extractEnergy(10, false); this.feLevel += 10000; updateTE(); }  }  }
/* 177 */   protected void sendPower() { if (this.orientation == null) { resetPowerDirection(); return; }  TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.orientation)); if (te != null) { te.getCapability(CapabilityEnergy.ENERGY, this.orientation.getOpposite()).ifPresent(storage -> { if (this.feLevel > 0) { int sent = storage.receiveEnergy(Math.min(1000, this.feLevel), false); if (sent > 0) { this.feOutput = sent; this.feLevel -= sent; setChanged(); }  } else { this.feOutput = 0; }  }); } else { this.orientation = null; this.feOutput = 0; }  } private void resetPowerDirection() { for (Direction dir : Direction.values()) { TileEntity te = this.level.getBlockEntity(this.worldPosition.relative(dir)); if (te != null && te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent()) { this.orientation = dir; return; }  }  } private boolean debitAccount(int amount) { ItemStack card = getItem(0); if (card.isEmpty() || !card.hasTag()) return false;  String acc = card.getTag().getString("Account"); return UniversalAccounts.getInstance().debitAccount(acc, amount, false); } public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) { if (cap == CapabilityEnergy.ENERGY) return this.energyHolder.cast(); 
/* 178 */     return super.getCapability(cap, side); }
/*     */   public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
/*     */   public int extractEnergy(int maxExtract, boolean simulate) { int extracted = Math.min(this.feLevel, maxExtract); if (!simulate && extracted > 0) this.feLevel -= extracted;  return extracted; }
/* 181 */   public int getEnergyStored() { return this.feLevel; } public int getMaxEnergyStored() { return 1000000; } public boolean canExtract() { return true; } public boolean canReceive() { return false; } public int getContainerSize() { return 3; } public ItemStack getItem(int index) {
/* 182 */     return (ItemStack)this.inventory.get(index);
/*     */   }
/*     */   
/*     */   public void setItem(int index, ItemStack stack) {
/* 186 */     this.inventory.set(index, stack);
/*     */     
/* 188 */     if (!this.level.isClientSide) {
/* 189 */       setChanged();
/* 190 */       this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
/*     */     } 
/*     */   }
/*     */   public ITextComponent getDisplayName() {
/* 194 */     return (ITextComponent)new TranslationTextComponent("block.universalcoins.power_receiver");
/*     */   } @Nullable
/*     */   public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
/* 197 */     return (Container)new ContainerPowerReceiver(id, inv, (IInventory)this, this.receiverData, this);
/*     */   }
/*     */   
/*     */   public boolean stillValid(PlayerEntity player) {
/* 201 */     return (player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D);
/*     */   }
/*     */   
/* 204 */   public void clearContent() { this.inventory.clear(); } public boolean isEmpty() {
/* 205 */     return this.inventory.stream().allMatch(ItemStack::isEmpty);
/*     */   } public ItemStack removeItem(int index, int count) {
/* 207 */     ItemStack s = ItemStackHelper.removeItem((List)this.inventory, index, count);
/* 208 */     setChanged();
/* 209 */     updateTE();
/* 210 */     return s;
/*     */   } public ItemStack removeItemNoUpdate(int index) {
/* 212 */     return ItemStackHelper.takeItem((List)this.inventory, index);
/*     */   }
/* 214 */   public int[] getSlotsForFace(Direction side) { return new int[] { 1 }; }
/* 215 */   public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) { return (index == 1); } public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
/* 216 */     return false;
/*     */   }
/*     */   
/*     */   public void load(BlockState state, CompoundNBT tag) {
/* 220 */     super.load(state, tag);
/* 221 */     this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
/* 222 */     ItemStackHelper.loadAllItems(tag, this.inventory);
/* 223 */     this.coinSum = tag.getLong("coinSum");
/* 224 */     this.feLevel = tag.getInt("feLevel");
/* 225 */     this.publicAccess = tag.getBoolean("publicAccess");
/*     */   }
/*     */ 
/*     */   
/*     */   public CompoundNBT save(CompoundNBT tag) {
/* 230 */     super.save(tag);
/* 231 */     ItemStackHelper.saveAllItems(tag, this.inventory);
/* 232 */     tag.putLong("coinSum", this.coinSum);
/* 233 */     tag.putInt("feLevel", this.feLevel);
/* 234 */     tag.putBoolean("publicAccess", this.publicAccess);
/* 235 */     return tag;
/*     */   }
/*     */   
/*     */   public void onButtonPressed(int buttonId, boolean shift) {
/* 239 */     if (this.level == null || this.level.isClientSide)
/* 240 */       return;  if (buttonId == 0) {
/* 241 */       fillOutputSlot();
/* 242 */     } else if (buttonId == 1) {
/* 243 */       this.publicAccess = !this.publicAccess;
/*     */     } 
/* 245 */     updateTE();
/*     */   }
/*     */   
/*     */   private void fillOutputSlot() {
/* 249 */     if (getItem(2).isEmpty() && this.coinSum > 0L) {
/* 250 */       for (int i = 4; i >= 0; i--) {
/* 251 */         int val = UniversalCoins.coinValues[i];
/* 252 */         if (this.coinSum >= val) {
/* 253 */           ItemStack coins = new ItemStack((IItemProvider)ModItems.getCoinByIndex(i));
/* 254 */           int count = (int)Math.min(this.coinSum / val, 64L);
/* 255 */           coins.setCount(count);
/*     */           
/* 257 */           this.inventory.set(2, coins);
/* 258 */           this.coinSum -= count * val;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 263 */     updateTE();
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TilePowerReceiver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */