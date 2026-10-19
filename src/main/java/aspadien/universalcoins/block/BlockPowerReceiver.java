 package aspadien.universalcoins.block;

 import javax.annotation.Nullable;
 import net.minecraft.block.AbstractBlock;
 import net.minecraft.block.BlockState;
 import net.minecraft.block.material.Material;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.entity.player.ServerPlayerEntity;
 import net.minecraft.inventory.container.INamedContainerProvider;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Hand;
 import net.minecraft.util.Util;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.util.math.BlockRayTraceResult;
 import net.minecraft.util.text.ITextComponent;
 import net.minecraft.util.text.TranslationTextComponent;
 import net.minecraft.world.IBlockReader;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkHooks;
 import aspadien.universalcoins.tileentity.TilePowerReceiver;

 public class BlockPowerReceiver
   extends BlockProtected {
   public BlockPowerReceiver() {
     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 30.0F));
   }


   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
     TileEntity te = world.getBlockEntity(pos);
     if (te instanceof TilePowerReceiver) {
       TilePowerReceiver tile = (TilePowerReceiver)te;

       if (tile.publicAccess || player.getUUID().toString().equals(tile.blockOwner)) {
         if (!world.isClientSide) {
           NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
         }
         return ActionResultType.SUCCESS;
       }
       if (!world.isClientSide) {
         player.sendMessage(new TranslationTextComponent("chat.warning.private"), Util.NIL_UUID);
       }
     }

     return ActionResultType.SUCCESS;
   }


   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
     return new TilePowerReceiver();
   }
 }


