package aspadien.universalcoins;
import aspadien.universalcoins.net.UCAccountBalanceTrigger;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import aspadien.universalcoins.init.*;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.util.UCItemPricer;



 @Mod("universalcoins")
 public class UniversalCoins {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_ID = "universalcoins";
  public static int[] coinValues = new int[] { 1, 10, 100, 1000, 10000, 100000 };
  public static int smallPackagePrice; public static int medPackagePrice; public static int largePackagePrice;
  public static int rfWholesaleRate;
  public static int rfRetailRate;
  public static double itemSellRatio;
  public static boolean tradeStationBuyEnabled;
  public static boolean autoModeEnabled;
  public static int bankGenWeight = 2;
  public static int tradeGenWeight = 1;
  public static int shopGenWeight = 3;
  public static int shopMaxPrice = 140;
  public static int shopMinPrice = 100;

   public UniversalCoins() {
     IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

     ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, UCConfig.COMMON_SPEC);

     ModItems.register(bus);
     ModBlocks.register(bus);
     ModTileEntities.register(bus);
     ModContainerTypes.register(bus);


    bus.addListener(ClientSetup::init);

    bus.addListener(this::setup);

    MinecraftForge.EVENT_BUS.register(this);
   }


   private void setup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
           ModPacketHandler.registerMessages();

           UCItemPricer.getInstance().loadConfigs();

           UCAccountBalanceTrigger dummy = UCAccountBalanceTrigger.INSTANCE;
         });

    syncConfig();
   }

   public static void syncConfig() {
    smallPackagePrice = UCConfig.SMALL_PACKAGE_PRICE.get();
    medPackagePrice = UCConfig.MED_PACKAGE_PRICE.get();
    largePackagePrice = UCConfig.LARGE_PACKAGE_PRICE.get();
    rfWholesaleRate = UCConfig.RF_WHOLESALE_RATE.get();
    rfRetailRate = UCConfig.RF_RETAIL_RATE.get();
    itemSellRatio = UCConfig.ITEM_SELL_RATIO.get();
    tradeStationBuyEnabled = UCConfig.TRADE_STATION_BUY_ENABLED.get();
    autoModeEnabled = UCConfig.AUTO_MODE_ENABLED.get();

    for (int i = 0; i < 5; i++) {
      coinValues[i] = ((UCConfig.COIN_VALUES.get()).get(i));
     }
   }

   @SubscribeEvent
   public void onServerStarting(FMLServerStartingEvent event) {
    UCItemPricer.getInstance().priceCraftedItems();
    LOGGER.info("Universal Coins: Item prices calculated.");
   }
 }