package aspadien.universalcoins.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.init.ModItems;

public class CoinUtils {
    public static int getCoinValue(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }

        Item item = stack.getItem();

        if (item == ModItems.IRON_COIN.get()) return UniversalCoins.coinValues[0];
        if (item == ModItems.GOLD_COIN.get()) return UniversalCoins.coinValues[1];
        if (item == ModItems.EMERALD_COIN.get()) return UniversalCoins.coinValues[2];
        if (item == ModItems.DIAMOND_COIN.get()) return UniversalCoins.coinValues[3];
        if (item == ModItems.OBSIDIAN_COIN.get()) return UniversalCoins.coinValues[4];
        if (item == ModItems.NETHERITE_COIN.get()) return UniversalCoins.coinValues[5];

        return 0;
    }
}