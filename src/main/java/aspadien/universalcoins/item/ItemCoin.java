 package aspadien.universalcoins.item;

 import java.text.DecimalFormat;
 import java.util.List;
 import javax.annotation.Nullable;
 import aspadien.universalcoins.UniversalCoins;
 import net.minecraft.ChatFormatting;
 import net.minecraft.network.chat.Component;
 import net.minecraft.world.item.Item;
 import net.minecraft.world.item.ItemStack;
 import net.minecraft.world.item.TooltipFlag;
 import net.minecraft.world.level.Level;

 public class ItemCoin
   extends Item
 {
   private final int coinIndex;

   public ItemCoin(Item.Properties properties, int coinIndex) {
     super(properties);
     this.coinIndex = coinIndex;
   }

     public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
     super.appendHoverText(stack, worldIn, tooltip, flagIn);


     int valuePerItem = UniversalCoins.coinValues[this.coinIndex];
     long totalValue = (long) stack.getCount() * valuePerItem;

     DecimalFormat formatter = new DecimalFormat("###,###,###,###,###");
     String formattedValue = formatter.format(totalValue);


     String translationKey = (totalValue > 1L) ? "general.currency.multiple" : "general.currency.single";



     tooltip.add((Component.literal(formattedValue + " "))
         .append(Component.translatable(translationKey))
         .withStyle(ChatFormatting.GOLD));
   }
 }
