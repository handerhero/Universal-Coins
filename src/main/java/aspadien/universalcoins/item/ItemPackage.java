/*    */ package aspadien.universalcoins.item;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ import net.minecraft.client.util.ITooltipFlag;
/*    */ import net.minecraft.entity.Entity;
/*    */ import net.minecraft.entity.item.ItemEntity;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.item.Item;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.item.ItemUseContext;
/*    */ import net.minecraft.nbt.ListNBT;
/*    */ import net.minecraft.util.ActionResult;
/*    */ import net.minecraft.util.ActionResultType;
/*    */ import net.minecraft.util.Hand;
/*    */ import net.minecraft.util.math.vector.Vector3d;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.util.text.TextFormatting;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class ItemPackage
/*    */   extends Item {
/*    */   public ItemPackage(Item.Properties properties) {
/* 27 */     super(properties.stacksTo(1));
/*    */   }
/*    */ 
/*    */   
/*    */   public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
/* 32 */     if (stack.hasTag() && stack.getTag().contains("Inventory", 9)) {
/* 33 */       ListNBT tagList = stack.getTag().getList("Inventory", 10);
/* 34 */       for (int i = 0; i < tagList.size(); i++) {
/* 35 */         ItemStack innerStack = ItemStack.of(tagList.getCompound(i));
/* 36 */         if (!innerStack.isEmpty()) {
/* 37 */           tooltip.add((new StringTextComponent(innerStack.getCount() + " x "))
/* 38 */               .append(innerStack.getHoverName())
/* 39 */               .withStyle(TextFormatting.GRAY));
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
/* 47 */     ItemStack heldItem = player.getItemInHand(hand);
/*    */     
/* 49 */     if (!world.isClientSide && 
/* 50 */       heldItem.hasTag() && heldItem.getTag().contains("Inventory", 9)) {
/* 51 */       ListNBT tagList = heldItem.getTag().getList("Inventory", 10);
/*    */ 
/*    */       
/* 54 */       List<ItemStack> toSpawn = new ArrayList<>();
/* 55 */       for (int i = 0; i < tagList.size(); i++) {
/* 56 */         ItemStack s = ItemStack.of(tagList.getCompound(i));
/* 57 */         if (!s.isEmpty()) {
/* 58 */           toSpawn.add(s.copy());
/*    */         }
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 64 */       heldItem.setCount(0);
/* 65 */       player.setItemInHand(hand, ItemStack.EMPTY);
/*    */ 
/*    */       
/* 68 */       for (ItemStack stack : toSpawn) {
/*    */         
/* 70 */         ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
/*    */ 
/*    */         
/* 73 */         itemEntity.setNoPickUpDelay();
/*    */ 
/*    */         
/* 76 */         itemEntity.setDeltaMovement(Vector3d.ZERO);
/*    */         
/* 78 */         world.addFreshEntity((Entity)itemEntity);
/*    */       } 
/*    */ 
/*    */       
/* 82 */       if (player instanceof ServerPlayerEntity) {
/* 83 */         ((ServerPlayerEntity)player).containerMenu.broadcastChanges();
/*    */       }
/*    */ 
/*    */       
/* 87 */       return ActionResult.consume(ItemStack.EMPTY);
/*    */     } 
/*    */ 
/*    */     
/* 91 */     return ActionResult.sidedSuccess(heldItem, world.isClientSide());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ActionResultType useOn(ItemUseContext context) {
/* 98 */     return ActionResultType.PASS;
/*    */   }
/*    */ }