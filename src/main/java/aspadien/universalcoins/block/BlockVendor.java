/*     */ package aspadien.universalcoins.block;
/*     */ import javax.annotation.Nullable;
/*     */ import net.minecraft.block.AbstractBlock;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.block.BlockState;
/*     */ import net.minecraft.block.material.Material;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.LivingEntity;
/*     */ import net.minecraft.entity.item.ItemEntity;
/*     */ import net.minecraft.entity.player.PlayerEntity;
/*     */ import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.nbt.CompoundNBT;
/*     */ import net.minecraft.nbt.INBT;
/*     */ import net.minecraft.tileentity.TileEntity;
/*     */ import net.minecraft.util.ActionResultType;
/*     */ import net.minecraft.util.Hand;
/*     */ import net.minecraft.util.IItemProvider;
/*     */ import net.minecraft.util.Util;
/*     */ import net.minecraft.util.math.BlockPos;
/*     */ import net.minecraft.util.math.BlockRayTraceResult;
/*     */ import net.minecraft.util.math.shapes.ISelectionContext;
/*     */ import net.minecraft.util.math.shapes.VoxelShape;
/*     */ import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
/*     */ import net.minecraft.world.World;
/*     */ import net.minecraftforge.fml.network.NetworkHooks;
import aspadien.universalcoins.tileentity.TileVendor;
/*     */ import aspadien.universalcoins.tileentity.TileVendorBlock;
/*     */ 
/*     */ public class BlockVendor extends Block {
    /*  29 */   protected static final VoxelShape SHAPE = Block.box(1.0D, 2.0D, 1.0D, 15.0D, 15.0D, 15.0D);

    /*     */
    /*     */
    public BlockVendor() {
        /*  32 */
        super(AbstractBlock.Properties.of(Material.METAL)
/*  33 */.strength(0.3F, 6000.0F)
/*  34 */.noOcclusion());
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        /*  39 */
        return SHAPE;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        /*  44 */
        super.setPlacedBy(world, pos, state, placer, stack);
        /*  45 */
        if (!world.isClientSide && placer instanceof PlayerEntity) {
            /*  46 */
            TileEntity te = world.getBlockEntity(pos);
            /*  47 */
            if (te instanceof TileVendor) {
                /*     */
                /*  49 */
                ((TileVendor) te).blockOwner = placer.getUUID().toString();
                /*  50 */
                te.setChanged();
                /*     */
            }
            /*     */
        }
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        /*  57 */
        TileEntity tileEntity = world.getBlockEntity(pos);
        /*  58 */
        if (tileEntity instanceof TileVendor) {
            /*  59 */
            TileVendor vendor = (TileVendor) tileEntity;
            /*  60 */
            if (vendor.inUse) {
                /*  61 */
                if (!world.isClientSide) {
                    /*  62 */
                    player.sendMessage((ITextComponent) new TranslationTextComponent("chat.warning.inuse"), Util.NIL_UUID);
                    /*     */
                }
                /*  64 */
                return ActionResultType.SUCCESS;
                /*     */
            }
            /*  66 */
            if (!world.isClientSide) {
                /*  67 */
                vendor.playerName = player.getName().getString();
                /*  68 */
                vendor.inUse = true;
                /*  69 */
                vendor.setChanged();
                /*     */
                /*  71 */
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) vendor, pos);
                /*     */
            }
            /*  73 */
            return ActionResultType.SUCCESS;
            /*     */
        }
        /*     */
        /*  76 */
        return ActionResultType.PASS;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public boolean hasTileEntity(BlockState state) {
        /*  81 */
        return true;
        /*     */
    }

    /*     */
    /*     */
    /*     */
    @Nullable
    /*     */ public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        /*  87 */
        return (TileEntity) new TileVendorBlock();
        /*     */
    }

    /*     */
    /*     */
    /*     */
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof TileVendor && !world.isClientSide && !player.isCreative()) {
            TileVendor vendor = (TileVendor) te;
            ItemStack stack = new ItemStack((IItemProvider) this);

            // Проверяем, является ли ломающий блок его владельцем
            boolean isOwner = vendor.blockOwner != null && vendor.blockOwner.equals(player.getUUID().toString());

            if (isOwner) {
                CompoundNBT tag = te.save(new CompoundNBT());
                // Обязательно отключаем безлимитный режим перед тем, как отдать предмет игроку
                tag.putBoolean("Inf", false);
                stack.addTagElement("BlockEntityTag", (INBT) tag);
            }
            // Если ломает НЕ владелец (или это сгенерированный шоп), NBT не записывается.
            // Игрок получит просто пустой блок магазина, без содержимого и без читов.

            ItemEntity itementity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
            world.addFreshEntity((Entity) itementity);
        }
        super.playerWillDestroy(world, pos, state, player);
    }
}


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockVendor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */