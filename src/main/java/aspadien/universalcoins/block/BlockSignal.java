package aspadien.universalcoins.block;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.blockentities.Signal;
import aspadien.universalcoins.init.ModItems;
import aspadien.universalcoins.util.CoinUtils;
import aspadien.universalcoins.util.UCUtils;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.network.NetworkHooks;

public class BlockSignal extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BlockSignal() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.METAL)
                .strength(3.0F, 30.0F));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof Signal tile)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                if (player.getUUID().toString().equals(tile.blockOwner) || player.isCreative()) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        NetworkHooks.openScreen(serverPlayer, tile, pos);
                    }
                } else {
                    player.sendSystemMessage(Component.translatable("chat.warning.private"));
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        int feeNeeded = tile.fee;
        boolean paid = false;

        ItemStack heldMain = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack heldOff = player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack cardToUse = ItemStack.EMPTY;

        if (UCUtils.getCardBalance(heldMain) >= feeNeeded) {
            cardToUse = heldMain;
        } else if (UCUtils.getCardBalance(heldOff) >= feeNeeded) {
            cardToUse = heldOff;
        }

        if (!cardToUse.isEmpty() && UCUtils.debitCard(cardToUse, feeNeeded)) {
            paid = true;
        }

        if (!paid) {
            int totalCoins = 0;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                int val = CoinUtils.getCoinValue(player.getInventory().getItem(i));
                if (val > 0) totalCoins += val * player.getInventory().getItem(i).getCount();
            }

            if (totalCoins >= feeNeeded) {
                int remaining = feeNeeded;
                for (int j = 0; j < player.getInventory().getContainerSize(); j++) {
                    ItemStack stack = player.getInventory().getItem(j);
                    int val = CoinUtils.getCoinValue(stack);
                    if (val > 0) {
                        int stackVal = val * stack.getCount();
                        if (stackVal <= remaining) {
                            remaining -= stackVal;
                            player.getInventory().setItem(j, ItemStack.EMPTY);
                        } else {
                            int toTake = (int) Math.ceil((double) remaining / val);
                            int change = toTake * val - remaining;
                            stack.shrink(toTake);
                            if (change > 0) giveChange(level, player, change);
                            remaining = 0;
                        }
                    }
                    if (remaining <= 0) break;
                }
                paid = true;
            }
        }

        if (paid) {
            completeActivation(player, tile);
        } else {
            player.sendSystemMessage(Component.translatable("signal.message.notenough").withStyle(ChatFormatting.RED));
        }

        return InteractionResult.CONSUME;
    }

    private void completeActivation(Player player, Signal tile) {
        player.sendSystemMessage(Component.translatable("signal.message.activated").withStyle(ChatFormatting.GREEN));
        tile.activateSignal();
        tile.setChanged();
        tile.updateTE();
    }

    private void giveChange(Level level, Player player, int amount) {
        int remaining = amount;
        for (int i = 4; i >= 0; i--) {
            int coinVal = UniversalCoins.coinValues[i];
            if (remaining >= coinVal) {
                int count = remaining / coinVal;
                ItemStack stack = new ItemStack(ModItems.getCoinByIndex(i), count);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                remaining %= coinVal;
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof Signal signal) {
                signal.blockOwner = player.getUUID().toString();
            }
        }
    }

    @Override
    public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof Signal signal) {
            return signal.canProvidePower ? 15 : 0;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Signal(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, p, st, be) -> {
            if (be instanceof Signal signal) {
                signal.tick();
            }
        };
    }
}