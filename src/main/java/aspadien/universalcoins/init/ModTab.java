package aspadien.universalcoins.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;


public class ModTab {
  public static final ItemGroup UNIVERSAL_COINS_GROUP = new ItemGroup(MOD_ID)
    {
      public ItemStack makeIcon() {
        return new ItemStack(ModItems.GOLD_COIN.get());
      }
    };
}
