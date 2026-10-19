/*     */ package aspadien.universalcoins.block;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.AbstractBlock;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockRenderType;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.block.StandingSignBlock;
/*     */ import net.minecraft.block.WoodType;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.gui.screen.Screen;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.util.ActionResultType;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.world.IBlockReader;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.api.distmarker.Dist;
/*     */ import net.minecraftforge.api.distmarker.OnlyIn;
import aspadien.universalcoins.gui.UCSignEditScreen;
/*     */ import aspadien.universalcoins.init.ModItems;
/*     */ import aspadien.universalcoins.tileentity.TileUCSign;
/*     */ 
/*     */ public class BlockUCStandingSign extends StandingSignBlock {
/*     */   public BlockUCStandingSign() {
/*  30 */     super(AbstractBlock.Properties.of(Material.WOOD).noCollission().strength(1.0F), WoodType.OAK);
/*     */   }
/*     */ 
/*     */   
/*     */   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/*  35 */     return (TileEntity)new TileUCSign();
/*     */   }
/*     */ 
/*     */   
/*     */   public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
/*  40 */     TileEntity te = world.getBlockEntity(pos);
/*  41 */     if (te instanceof TileUCSign)
/*     */     {
/*  43 */       ((TileUCSign)te).scanChestContents(state, fromPos);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
/*  50 */     TileEntity te = world.getBlockEntity(pos);
/*  51 */     if (te instanceof TileUCSign) {
/*  52 */       ((TileUCSign)te).scanChestContents(state, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/*  60 */     TileEntity te = world.getBlockEntity(pos);
/*  61 */     if (te instanceof TileUCSign) {
/*  62 */       TileUCSign sign = (TileUCSign)te;
/*     */       
/*  64 */       if (player.getName().getString().equals(sign.blockOwner) || player.isCreative()) {
/*  65 */         if (world.isClientSide) {
/*  66 */           Minecraft.getInstance().setScreen((Screen)new UCSignEditScreen(sign));
/*     */         }
/*  68 */         return ActionResultType.SUCCESS;
/*     */       } 
/*     */     } 
/*  71 */     return ActionResultType.PASS;
/*     */   }
/*     */ 
/*     */   
/*     */   public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
/*  76 */     TileEntity te = world.getBlockEntity(pos);
/*  77 */     if (te instanceof TileUCSign && !player.isCreative()) {
/*  78 */       TileUCSign sign = (TileUCSign)te;
/*  79 */       if (!player.getName().getString().equals(sign.blockOwner)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/*  84 */     super.playerWillDestroy(world, pos, state, player);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockRenderType getRenderShape(BlockState state) {
/*  91 */     return BlockRenderType.ENTITYBLOCK_ANIMATED;
/*     */   }
/*     */ 
/*     */   
/*     */   @OnlyIn(Dist.CLIENT)
/*     */   private void openSignGui(TileUCSign te) {
/*  97 */     Minecraft.getInstance().setScreen((Screen)new UCSignEditScreen(te));
/*     */   }
/*     */ 
/*     */   
/*     */   public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
/* 102 */     return new ItemStack((IItemProvider)ModItems.UC_SIGN.get());
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockUCStandingSign.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */