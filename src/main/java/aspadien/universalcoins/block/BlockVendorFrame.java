package aspadien.universalcoins.block;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import aspadien.universalcoins.tileentity.TileVendorFrame;

public class BlockVendorFrame extends Block {
  public static final DirectionProperty FACING = HorizontalBlock.FACING;

  protected static final VoxelShape NORTH_SHAPE = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 2.0D);
  protected static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
  protected static final VoxelShape EAST_SHAPE = Block.box(14.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
  protected static final VoxelShape WEST_SHAPE = Block.box(0.0D, 2.0D, 2.0D, 2.0D, 14.0D, 14.0D);

  public BlockVendorFrame() {
    super(AbstractBlock.Properties.of(Material.WOOD).strength(1.0F, 6000.0F).noOcclusion());
  }


  public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
    switch (state.getValue(FACING)) { case SOUTH:
        return SOUTH_SHAPE;
      case EAST: return EAST_SHAPE;
      case WEST: return WEST_SHAPE; }
     return NORTH_SHAPE;
  }



  @Nullable
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    Direction clickedFace = context.getClickedFace();


    if (clickedFace.getAxis().isHorizontal())
    {
      return defaultBlockState().setValue(FACING, clickedFace);
    }


    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }


  public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    if (!world.isClientSide && placer instanceof PlayerEntity) {
      TileEntity te = world.getBlockEntity(pos);
      if (te instanceof TileVendorFrame) {
          ((TileVendorFrame)te).blockOwner = placer.getName().getString();
          te.setChanged();
      }
    }
  }


  public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof TileVendorFrame) {
      TileVendorFrame vendor = (TileVendorFrame)te;
      if (vendor.inUse) {
        if (!world.isClientSide) player.sendMessage(new TranslationTextComponent("chat.warning.inuse"), Util.NIL_UUID);
        return ActionResultType.SUCCESS;
      }
      if (!world.isClientSide) {
        vendor.playerName = player.getName().getString();
        vendor.inUse = true;

        NetworkHooks.openGui((ServerPlayerEntity)player, vendor, pos);
      }
      return ActionResultType.SUCCESS;
    }

    return ActionResultType.PASS;
  }

  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new TileVendorFrame();
  }


  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    TileEntity te = world.getBlockEntity(pos);
    if (te instanceof TileVendorFrame && !world.isClientSide && !player.isCreative()) {
        TileVendorFrame vendor = (TileVendorFrame)te;
        ItemStack stack = new ItemStack(this);

        // В BlockVendorFrame владелец почему-то сохраняется как Name, а не UUID (особенность оригинального кода мода)
        boolean isOwner = vendor.blockOwner != null && vendor.blockOwner.equals(player.getName().getString());

        if (isOwner) {
            CompoundNBT tag = te.save(new CompoundNBT());
            // Отключаем безлимитный режим
            tag.putBoolean("Inf", false);
            stack.addTagElement("BlockEntityTag", (INBT)tag);
        }

        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
        world.removeBlockEntity(pos);
    }
    super.playerWillDestroy(world, pos, state, player);
}
}
