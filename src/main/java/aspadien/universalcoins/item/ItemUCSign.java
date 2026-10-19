 package aspadien.universalcoins.item;
 import net.minecraft.block.BlockState;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.item.Item;
 import net.minecraft.item.ItemStack;
 import net.minecraft.item.ItemUseContext;
 import net.minecraft.state.properties.BlockStateProperties;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Direction;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.util.math.MathHelper;
 import net.minecraft.world.World;
 import aspadien.universalcoins.init.ModBlocks;
 import aspadien.universalcoins.tileentity.TileUCSign;

 public class ItemUCSign extends Item {
   public ItemUCSign(Item.Properties properties) {
     super(properties.stacksTo(16));
   }


   public ActionResultType useOn(ItemUseContext context) {
     World world = context.getLevel();
     BlockPos pos = context.getClickedPos();
     Direction facing = context.getClickedFace();
     PlayerEntity player = context.getPlayer();
     ItemStack stack = context.getItemInHand();

     if (player == null || facing == Direction.DOWN) {
       return ActionResultType.FAIL;
     }


     BlockState clickedState = world.getBlockState(pos);
     BlockPos placePos = clickedState.getMaterial().isReplaceable() ? pos : pos.relative(facing);


     if (!world.getBlockState(placePos).getMaterial().isReplaceable()) {
       return ActionResultType.FAIL;
     }

     if (!world.isClientSide) {
       BlockState stateToPlace;


       if (facing == Direction.UP) {


         int rotation = MathHelper.floor(((player.yRot + 180.0F) * 16.0F / 360.0F) + 0.5D) & 0xF;

         stateToPlace = ModBlocks.STANDING_UC_SIGN.get().defaultBlockState().setValue(BlockStateProperties.ROTATION_16, rotation);

       }
       else {

         stateToPlace = ModBlocks.WALL_UC_SIGN.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
       }


       if (stateToPlace.canSurvive(world, placePos)) {
         world.setBlock(placePos, stateToPlace, 3);


         if (!player.isCreative()) {
           stack.shrink(1);
         }


         TileEntity te = world.getBlockEntity(placePos);
         if (te instanceof TileUCSign) {
           ((TileUCSign)te).blockOwner = player.getName().getString();
           te.setChanged();
         }

         return ActionResultType.SUCCESS;
       }
       return ActionResultType.FAIL;
     }


     return ActionResultType.SUCCESS;
   }
 }