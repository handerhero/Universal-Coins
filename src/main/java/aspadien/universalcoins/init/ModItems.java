 package aspadien.universalcoins.init;
 import net.minecraft.world.item.BlockItem;
 import net.minecraft.world.item.Item;
 import net.minecraft.world.level.block.Block;
 import net.minecraftforge.eventbus.api.IEventBus;
 import net.minecraftforge.registries.DeferredRegister;
 import net.minecraftforge.registries.ForgeRegistries;
import aspadien.universalcoins.item.*;
 import net.minecraftforge.registries.RegistryObject;

 import static aspadien.universalcoins.UniversalCoins.MOD_ID;

 public class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

  public static final RegistryObject<Item> IRON_COIN = ITEMS.register("iron_coin", () -> new ItemCoin((new Item.Properties()), 0));

  public static final RegistryObject<Item> GOLD_COIN = ITEMS.register("gold_coin", () -> new ItemCoin((new Item.Properties()), 1));

  public static final RegistryObject<Item> EMERALD_COIN = ITEMS.register("emerald_coin", () -> new ItemCoin((new Item.Properties()), 2));

  public static final RegistryObject<Item> DIAMOND_COIN = ITEMS.register("diamond_coin", () -> new ItemCoin((new Item.Properties()), 3));

  public static final RegistryObject<Item> OBSIDIAN_COIN = ITEMS.register("obsidian_coin", () -> new ItemCoin((new Item.Properties()), 4));

  public static final RegistryObject<Item> NETHERITE_COIN = ITEMS.register("netherite_coin", () -> new ItemCoin((new Item.Properties()), 5));

  public static final RegistryObject<Item> UC_CARD = ITEMS.register("uc_card", () -> new ItemUCCard((new Item.Properties())));

  public static final RegistryObject<Item> ENDER_CARD = ITEMS.register("ender_card", () -> new ItemEnderCard((new Item.Properties())));

  public static final RegistryObject<Item> LINK_CARD = ITEMS.register("link_card", () -> new ItemLinkCard((new Item.Properties())));

  public static final RegistryObject<Item> UC_PACKAGE = ITEMS.register("uc_package", () -> new ItemPackage((new Item.Properties())));

  public static final RegistryObject<Item> UC_SIGN = ITEMS.register("uc_sign", () -> new ItemUCSign((new Item.Properties())));

  public static final RegistryObject<Item> VENDOR_WRENCH = ITEMS.register("vendor_wrench", () -> new ItemVendorWrench((new Item.Properties())));

  public static final RegistryObject<Item> CATALOG = ITEMS.register("catalog", () -> new Item((new Item.Properties()).stacksTo(1)));

  public static final RegistryObject<BlockItem> ATM = ITEMS.register("atm", () -> new BlockItem(ModBlocks.ATM.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> TRADE_STATION = ITEMS.register("tradestation", () -> new BlockItem(ModBlocks.TRADE_STATION.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> SAFE = ITEMS.register("safe", () -> new BlockItem(ModBlocks.SAFE.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> PACKAGER = ITEMS.register("packager", () -> new BlockItem(ModBlocks.PACKAGER.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> VENDOR = ITEMS.register("vendor_block", () -> new BlockItem(ModBlocks.VENDOR.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> VENDOR_FRAME = ITEMS.register("vendor_frame", () -> new BlockItem(ModBlocks.VENDOR_FRAME.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> POWER_TRANSMITTER = ITEMS.register("power_transmitter", () -> new BlockItem(ModBlocks.POWER_TRANSMITTER.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> POWER_RECEIVER = ITEMS.register("power_receiver", () -> new BlockItem(ModBlocks.POWER_RECEIVER.get(), (new Item.Properties())));

  public static final RegistryObject<BlockItem> SIGNAL_BLOCK = ITEMS.register("signalblock", () -> new BlockItem(ModBlocks.SIGNAL.get(), (new Item.Properties())));

   public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
   }

  public static ItemCoin getCoinByIndex(int index) {
    switch (index) {
      case 0: return (ItemCoin)IRON_COIN.get();
      case 1: return (ItemCoin)GOLD_COIN.get();
      case 2: return (ItemCoin)EMERALD_COIN.get();
      case 3: return (ItemCoin)DIAMOND_COIN.get();
      case 4: return (ItemCoin)OBSIDIAN_COIN.get();
      case 5: return (ItemCoin)NETHERITE_COIN.get(); }
    return (ItemCoin)IRON_COIN.get();
  }
}
