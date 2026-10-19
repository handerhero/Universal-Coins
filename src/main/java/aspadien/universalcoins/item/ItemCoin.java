 package aspadien.universalcoins.item;

 import java.text.DecimalFormat;
 import java.util.List;
 import javax.annotation.Nullable;
 import net.minecraft.client.util.ITooltipFlag;
 import net.minecraft.item.Item;
 import net.minecraft.item.ItemStack;
 import net.minecraft.util.text.ITextComponent;
 import net.minecraft.util.text.StringTextComponent;
 import net.minecraft.util.text.TextFormatting;
 import net.minecraft.util.text.TranslationTextComponent;
 import net.minecraft.world.World;
 import aspadien.universalcoins.UniversalCoins;

 public class ItemCoin
   extends Item
 {
   private final int coinIndex;

   public ItemCoin(Item.Properties properties, int coinIndex) {
     super(properties);
     this.coinIndex = coinIndex;
   }


   public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
     super.appendHoverText(stack, worldIn, tooltip, flagIn);


     int valuePerItem = UniversalCoins.coinValues[this.coinIndex];
     long totalValue = (long) stack.getCount() * valuePerItem;

     DecimalFormat formatter = new DecimalFormat("###,###,###,###,###");
     String formattedValue = formatter.format(totalValue);


     String translationKey = (totalValue > 1L) ? "general.currency.multiple" : "general.currency.single";



     tooltip.add((new StringTextComponent(formattedValue + " "))
         .append(new TranslationTextComponent(translationKey))
         .withStyle(TextFormatting.GOLD));
   }
 }
