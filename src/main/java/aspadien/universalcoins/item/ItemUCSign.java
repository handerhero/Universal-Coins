package aspadien.universalcoins.item;

import aspadien.universalcoins.blockentities.UCSign;
import aspadien.universalcoins.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ItemUCSign extends Item {
    public ItemUCSign(Item.Properties properties) {
        super(properties.stacksTo(16));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null || facing == Direction.DOWN) {
            return InteractionResult.FAIL;
        }

        BlockState clickedState = world.getBlockState(pos);
        BlockPos placePos = clickedState.canBeReplaced() ? pos : pos.relative(facing);

        if (!world.getBlockState(placePos).canBeReplaced()) {
            return InteractionResult.FAIL;
        }

        if (!world.isClientSide) {
            BlockState stateToPlace;

            if (facing == Direction.UP) {
                int rotation = Mth.floor(((player.getYRot() + 180.0F) * 16.0F / 360.0F) + 0.5D) & 0xF;
                stateToPlace = ModBlocks.STANDING_UC_SIGN.get().defaultBlockState().setValue(BlockStateProperties.ROTATION_16, rotation);
            } else {
                stateToPlace = ModBlocks.WALL_UC_SIGN.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
            }

            if (stateToPlace.canSurvive(world, placePos)) {
                world.setBlock(placePos, stateToPlace, 3);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                BlockEntity te = world.getBlockEntity(placePos);
                if (te instanceof UCSign ucSign) {
                    ucSign.blockOwner = player.getName().getString();
                    ucSign.setChanged();
                }

                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}