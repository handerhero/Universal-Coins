/*    */ package aspadien.universalcoins.item;
/*    */ 
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ import net.minecraft.client.util.ITooltipFlag;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.item.ItemUseContext;
/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.ActionResultType;
/*    */ import net.minecraft.util.Util;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.TextFormatting;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import net.minecraft.world.World;
/*    */ import aspadien.universalcoins.tileentity.TileVendor;
/*    */ 
/*    */ 
/*    */ public class ItemLinkCard
/*    */   extends Item
/*    */ {
/*    */   public ItemLinkCard(Item.Properties properties) {
/* 26 */     super(properties.stacksTo(1));
/*    */   }
/*    */ 
/*    */   
/*    */   public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
/* 31 */     if (stack.hasTag() && stack.getTag().contains("storageLocation")) {
/* 32 */       tooltip.add((new TranslationTextComponent("item.link_card.stored")).withStyle(TextFormatting.GREEN));
/*    */     } else {
/* 34 */       tooltip.add((new TranslationTextComponent("item.link_card.blank")).withStyle(TextFormatting.GRAY));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionResultType useOn(ItemUseContext context) {
/* 40 */     World world = context.getLevel();
/* 41 */     BlockPos pos = context.getClickedPos();
/* 42 */     PlayerEntity player = context.getPlayer();
/* 43 */     ItemStack stack = context.getItemInHand();
/*    */     
/* 45 */     if (world.isClientSide || player == null) return ActionResultType.SUCCESS;
/*    */     
/* 47 */     TileEntity tile = world.getBlockEntity(pos);
/*    */ 
/*    */     
/* 50 */     if (tile instanceof net.minecraft.tileentity.ChestTileEntity) {
/* 51 */       CompoundNBT nbt = stack.getOrCreateTag();
/* 52 */       nbt.putIntArray("storageLocation", new int[] { pos.getX(), pos.getY(), pos.getZ() });
/*    */       
/* 54 */       player.sendMessage((ITextComponent)(new TranslationTextComponent("item.link_card.message.stored"))
/* 55 */           .append(" " + pos.getX() + " " + pos.getY() + " " + pos.getZ())
/* 56 */           .withStyle(TextFormatting.AQUA), Util.NIL_UUID);
/* 57 */       return ActionResultType.SUCCESS;
/*    */     } 
/*    */ 
/*    */     
/* 61 */     if (tile instanceof TileVendor) {
/* 62 */       TileVendor vendor = (TileVendor)tile;
/* 63 */       if (stack.hasTag() && stack.getTag().contains("storageLocation")) {
/* 64 */         int[] coords = stack.getTag().getIntArray("storageLocation");
/* 65 */         vendor.setRemoteStorage(coords);
/*    */         
/* 67 */         player.sendMessage((ITextComponent)(new TranslationTextComponent("item.link_card.message.set"))
/* 68 */             .withStyle(TextFormatting.GOLD), Util.NIL_UUID);
/*    */         
/* 70 */         stack.shrink(1);
/*    */       } 
/* 72 */       return ActionResultType.SUCCESS;
/*    */     } 
/*    */     
/* 75 */     return ActionResultType.PASS;
/*    */   }
/*    */ }