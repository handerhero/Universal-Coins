/*     */ package aspadien.universalcoins.tileentity;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.block.ChestBlock;
/*     */ import net.minecraft.inventory.IInventory;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.network.NetworkManager;
/*     */ import net.minecraft.network.play.server.SUpdateTileEntityPacket;
/*     */ import net.minecraft.state.Property;
/*     */ import net.minecraft.tileentity.SignTileEntity;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.tileentity.TileEntityType;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import aspadien.universalcoins.block.BlockUCWallSign;
/*     */ import aspadien.universalcoins.init.ModTileEntities;
/*     */ import aspadien.universalcoins.net.ModPacketHandler;
/*     */ import aspadien.universalcoins.net.UCSignServerMessage;
/*     */ import aspadien.universalcoins.util.UCUtils;
/*     */ 
/*     */ public class TileUCSign extends SignTileEntity {
/*  27 */   public String blockOwner = "";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TileEntityType<?> getType() {
/*  35 */     return (TileEntityType)ModTileEntities.UC_SIGN.get();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public CompoundNBT save(CompoundNBT tag) {
/*  41 */     tag.putString("blockOwner", this.blockOwner);
/*  42 */     return super.save(tag);
/*     */   }
/*     */ 
/*     */   
/*     */   public void load(BlockState state, CompoundNBT tag) {
/*  47 */     super.load(state, tag);
/*  48 */     this.blockOwner = tag.getString("blockOwner");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SUpdateTileEntityPacket getUpdatePacket() {
/*  55 */     return new SUpdateTileEntityPacket(this.worldPosition, 1, getUpdateTag());
/*     */   }
/*     */ 
/*     */   
/*     */   public CompoundNBT getUpdateTag() {
/*  60 */     return save(new CompoundNBT());
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
/*  65 */     load(getBlockState(), pkt.getTag());
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateSign() {
/*  70 */     if (this.level != null && !this.level.isClientSide) {
/*  71 */       setChanged();
/*  72 */       BlockState state = this.level.getBlockState(this.worldPosition);
/*  73 */       this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void sendServerUpdateMessage() {
/*  78 */     ITextComponent[] itext = new ITextComponent[4];
/*  79 */     for (int i = 0; i < 4; ) { itext[i] = getMessage(i); i++; }
/*  80 */      ModPacketHandler.sendToServer(new UCSignServerMessage(this.worldPosition, itext));
/*     */   }
/*     */   
/*     */   public void scanChestContents(BlockState state, BlockPos fromPos) {
/*  84 */     if (this.level == null || this.level.isClientSide) {
/*     */       return;
/*     */     }
/*  87 */     BlockPos targetPos = fromPos;
/*  88 */     if (targetPos == null) {
/*  89 */       if (state.getBlock() instanceof BlockUCWallSign) {
/*  90 */         targetPos = this.worldPosition.relative(((Direction)state.getValue((Property)BlockUCWallSign.FACING)).getOpposite());
/*     */       } else {
/*  92 */         targetPos = this.worldPosition.below();
/*     */       } 
/*     */     }
/*     */     
/*  96 */     TileEntity te = this.level.getBlockEntity(targetPos);
/*  97 */     if (te == null) {
/*     */       return;
/*     */     }
/* 100 */     if (te instanceof TileVendor) {
/* 101 */       TileVendor vendor = (TileVendor)te;
/* 102 */       ItemStack tradeStack = vendor.getItem(9);
/*     */       
/* 104 */       if (tradeStack.isEmpty()) {
/* 105 */         setMessage(0, (ITextComponent)new StringTextComponent("---"));
/* 106 */         setMessage(1, (ITextComponent)new StringTextComponent("Out of Order"));
/* 107 */         setMessage(2, (ITextComponent)new StringTextComponent("---"));
/* 108 */         setMessage(3, (ITextComponent)new StringTextComponent(""));
/*     */       } else {
/*     */         
/* 111 */         setMessage(0, (ITextComponent)new StringTextComponent(tradeStack.getHoverName().getString()));
/*     */ 
/*     */ 
/*     */         
/* 115 */         String mode = vendor.sellMode ? "Selling" : "Buying";
/* 116 */         setMessage(1, (ITextComponent)new StringTextComponent(mode));
/*     */ 
/*     */         
/* 119 */         setMessage(2, (ITextComponent)new StringTextComponent("Price: " + vendor.itemPrice));
/*     */ 
/*     */         
/* 122 */         if (vendor.infiniteMode) {
/* 123 */           setMessage(3, (ITextComponent)new StringTextComponent("Stock: Infinite"));
/*     */         } else {
/* 125 */           int stock = 0;
/*     */           
/* 127 */           for (int i = 0; i <= 8; i++) {
/* 128 */             ItemStack s = vendor.getItem(i);
/* 129 */             if (!s.isEmpty() && UCUtils.isSameItem(s, tradeStack)) {
/* 130 */               stock += s.getCount();
/*     */             }
/*     */           } 
/* 133 */           setMessage(3, (ITextComponent)new StringTextComponent("In Stock: " + stock));
/*     */         } 
/*     */       } 
/* 136 */       updateSign();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 141 */     IInventory inventory = null;
/* 142 */     if (te instanceof IInventory) {
/* 143 */       inventory = (IInventory)te;
/*     */     } else {
/* 145 */       BlockState targetState = this.level.getBlockState(targetPos);
/* 146 */       if (targetState.getBlock() instanceof ChestBlock) {
/* 147 */         inventory = ChestBlock.getContainer((ChestBlock)targetState
/* 148 */             .getBlock(), targetState, this.level, targetPos, true);
/*     */       }
/*     */     } 
/*     */     
/* 152 */     if (inventory != null) {
/* 153 */       Map<String, Integer> counts = new LinkedHashMap<>();
/* 154 */       int emptySlots = 0;
/*     */       
/* 156 */       for (int i = 0; i < inventory.getContainerSize(); i++) {
/* 157 */         ItemStack stack = inventory.getItem(i);
/* 158 */         if (stack.isEmpty()) {
/* 159 */           emptySlots++;
/*     */         } else {
/* 161 */           String name = stack.getHoverName().getString();
/* 162 */           counts.put(name, Integer.valueOf(((Integer)counts.getOrDefault(name, Integer.valueOf(0))).intValue() + stack.getCount()));
/*     */         } 
/*     */       } 
/*     */       
/* 166 */       int line = 0;
/* 167 */       for (Map.Entry<String, Integer> entry : counts.entrySet()) {
/* 168 */         if (line < 4) {
/* 169 */           setMessage(line++, (ITextComponent)new StringTextComponent((new StringBuilder()).append(entry.getValue()).append(" ").append(entry.getKey()).toString()));
/*     */         }
/*     */       } 
/* 172 */       if (line < 4 && emptySlots > 0) {
/* 173 */         setMessage(line++, (ITextComponent)new StringTextComponent(emptySlots + " Empty Slots"));
/*     */       }
/* 175 */       while (line < 4) {
/* 176 */         setMessage(line++, (ITextComponent)new StringTextComponent(""));
/*     */       }
/*     */       
/* 179 */       updateSign();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TileUCSign.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */