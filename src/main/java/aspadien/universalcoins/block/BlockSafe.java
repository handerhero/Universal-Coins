 package aspadien.universalcoins.block;
 import javax.annotation.Nullable;
 import net.minecraft.block.AbstractBlock;
 import net.minecraft.block.Block;
 import net.minecraft.block.BlockState;
 import net.minecraft.block.HorizontalBlock;
 import net.minecraft.block.material.Material;
 import net.minecraft.entity.LivingEntity;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.entity.player.ServerPlayerEntity;
 import net.minecraft.inventory.container.INamedContainerProvider;
 import net.minecraft.item.BlockItemUseContext;
 import net.minecraft.item.ItemStack;
 import net.minecraft.state.DirectionProperty;
 import net.minecraft.state.Property;
 import net.minecraft.state.StateContainer;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Direction;
 import net.minecraft.util.Hand;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.util.math.BlockRayTraceResult;
 import net.minecraft.world.IBlockReader;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkHooks;
 import aspadien.universalcoins.tileentity.TileSafe;

 public class BlockSafe extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.FACING;

   public BlockSafe() {
     super(AbstractBlock.Properties.of(Material.METAL)
        .strength(3.0F, 30.0F)
         .noOcclusion());
     registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
   }


   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
     if (!world.isClientSide) {
       TileEntity te = world.getBlockEntity(pos);
       if (te instanceof TileSafe)
       {


         NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
       }
     }
     return ActionResultType.SUCCESS;
   }



   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
     world.setBlock(pos, (BlockState)state.setValue((Property)FACING, (Comparable)placer.getDirection().getOpposite()), 2);



     super.setPlacedBy(world, pos, state, placer, stack);
   }





   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
     return (BlockState)defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
   }


   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
     builder.add(new Property[] { (Property)FACING });
   }

   public boolean hasTileEntity(BlockState state) {
     return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
     return (TileEntity)new TileSafe();
   }
 }

