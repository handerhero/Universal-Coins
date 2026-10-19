/*     */ package aspadien.universalcoins.block;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.AbstractBlock;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.block.HorizontalBlock;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
/*     */ import net.minecraft.item.BlockItemUseContext;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.state.DirectionProperty;
/*     */ import net.minecraft.state.Property;
/*     */ import net.minecraft.state.StateContainer;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.util.ActionResultType;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.Util;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.TextFormatting;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import net.minecraft.world.IBlockReader;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.network.NetworkHooks;
import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.tileentity.TileSignal;
/*     */ import aspadien.universalcoins.util.CoinUtils;
/*     */ import aspadien.universalcoins.util.UCUtils;
/*     */ 
/*     */ public class BlockSignal extends Block {
/*  36 */   public static final DirectionProperty FACING = HorizontalBlock.FACING;
/*     */   
/*     */   public BlockSignal() {
/*  39 */     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 30.0F));
/*  40 */     registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/*  46 */     if (hand != Hand.MAIN_HAND) return ActionResultType.PASS;
/*     */     
/*  48 */     TileEntity te = world.getBlockEntity(pos);
/*  49 */     if (!(te instanceof TileSignal)) return ActionResultType.PASS; 
/*  50 */     TileSignal tile = (TileSignal)te;
/*     */ 
/*     */     
/*  53 */     if (player.isShiftKeyDown()) {
/*  54 */       if (!world.isClientSide) {
/*  55 */         if (player.getUUID().toString().equals(tile.blockOwner) || player.isCreative()) {
/*  56 */           NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tile, pos);
/*     */         } else {
/*  58 */           player.sendMessage((ITextComponent)new TranslationTextComponent("chat.warning.private"), Util.NIL_UUID);
/*     */         } 
/*     */       }
/*  61 */       return ActionResultType.sidedSuccess(world.isClientSide);
/*     */     } 
/*     */     
/*  64 */     if (world.isClientSide) return ActionResultType.SUCCESS;
/*     */     
/*  66 */     int feeNeeded = tile.fee;
/*  67 */     boolean paid = false;
/*     */ 
/*     */     
/*  70 */     ItemStack heldMain = player.getItemInHand(Hand.MAIN_HAND);
/*  71 */     ItemStack heldOff = player.getItemInHand(Hand.OFF_HAND);
/*  72 */     ItemStack cardToUse = ItemStack.EMPTY;
/*     */ 
/*     */     
/*  75 */     if (UCUtils.getCardBalance(heldMain) >= feeNeeded) {
/*  76 */       cardToUse = heldMain;
/*  77 */     } else if (UCUtils.getCardBalance(heldOff) >= feeNeeded) {
/*  78 */       cardToUse = heldOff;
/*     */     } 
/*     */     
/*  81 */     if (!cardToUse.isEmpty() && 
/*  82 */       UCUtils.debitCard(cardToUse, feeNeeded)) {
/*  83 */       paid = true;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  88 */     if (!paid) {
/*  89 */       int totalCoins = 0;
/*  90 */       for (int i = 0; i < player.inventory.getContainerSize(); i++) {
/*  91 */         int val = CoinUtils.getCoinValue(player.inventory.getItem(i));
/*  92 */         if (val > 0) totalCoins += val * player.inventory.getItem(i).getCount();
/*     */       
/*     */       } 
/*  95 */       if (totalCoins >= feeNeeded) {
/*  96 */         int remaining = feeNeeded;
/*  97 */         for (int j = 0; j < player.inventory.getContainerSize(); j++) {
/*  98 */           ItemStack stack = player.inventory.getItem(j);
/*  99 */           int val = CoinUtils.getCoinValue(stack);
/* 100 */           if (val > 0) {
/* 101 */             int stackVal = val * stack.getCount();
/* 102 */             if (stackVal <= remaining) {
/* 103 */               remaining -= stackVal;
/* 104 */               player.inventory.setItem(j, ItemStack.EMPTY);
/*     */             } else {
/* 106 */               int toTake = (int)Math.ceil(remaining / val);
/* 107 */               int change = toTake * val - remaining;
/* 108 */               stack.shrink(toTake);
/* 109 */               if (change > 0) giveChange(world, player, change); 
/* 110 */               remaining = 0;
/*     */             } 
/*     */           } 
/* 113 */           if (remaining <= 0)
/*     */             break; 
/* 115 */         }  paid = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 120 */     if (paid) {
/* 121 */       completeActivation(player, tile);
/*     */     } else {
/*     */       
/* 124 */       player.sendMessage((ITextComponent)(new TranslationTextComponent("signal.message.notenough")).withStyle(TextFormatting.RED), Util.NIL_UUID);
/*     */     } 
/*     */     
/* 127 */     return ActionResultType.CONSUME;
/*     */   }
/*     */   
/*     */   private void completeActivation(PlayerEntity player, TileSignal tile) {
/* 131 */     player.sendMessage((ITextComponent)(new TranslationTextComponent("signal.message.activated")).withStyle(TextFormatting.GREEN), Util.NIL_UUID);
/* 132 */     tile.activateSignal();
/* 133 */     tile.setChanged();
/* 134 */     tile.updateTE();
/*     */   }
/*     */   
/*     */   private void giveChange(World world, PlayerEntity player, int amount) {
/* 138 */     int remaining = amount;
/* 139 */     for (int i = 4; i >= 0; i--) {
/* 140 */       int coinVal = UniversalCoins.coinValues[i];
/* 141 */       if (remaining >= coinVal) {
/* 142 */         int count = remaining / coinVal;
/* 143 */         ItemStack stack = new ItemStack((IItemProvider)ModItems.getCoinByIndex(i), count);
/* 144 */         if (!player.inventory.add(stack)) {
/* 145 */           player.drop(stack, false);
/*     */         }
/* 147 */         remaining %= coinVal;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
/* 154 */     if (!world.isClientSide && placer instanceof PlayerEntity) {
/* 155 */       TileEntity te = world.getBlockEntity(pos);
/* 156 */       if (te instanceof TileSignal) {
/* 157 */         ((TileSignal)te).blockOwner = placer.getUUID().toString();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
/* 164 */     TileEntity te = world.getBlockEntity(pos);
/* 165 */     if (te instanceof TileSignal) {
/* 166 */       return ((TileSignal)te).canProvidePower ? 15 : 0;
/*     */     }
/* 168 */     return 0;
/*     */   }
/*     */   
/*     */   public boolean isSignalSource(BlockState state) {
/* 172 */     return true;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public BlockState getStateForPlacement(BlockItemUseContext context) {
/* 177 */     return (BlockState)defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
/* 182 */     builder.add(new Property[] { (Property)FACING });
/*     */   }
/*     */   
/*     */   public boolean hasTileEntity(BlockState state) {
/* 186 */     return true;
/*     */   }
/*     */   @Nullable
/*     */   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/* 190 */     return (TileEntity)new TileSignal();
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockSignal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */