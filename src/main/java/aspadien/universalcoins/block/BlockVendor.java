package aspadien.universalcoins.block;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.blockentities.VendorBlock;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class BlockVendor extends Block implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0D, 2.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    public BlockVendor() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(0.3F, 6000.0F)
                .noOcclusion());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof Vendor vendor) {
                vendor.blockOwner = player.getUUID().toString();
                vendor.setChanged();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof Vendor vendor) {
            if (vendor.inUse) {
                if (!level.isClientSide) {
                    player.sendSystemMessage(Component.translatable("chat.warning.inuse"));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                vendor.playerName = player.getName().getString();
                vendor.inUse = true;
                vendor.setChanged();
                NetworkHooks.openScreen(serverPlayer, vendor, pos);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VendorBlock(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof Vendor vendor) {
                vendor.tick();
            }
        };
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof Vendor vendor && !level.isClientSide && !player.isCreative()) {
            ItemStack stack = new ItemStack(this);
            boolean isOwner = vendor.blockOwner != null && vendor.blockOwner.equals(player.getUUID().toString());

            if (isOwner) {
                CompoundTag tag = te.saveWithoutMetadata();
                tag.putBoolean("Inf", false);
                stack.addTagElement("BlockEntityTag", tag);
            }

            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            level.addFreshEntity(itemEntity);
        }
        super.playerWillDestroy(level, pos, state, player);
    }
}