package aspadien.universalcoins.block;

import aspadien.universalcoins.blockentities.VendorFrame;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class BlockVendorFrame extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected static final VoxelShape NORTH_SHAPE = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 2.0D);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
    protected static final VoxelShape EAST_SHAPE  = Block.box(14.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
    protected static final VoxelShape WEST_SHAPE  = Block.box(0.0D, 2.0D, 2.0D, 2.0D, 14.0D, 14.0D);

    public BlockVendorFrame() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOD)
                .strength(1.0F, 6000.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal()) {
            return this.defaultBlockState().setValue(FACING, clickedFace);
        }
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof VendorFrame frame) {
                frame.blockOwner = player.getName().getString();
                frame.setChanged();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof VendorFrame vendor) {
            if (vendor.inUse) {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.translatable("chat.warning.inuse"));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                vendor.playerName = player.getName().getString();
                vendor.inUse = true;
                NetworkHooks.openScreen(serverPlayer, vendor, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VendorFrame(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof VendorFrame frame) {
                frame.tick();
            }
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof VendorFrame vendor && !level.isClientSide && !player.isCreative()) {
            ItemStack stack = new ItemStack(this);
            boolean isOwner = vendor.blockOwner != null && vendor.blockOwner.equals(player.getName().getString());

            if (isOwner) {
                CompoundTag tag = te.saveWithoutMetadata();
                tag.putBoolean("Inf", false);
                stack.addTagElement("BlockEntityTag", tag);
            }

            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
            level.removeBlockEntity(pos);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}