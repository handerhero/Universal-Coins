/*    */ package aspadien.universalcoins.block;
/*    */ import javax.annotation.Nullable;
/*    */ import net.minecraft.block.AbstractBlock;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.BlockState;
/*    */ import net.minecraft.block.HorizontalBlock;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
/*    */ import net.minecraft.item.BlockItemUseContext;
/*    */ import net.minecraft.state.DirectionProperty;
/*    */ import net.minecraft.state.Property;
/*    */ import net.minecraft.state.StateContainer;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.ActionResultType;
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.Util;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.BlockRayTraceResult;
/*    */ import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkHooks;
/*    */ import aspadien.universalcoins.tileentity.TilePackager;
/*    */ 
/*    */ public class BlockPackager extends BlockProtected {
/* 27 */   public static final DirectionProperty FACING = HorizontalBlock.FACING;
/*    */   
/*    */   public BlockPackager() {
/* 30 */     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 30.0F));
/* 31 */     registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/* 36 */     TileEntity te = world.getBlockEntity(pos);
/* 37 */     if (te instanceof TilePackager) {
/* 38 */       TilePackager tile = (TilePackager)te;
/* 39 */       if (tile.inUse) {
/* 40 */         if (!world.isClientSide) player.sendMessage((ITextComponent)new TranslationTextComponent("chat.warning.inuse"), Util.NIL_UUID); 
/* 41 */         return ActionResultType.SUCCESS;
/*    */       } 
/* 43 */       if (!world.isClientSide) {
/* 44 */         tile.playerName = player.getName().getString();
/* 45 */         tile.inUse = true;
/* 46 */         NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
/*    */       } 
/* 48 */       return ActionResultType.SUCCESS;
/*    */     } 
/*    */     
/* 51 */     return ActionResultType.SUCCESS;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public BlockState getStateForPlacement(BlockItemUseContext context) {
/* 57 */     return (BlockState)defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
/* 62 */     builder.add(new Property[] { (Property)FACING });
/*    */   }
/*    */ 
/*    */   
/*    */   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/* 67 */     return (TileEntity)new TilePackager();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockPackager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */