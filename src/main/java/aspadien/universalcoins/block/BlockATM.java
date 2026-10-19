 package aspadien.universalcoins.block;
 import javax.annotation.Nullable;
 import net.minecraft.block.AbstractBlock;
 import net.minecraft.block.Block;
 import net.minecraft.block.BlockState;
 import net.minecraft.block.HorizontalBlock;
 import net.minecraft.block.material.Material;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
 import net.minecraft.item.BlockItemUseContext;
 import net.minecraft.state.DirectionProperty;
 import net.minecraft.state.Property;
 import net.minecraft.state.StateContainer;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Direction;
 import net.minecraft.util.Hand;
 import net.minecraft.util.Mirror;
 import net.minecraft.util.Rotation;
 import net.minecraft.util.Util;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.util.math.BlockRayTraceResult;
 import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkHooks;
 import aspadien.universalcoins.tileentity.TileATM;

 public class BlockATM extends Block {
/* 29 */   public static final DirectionProperty FACING = HorizontalBlock.FACING;

   public BlockATM() {
/* 32 */     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 30.0F).lightLevel(state -> 8));
/* 33 */     registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
   }


   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
/* 39 */     return defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
   }


   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/* 44 */     if (!world.isClientSide) {
/* 45 */       TileEntity tileEntity = world.getBlockEntity(pos);
/* 46 */       if (tileEntity instanceof TileATM) {
/* 47 */         TileATM atm = (TileATM)tileEntity;
/* 48 */         if (atm.inUse) {
/* 49 */           player.sendMessage((ITextComponent)new TranslationTextComponent("chat.warning.inuse"), Util.NIL_UUID);
         } else {
/* 51 */           atm.playerName = player.getName().getString();
/* 52 */           atm.playerUID = player.getUUID().toString();
/* 53 */           atm.setChanged();
/* 54 */           atm.updateTE();
/* 55 */           atm.inUse = true;


/* 58 */           NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)tileEntity, pos);
/* 59 */           atm.setChanged();
         }
       }
     }
/* 63 */     return ActionResultType.SUCCESS;
   }


   @Override
   public BlockState rotate(BlockState state, Rotation rot) {
    return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
   }



   public BlockState mirror(BlockState state, Mirror mirrorIn) {
/* 75 */     return state.rotate(mirrorIn.getRotation((Direction)state.getValue((Property)FACING)));
   }

   public boolean hasTileEntity(BlockState state) {
/* 79 */     return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/* 84 */     return new TileATM();
   }


   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
/* 89 */     builder.add(FACING);
   }
 }
