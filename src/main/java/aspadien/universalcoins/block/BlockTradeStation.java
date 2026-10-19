package aspadien.universalcoins.block;

import aspadien.universalcoins.blockentities.TradeStation;
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
import net.minecraftforge.network.NetworkHooks;

public class BlockTradeStation extends Block implements EntityBlock {

    public BlockTradeStation() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(3.0F, 6000.0F));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TradeStation ts) {
                ts.blockOwner = player.getUUID().toString();
                if (stack.hasCustomHoverName()) {
                    ts.playerName = stack.getHoverName().getString();
                }
                ts.setChanged();
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TradeStation ts) {
            if (ts.inUse) {
                player.sendSystemMessage(Component.translatable("chat.warning.inuse"));
                return InteractionResult.SUCCESS;
            }

            if (ts.publicAccess || ts.blockOwner.equals(player.getUUID().toString())) {
                ts.playerName = player.getName().getString();
                ts.inUse = true;
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, ts, pos);
                }
                return InteractionResult.SUCCESS;
            }
            player.sendSystemMessage(Component.translatable("chat.warning.private"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TradeStation && !level.isClientSide && !player.isCreative()) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = te.saveWithoutMetadata();
            stack.addTagElement("BlockEntityTag", tag);

            ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            level.addFreshEntity(entity);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TradeStation(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof TradeStation ts) {
                ts.tick();
            }
        };
    }
}