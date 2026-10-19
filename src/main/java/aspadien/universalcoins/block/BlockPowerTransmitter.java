/*    */ package aspadien.universalcoins.block;
/*    */ 
/*    */ import net.minecraft.block.AbstractBlock;
/*    */ import net.minecraft.block.BlockState;
/*    */ import net.minecraft.block.material.Material;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.inventory.container.INamedContainerProvider;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.ActionResultType;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.Util;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.math.BlockRayTraceResult;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import net.minecraft.world.IBlockReader;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkHooks;
/*    */ import aspadien.universalcoins.tileentity.TilePowerTransmitter;
/*    */ 
/*    */ public class BlockPowerTransmitter extends BlockProtected {
/*    */   public BlockPowerTransmitter() {
/* 24 */     super(AbstractBlock.Properties.of(Material.METAL).strength(3.0F, 30.0F));
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
/* 29 */     TileEntity te = world.getBlockEntity(pos);
/* 30 */     if (te instanceof TilePowerTransmitter) {
/* 31 */       TilePowerTransmitter tile = (TilePowerTransmitter)te;
/* 32 */       if (tile.publicAccess || player.getUUID().toString().equals(tile.blockOwner)) {
/* 33 */         if (!world.isClientSide) {
/* 34 */           NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider)te, pos);
/*    */         }
/* 36 */         return ActionResultType.SUCCESS;
/*    */       } 
/* 38 */       if (!world.isClientSide) player.sendMessage((ITextComponent)new TranslationTextComponent("chat.warning.private"), Util.NIL_UUID);
/*    */     
/*    */     } 
/* 41 */     return ActionResultType.SUCCESS;
/*    */   }
/*    */ 
/*    */   
/*    */   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
/* 46 */     return (TileEntity)new TilePowerTransmitter();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/block/BlockPowerTransmitter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */