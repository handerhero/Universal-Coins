package aspadien.universalcoins.block;

import aspadien.universalcoins.blockentities.UCSign;
import aspadien.universalcoins.gui.UCSignEditScreen;
import aspadien.universalcoins.init.ModItems;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockUCWallSign extends WallSignBlock {

    public BlockUCWallSign() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.WOOD)
                .noCollission()
                .strength(1.0F), WoodType.OAK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new UCSign(pos, state);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof UCSign sign) {
            sign.scanChestContents(state, fromPos);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof UCSign sign) {
            sign.scanChestContents(state, null);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof UCSign sign) {
            if (player.getName().getString().equals(sign.blockOwner) || player.isCreative()) {
                if (level.isClientSide) {
                    openSignGui(sign);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof UCSign sign && !player.isCreative()) {
            if (!player.getName().getString().equals(sign.blockOwner)) {
                return;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return (facing == state.getValue(FACING).getOpposite() && !state.canSurvive(level, currentPos))
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @OnlyIn(Dist.CLIENT)
    private void openSignGui(UCSign te) {
        Minecraft.getInstance().setScreen(new UCSignEditScreen(te));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.UC_SIGN.get());
    }
}