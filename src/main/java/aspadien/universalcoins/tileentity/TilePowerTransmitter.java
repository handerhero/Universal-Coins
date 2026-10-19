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
/*     */ import net.minecraft.tileentity.ITickableTileEntity;
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
/*     */ import aspadien.universalcoins.container.ContainerPowerTransmitter;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.util.UniversalAccounts;
/*     */ import aspadien.universalcoins.util.UniversalPower;
/*     */ 
/*     */ public class TilePowerTransmitter extends TileProtected implements IInventory, IEnergyStorage, INamedContainerProvider, ITickableTileEntity {
/*  33 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
/*     */   public static final int itemCardSlot = 0;
/*     */   public static final int itemOutputSlot = 1;
/*  36 */   public long coinSum = 0L;
/*  37 */   public int feLevel = 0;
/*  38 */   public int kfeSold = 0;
/*     */   public boolean publicAccess = false;
/*     */   
/*  41 */   protected final IIntArray transmitterData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  44 */         switch (index) { case 0:
/*  45 */             return (int)(TilePowerTransmitter.this.coinSum & 0xFFFFFFFFL);
/*  46 */           case 1: return (int)(TilePowerTransmitter.this.coinSum >> 32L);
/*  47 */           case 2: return TilePowerTransmitter.this.feLevel;
/*  48 */           case 3: return TilePowerTransmitter.this.kfeSold;
/*  49 */           case 4: return TilePowerTransmitter.this.publicAccess ? 1 : 0; }
/*  50 */          return 0;
/*     */       }
/*     */ 
/*     */       
/*     */       public void set(int index, int value) {
/*  55 */         switch (index) { case 0:
/*  56 */             TilePowerTransmitter.this.coinSum = TilePowerTransmitter.this.coinSum & 0xFFFFFFFF00000000L | value & 0xFFFFFFFFL; break;
/*  57 */           case 1: TilePowerTransmitter.this.coinSum = TilePowerTransmitter.this.coinSum & 0xFFFFFFFFL | value << 32L; break;
/*  58 */           case 2: TilePowerTransmitter.this.feLevel = value; break;
/*  59 */           case 3: TilePowerTransmitter.this.kfeSold = value; break;
/*  60 */           case 4: TilePowerTransmitter.this.publicAccess = (value == 1);
/*     */             break; }
/*     */       
/*     */       } public int getCount() {
/*  64 */         return 5;
/*     */       }
/*     */     }; private final LazyOptional<IEnergyStorage> energyHolder;
/*  67 */   public TilePowerTransmitter() { super((TileEntityType)ModTileEntities.POWER_TRANSMITTER.get());
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
/* 122 */     this.energyHolder = LazyOptional.of(() -> this); }
/*     */   public void tick() { if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 10L == 0L) setChanged();  }
/*     */   public int receiveEnergy(int maxReceive, boolean simulate) { int canReceive = Math.min(maxReceive, 1000000 - this.feLevel); if (!simulate) { this.feLevel += canReceive; processEnergySale(); }  return canReceive; }
/* 125 */   private void processEnergySale() { if (this.feLevel >= 10000) { int chunks = this.feLevel / 10000; int payment = chunks * UniversalCoins.rfWholesaleRate; if (creditAccount(payment)) { this.kfeSold += chunks * 10; this.feLevel -= chunks * 10000; UniversalPower.getInstance().receiveEnergy(chunks * 10, false); } else { this.coinSum += payment; this.kfeSold += chunks * 10; this.feLevel -= chunks * 10000; UniversalPower.getInstance().receiveEnergy(chunks * 10, false); }  setChanged(); }  } private boolean creditAccount(long amount) { ItemStack card = getItem(0); if (card.isEmpty() || !card.hasTag()) return false;  String acc = card.getTag().getString("Account"); return UniversalAccounts.getInstance().creditAccount(acc, amount, false); } public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) { if (cap == CapabilityEnergy.ENERGY) return this.energyHolder.cast(); 
/* 126 */     return super.getCapability(cap, side); }
/*     */   public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
/*     */   public int getEnergyStored() { return this.feLevel; }
/*     */   public int getMaxEnergyStored() { return 1000000; }
/* 130 */   public boolean canExtract() { return false; } public boolean canReceive() { return true; } public int getContainerSize() { return 2; }
/* 131 */    public ItemStack getItem(int index) { return (ItemStack)this.inventory.get(index); }
/* 132 */   public void setItem(int index, ItemStack stack) { this.inventory.set(index, stack); setChanged(); }
/* 133 */   public boolean stillValid(PlayerEntity player) { return (player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D); }
/* 134 */   public void clearContent() { this.inventory.clear(); }
/* 135 */   public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); }
/* 136 */   public ItemStack removeItem(int index, int count) { return ItemStackHelper.removeItem((List)this.inventory, index, count); }
/* 137 */   public ItemStack removeItemNoUpdate(int index) { return ItemStackHelper.takeItem((List)this.inventory, index); }
/* 138 */   public ITextComponent getDisplayName() { return (ITextComponent)new TranslationTextComponent("block.universalcoins.power_transmitter"); } @Nullable
/*     */   public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
/* 140 */     return (Container)new ContainerPowerTransmitter(id, inv, this, this.transmitterData, this);
/*     */   }
/*     */   
/*     */   public void onButtonPressed(int buttonId, boolean shift) {
/* 144 */     if (this.level == null || this.level.isClientSide) {
/*     */       return;
/*     */     }
/* 147 */     if (buttonId == 0) {
/* 148 */       fillOutputSlot();
/* 149 */     } else if (buttonId == 1) {
/* 150 */       this.publicAccess = !this.publicAccess;
/*     */     } 
/* 152 */     setChanged();
/*     */   }
/*     */   
/*     */   public void updateTE() {
/* 156 */     if (this.level != null) {
/* 157 */       BlockState state = this.level.getBlockState(this.worldPosition);
/* 158 */       this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
/* 159 */       setChanged();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fillOutputSlot() {
/* 164 */     if (getItem(1).isEmpty() && this.coinSum > 0L) {
/* 165 */       for (int i = 4; i >= 0; i--) {
/* 166 */         int val = UniversalCoins.coinValues[i];
/* 167 */         if (this.coinSum >= val) {
/* 168 */           ItemStack coins = new ItemStack((IItemProvider)ModItems.getCoinByIndex(i));
/* 169 */           int count = (int)Math.min(this.coinSum / val, 64L);
/* 170 */           coins.setCount(count);
/* 171 */           setItem(1, coins);
/* 172 */           this.coinSum -= count * val;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/* 177 */     updateTE();
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TilePowerTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */