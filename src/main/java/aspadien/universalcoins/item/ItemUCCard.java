 package aspadien.universalcoins.item;
 import java.text.DecimalFormat;
 import java.util.List;
 import javax.annotation.Nullable;
 import net.minecraft.client.util.ITooltipFlag;
 import net.minecraft.entity.player.PlayerEntity;
 import net.minecraft.item.Item;
 import net.minecraft.item.ItemStack;
 import net.minecraft.item.ItemUseContext;
 import net.minecraft.nbt.CompoundNBT;
 import net.minecraft.util.ActionResultType;
 import net.minecraft.util.Util;
 import net.minecraft.util.text.IFormattableTextComponent;
 import net.minecraft.util.text.ITextComponent;
 import net.minecraft.util.text.StringTextComponent;
 import net.minecraft.util.text.TextFormatting;
 import net.minecraft.util.text.TranslationTextComponent;
 import net.minecraft.world.World;
 import aspadien.universalcoins.util.UniversalAccounts;

 public class ItemUCCard extends Item {
   public ItemUCCard(Item.Properties properties) {
     super(properties.stacksTo(1));
   }


   public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
     if (stack.hasTag()) {
       CompoundNBT tag = stack.getTag();

       tooltip.add((new StringTextComponent(tag.getString("Name"))).withStyle(TextFormatting.GRAY));
       tooltip.add((new StringTextComponent(tag.getString("Account"))).withStyle(TextFormatting.AQUA));
     } else {

       tooltip.add((new TranslationTextComponent("item.card.warning")).withStyle(TextFormatting.RED));
     }
   }


   public ActionResultType useOn(ItemUseContext context) {
     World world = context.getLevel();
     PlayerEntity player = context.getPlayer();
     ItemStack stack = context.getItemInHand();

     if (world.isClientSide || player == null) {
       return ActionResultType.SUCCESS;
     }


     if (!stack.hasTag()) {
       createNBT(stack, world, player);
     }


     String accountId = stack.getTag().getString("Account");
     long accountCoins = UniversalAccounts.getInstance().getAccountBalance(accountId);

     DecimalFormat formatter = new DecimalFormat("###,###,###,###,###,###,###");



     IFormattableTextComponent iFormattableTextComponent = (new TranslationTextComponent("item.card.balance")).append((new StringTextComponent(formatter.format(accountCoins))).withStyle(TextFormatting.GOLD));

     player.sendMessage(iFormattableTextComponent, Util.NIL_UUID);

     return ActionResultType.SUCCESS;
   }



   public void onCraftedBy(ItemStack stack, World world, PlayerEntity player) {
     if (!world.isClientSide) {
       createNBT(stack, world, player);
     }
   }



   protected void createNBT(ItemStack stack, World world, PlayerEntity player) {
     String accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(player.getUUID().toString());

     CompoundNBT tag = stack.getOrCreateTag();
     tag.putString("Name", player.getName().getString());
     tag.putString("Owner", player.getUUID().toString());
     tag.putString("Account", accountNumber);
   }
 }