/*     */ package aspadien.universalcoins.block;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.*;
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
/*     */ import net.minecraft.client.gui.screen.Screen;
/*     */ import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.item.ItemStack;
/*     */
import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.util.ActionResultType;
/*     */ import net.minecraft.util.Direction;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
/*     */ import net.minecraft.world.IWorld;
/*     */
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import aspadien.universalcoins.gui.UCSignEditScreen;
/*     */ import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.tileentity.TileUCSign;
/*     */ 
/*     */ public class BlockUCWallSign extends WallSignBlock {
/*     */   public BlockUCWallSign() {
/*  28 */     super(AbstractBlock.Properties.of(Material.WOOD).noCollission().strength(1.0F), WoodType.OAK);
/*     */   }
/*     */ 
/*     */   
/*     */   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/*  33 */     return (TileEntity)new TileUCSign();
/*     */   }
/*     */ 
/*     */   
/*     */   public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
/*  38 */     TileEntity te = world.getBlockEntity(pos);
/*  39 */     if (te instanceof TileUCSign)
/*     */     {
/*  41 */       ((TileUCSign)te).scanChestContents(state, fromPos);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
/*  48 */     TileEntity te = world.getBlockEntity(pos);
/*  49 */     if (te instanceof TileUCSign) {
/*  50 */       ((TileUCSign)te).scanChestContents(state, null);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/*  58 */     TileEntity te = world.getBlockEntity(pos);
/*  59 */     if (te instanceof TileUCSign) {
/*  60 */       TileUCSign sign = (TileUCSign)te;
/*     */       
/*  62 */       if (player.getName().getString().equals(sign.blockOwner) || player.isCreative()) {
/*  63 */         if (world.isClientSide) {
/*  64 */           Minecraft.getInstance().setScreen((Screen)new UCSignEditScreen(sign));
/*     */         }
/*  66 */         return ActionResultType.SUCCESS;
/*     */       } 
/*     */     } 
/*  69 */     return ActionResultType.PASS;
/*     */   }
/*     */ 
/*     */   
/*     */   public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
/*  74 */     TileEntity te = world.getBlockEntity(pos);
/*  75 */     if (te instanceof TileUCSign && !player.isCreative()) {
/*  76 */       TileUCSign sign = (TileUCSign)te;
/*  77 */       if (!player.getName().getString().equals(sign.blockOwner)) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/*  82 */     super.playerWillDestroy(world, pos, state, player);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockRenderType getRenderShape(BlockState state) {
/*  89 */     return BlockRenderType.ENTITYBLOCK_ANIMATED;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
/*  95 */  return (facing == state.getValue(FACING).getOpposite() && !state.canSurvive(world, currentPos)) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
}
/*     */ 
/*     */   
/*     */   @OnlyIn(Dist.CLIENT)
/*     */   private void openSignGui(TileUCSign te) {
/* 101 */     Minecraft.getInstance().setScreen((Screen)new UCSignEditScreen(te));
/*     */   }
/*     */   
/*     */   public ItemStack getCloneItemStack(IBlockReader world, BlockPos pos, BlockState state) {
/* 105 */     return new ItemStack((IItemProvider) ModItems.UC_SIGN.get());
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockUCWallSign.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */