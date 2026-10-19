package aspadien.universalcoins.init;

import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public class UCConfig {
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec COMMON_SPEC;

  public static final ForgeConfigSpec.ConfigValue<List<? extends Integer>> COIN_VALUES;

  public static final ForgeConfigSpec.IntValue SMALL_PACKAGE_PRICE;

  public static final ForgeConfigSpec.IntValue MED_PACKAGE_PRICE;

  public static final ForgeConfigSpec.IntValue LARGE_PACKAGE_PRICE;


  static {
    BUILDER.push("General");

    COIN_VALUES = BUILDER.comment("Coin values (Iron, Gold, Emerald, Diamond, Obsidian, Netherite)").defineList("coinValues", Arrays.asList(1, 10, 100, 1000, 10000, 100000), obj -> obj instanceof Integer);
  }
  public static final ForgeConfigSpec.DoubleValue ITEM_SELL_RATIO = BUILDER.defineInRange("itemSellRatio", 0.8D, 0.1D, 1.0D);
    public static final ForgeConfigSpec.IntValue RF_WHOLESALE_RATE; public static final ForgeConfigSpec.IntValue RF_RETAIL_RATE;
    public static final ForgeConfigSpec.BooleanValue TRADE_STATION_BUY_ENABLED = BUILDER.comment("Enable buying items from Trade Station")
    .define("tradeStationBuyEnabled", true);
  public static final ForgeConfigSpec.BooleanValue AUTO_MODE_ENABLED = BUILDER.comment("Enable automation features")
    .define("autoModeEnabled", true); static {
    BUILDER.pop();

    BUILDER.push("Packager");
    SMALL_PACKAGE_PRICE = BUILDER.defineInRange("smallPrice", 100, 1, 1000000);
    MED_PACKAGE_PRICE = BUILDER.defineInRange("medPrice", 500, 1, 1000000);
    LARGE_PACKAGE_PRICE = BUILDER.defineInRange("largePrice", 4000, 1, 1000000);
    BUILDER.pop();

    BUILDER.push("Energy");

    RF_WHOLESALE_RATE = BUILDER.defineInRange("rfWholesale", 12, 0, 2147483647);
    RF_RETAIL_RATE = BUILDER.defineInRange("rfRetail", 16, 0, 2147483647);
    BUILDER.pop();

    COMMON_SPEC = BUILDER.build();
  }
}
