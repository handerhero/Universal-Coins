 package aspadien.universalcoins.block;
import javax.annotation.Nullable;
 import net.minecraft.block.AbstractBlock;
 import net.minecraft.block.Block;
 import net.minecraft.block.BlockState;
 import net.minecraft.block.material.Material;
 import net.minecraft.entity.LivingEntity;
 import net.minecraft.entity.item.ItemEntity;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
 import net.minecraft.nbt.CompoundNBT;
 import net.minecraft.nbt.INBT;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Hand;
 import net.minecraft.util.Util;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.util.math.BlockRayTraceResult;
 import net.minecraft.util.text.TranslationTextComponent;
 import net.minecraft.world.IBlockReader;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkHooks;
import aspadien.universalcoins.tileentity.TileTradeStation;

 public class BlockTradeStation extends Block {
   public BlockTradeStation() {
/* 29 */     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 6000.0F));
   }


   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
/* 34 */     super.setPlacedBy(world, pos, state, placer, stack);
/* 35 */     if (!world.isClientSide && placer instanceof PlayerEntity) {
/* 36 */       TileEntity te = world.getBlockEntity(pos);
/* 37 */       if (te instanceof TileTradeStation) {
/* 38 */         TileTradeStation ts = (TileTradeStation)te;
/* 39 */         ts.blockOwner = placer.getUUID().toString();
/* 40 */         if (stack.hasCustomHoverName()) {
/* 41 */           ts.playerName = stack.getHoverName().getString();
         }
/* 43 */         ts.setChanged();
       }
     }
   }


   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/* 50 */     if (world.isClientSide) return ActionResultType.SUCCESS;

/* 52 */     TileEntity te = world.getBlockEntity(pos);
/* 53 */     if (te instanceof TileTradeStation) {
/* 54 */       TileTradeStation ts = (TileTradeStation)te;
/* 55 */       if (ts.inUse) {
/* 56 */         player.sendMessage(new TranslationTextComponent("chat.warning.inuse"), Util.NIL_UUID);
/* 57 */         return ActionResultType.SUCCESS;
       }


/* 61 */       if (ts.publicAccess || ts.blockOwner.equals(player.getUUID().toString())) {
/* 62 */         ts.playerName = player.getName().getString();
/* 63 */         ts.inUse = true;
/* 64 */         NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
/* 65 */         return ActionResultType.SUCCESS;
       }
/* 67 */       player.sendMessage(new TranslationTextComponent("chat.warning.private"), Util.NIL_UUID);
     }

/* 70 */     return ActionResultType.SUCCESS;
   }

   public boolean hasTileEntity(BlockState state) {
/* 74 */     return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/* 79 */     return new TileTradeStation();
   }


   public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
/* 84 */     TileEntity te = world.getBlockEntity(pos);
/* 85 */     if (te instanceof TileTradeStation && !world.isClientSide && !player.isCreative()) {
/* 86 */       ItemStack stack = new ItemStack(this);
/* 87 */       CompoundNBT tag = te.save(new CompoundNBT());
/* 88 */       stack.addTagElement("BlockEntityTag", (INBT)tag);


/* 91 */       ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
/* 92 */       world.addFreshEntity(entity);
     }
/* 94 */     super.playerWillDestroy(world, pos, state, player);
   }
 }

