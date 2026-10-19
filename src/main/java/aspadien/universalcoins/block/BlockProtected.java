/*    */ package aspadien.universalcoins.block;
/*    */ import javax.annotation.Nullable;
/*    */ import net.minecraft.block.AbstractBlock;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.block.BlockState;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.LivingEntity;
/*    */ import net.minecraft.entity.item.ItemEntity;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */ import net.minecraft.nbt.INBT;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.IItemProvider;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.Explosion;
/*    */ import net.minecraft.world.World;
/*    */ import aspadien.universalcoins.tileentity.TileProtected;
/*    */ 
/*    */ public abstract class BlockProtected extends Block {
/*    */   public BlockProtected(AbstractBlock.Properties properties) {
/* 22 */     super(properties);
/*    */   }
/*    */ 
/*    */
/*    */   public void setPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
/* 27 */     if (!world.isClientSide && placer instanceof PlayerEntity) {
/* 28 */       TileEntity te = world.getBlockEntity(pos);
/* 29 */       if (te instanceof TileProtected) {
/* 30 */         TileProtected tp = (TileProtected)te;
/* 31 */         tp.blockOwner = placer.getUUID().toString();
/* 32 */         tp.playerName = placer.getName().getString();
/* 33 */         tp.setChanged();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
/* 40 */     if (!world.isClientSide) {
/* 41 */       dropBlockWithNBT(world, pos);
/*    */     }
/* 43 */     super.onBlockExploded(state, world, pos, explosion);
/*    */   }
/*    */ 
/*    */   
/*    */   public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
/* 48 */     if (!world.isClientSide && !player.isCreative()) {
/* 49 */       dropBlockWithNBT(world, pos);
/*    */     }
/* 51 */     super.playerWillDestroy(world, pos, state, player);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void dropBlockWithNBT(World world, BlockPos pos) {
/* 56 */     TileEntity te = world.getBlockEntity(pos);
/* 57 */     if (te instanceof TileProtected) {
/* 58 */       ItemStack stack = new ItemStack(this);
/* 59 */       CompoundNBT tag = te.save(new CompoundNBT());
/* 60 */       stack.addTagElement("BlockEntityTag", tag);
/* 61 */       world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack));
/* 62 */       world.removeBlockEntity(pos);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasTileEntity(BlockState state) {
/* 68 */     return true;
/*    */   }
/*    */ }

