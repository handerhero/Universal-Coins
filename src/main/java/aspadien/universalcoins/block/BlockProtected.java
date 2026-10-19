package aspadien.universalcoins.block;

import aspadien.universalcoins.blockentities.Protected;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockProtected extends Block implements EntityBlock {

    public BlockProtected(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof Protected tp) {
                tp.blockOwner = player.getUUID().toString();
                tp.playerName = player.getName().getString();
                tp.setChanged();
            }
        }
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            dropBlockWithNBT(level, pos);
        }
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            dropBlockWithNBT(level, pos);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    protected void dropBlockWithNBT(Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof Protected) {
            ItemStack stack = new ItemStack(this);
            CompoundTag tag = te.saveWithoutMetadata();
            stack.addTagElement("BlockEntityTag", tag);
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
            level.removeBlockEntity(pos);
        }
    }
}