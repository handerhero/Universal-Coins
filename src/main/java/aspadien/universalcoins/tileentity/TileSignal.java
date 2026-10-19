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
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.tileentity.ITickableTileEntity;
/*     */ import net.minecraft.tileentity.TileEntityType;
/*     */ import net.minecraft.util.IIntArray;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.NonNullList;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.container.ContainerSignal;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ 
/*     */ public class TileSignal extends TileProtected implements IInventory, ITickableTileEntity, INamedContainerProvider {
/*  27 */   private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
/*     */   
/*     */   public static final int itemOutputSlot = 0;
/*  30 */   public int coinSum = 0;
/*  31 */   public int fee = 1;
/*  32 */   public int duration = 1;
/*  33 */   public int counter = 0;
/*  34 */   public int secondsLeft = 0;
/*  35 */   public String customName = "";
/*     */   
/*     */   public boolean canProvidePower = false;
/*     */   
/*  39 */   protected final IIntArray signalData = new IIntArray()
/*     */     {
/*     */       public int get(int index) {
/*  42 */         switch (index) { case 0:
/*  43 */             return TileSignal.this.coinSum;
/*  44 */           case 1: return TileSignal.this.fee;
/*  45 */           case 2: return TileSignal.this.duration;
/*  46 */           case 3: return TileSignal.this.secondsLeft;
/*  47 */           case 4: return TileSignal.this.canProvidePower ? 1 : 0; }
/*  48 */          return 0;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void set(int index, int value) {
/*  54 */         switch (index) { case 0:
/*  55 */             TileSignal.this.coinSum = value; break;
/*  56 */           case 1: TileSignal.this.fee = value; break;
/*  57 */           case 2: TileSignal.this.duration = value; break;
/*  58 */           case 3: TileSignal.this.secondsLeft = value; break;
/*  59 */           case 4: TileSignal.this.canProvidePower = (value == 1);
/*     */             break; }
/*     */       
/*     */       }
/*     */       public int getCount() {
/*  64 */         return 5;
/*     */       }
/*     */     };
/*     */   public TileSignal() {
/*  68 */     super((TileEntityType)ModTileEntities.SIGNAL.get());
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick() {
/*  73 */     if (this.level != null && !this.level.isClientSide) {
/*  74 */       if (this.counter > 0) {
/*  75 */         this.counter--;
/*  76 */         int currentSeconds = (int)Math.ceil(this.counter / 20.0D);
/*  77 */         if (currentSeconds != this.secondsLeft) {
/*  78 */           this.secondsLeft = currentSeconds;
/*  79 */           setChanged();
/*     */         } 
/*  81 */       } else if (this.canProvidePower) {
/*  82 */         this.canProvidePower = false;
/*  83 */         this.secondsLeft = 0;
/*  84 */         updateNeighbors();
/*  85 */         setChanged();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void onButtonPressed(int buttonId, boolean shift) {
/*  91 */     if (this.level == null || this.level.isClientSide)
/*     */       return; 
/*  93 */     switch (buttonId) {
/*     */       case 0:
/*  95 */         fillOutputSlot();
/*     */         break;
/*     */       case 1:
/*  98 */         this.duration = Math.max(1, this.duration - (shift ? 10 : 1));
/*     */         break;
/*     */       case 2:
/* 101 */         this.duration = Math.min(1000000, this.duration + (shift ? 10 : 1));
/*     */         break;
/*     */       case 3:
/* 104 */         this.fee = Math.max(1, this.fee - (shift ? 10 : 1));
/*     */         break;
/*     */       case 4:
/* 107 */         this.fee = Math.min(1000000, this.fee + (shift ? 10 : 1));
/*     */         break;
/*     */     } 
/* 110 */     setChanged();
/*     */   }
/*     */   
/*     */   public void activateSignal() {
/* 114 */     this.canProvidePower = true;
/* 115 */     this.counter += this.duration * 20;
/* 116 */     this.coinSum += this.fee;
/* 117 */     updateNeighbors();
/* 118 */     setChanged();
/*     */   }
/*     */   
/*     */   private void updateNeighbors() {
/* 122 */     if (this.level != null)
/*     */     {
/* 124 */       this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
/*     */     }
/*     */   }
/*     */   
/*     */   private void fillOutputSlot() {
/* 129 */     if (getItem(0).isEmpty() && this.coinSum > 0) {
/* 130 */       for (int i = 4; i >= 0; i--) {
/* 131 */         int val = UniversalCoins.coinValues[i];
/* 132 */         if (this.coinSum >= val) {
/* 133 */           ItemStack coins = new ItemStack((IItemProvider)ModItems.getCoinByIndex(i));
/* 134 */           int count = Math.min(this.coinSum / val, 64);
/* 135 */           coins.setCount(count);
/* 136 */           setItem(0, coins);
/* 137 */           this.coinSum -= count * val;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public int getContainerSize() {
/* 145 */     return this.inventory.size(); }
/* 146 */   public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); }
/* 147 */   public ItemStack getItem(int index) { return (ItemStack)this.inventory.get(index); }
/* 148 */   public ItemStack removeItem(int index, int count) { return ItemStackHelper.removeItem((List)this.inventory, index, count); }
/* 149 */   public ItemStack removeItemNoUpdate(int index) { return ItemStackHelper.takeItem((List)this.inventory, index); }
/* 150 */   public void setItem(int index, ItemStack stack) { this.inventory.set(index, stack); setChanged(); }
/* 151 */   public boolean stillValid(PlayerEntity player) { return (player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) <= 64.0D); } public void clearContent() {
/* 152 */     this.inventory.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void load(BlockState state, CompoundNBT tag) {
/* 157 */     super.load(state, tag);
/* 158 */     ItemStackHelper.loadAllItems(tag, this.inventory);
/* 159 */     this.coinSum = tag.getInt("coinSum");
/* 160 */     this.fee = tag.getInt("fee");
/* 161 */     this.duration = tag.getInt("duration");
/* 162 */     this.secondsLeft = tag.getInt("secondsLeft");
/* 163 */     this.counter = this.secondsLeft * 20;
/* 164 */     this.canProvidePower = tag.getBoolean("canProvidePower");
/* 165 */     this.customName = tag.getString("customName");
/*     */   }
/*     */ 
/*     */   
/*     */   public CompoundNBT save(CompoundNBT tag) {
/* 170 */     super.save(tag);
/* 171 */     ItemStackHelper.saveAllItems(tag, this.inventory);
/* 172 */     tag.putInt("coinSum", this.coinSum);
/* 173 */     tag.putInt("fee", this.fee);
/* 174 */     tag.putInt("duration", this.duration);
/* 175 */     tag.putInt("secondsLeft", this.secondsLeft);
/* 176 */     tag.putBoolean("canProvidePower", this.canProvidePower);
/* 177 */     tag.putString("customName", this.customName);
/* 178 */     return tag;
/*     */   }
/*     */ 
/*     */   
/*     */   public ITextComponent getDisplayName() {
/* 183 */     return this.customName.isEmpty() ? (ITextComponent)new TranslationTextComponent("block.aspadien.universalcoins.signal") : (ITextComponent)new StringTextComponent(this.customName);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
/* 189 */     return (Container)new ContainerSignal(id, inv, this, this.signalData, this);
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TileSignal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */