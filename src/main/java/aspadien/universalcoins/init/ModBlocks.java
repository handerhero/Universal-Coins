 package aspadien.universalcoins.init;



 import net.minecraft.block.Block;
 import net.minecraftforge.eventbus.api.IEventBus;
 import net.minecraftforge.fml.RegistryObject;
 import net.minecraftforge.registries.DeferredRegister;
 import net.minecraftforge.registries.ForgeRegistries;
 import aspadien.universalcoins.block.BlockVendorFrame;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;


 public class ModBlocks
 {
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

  public static final RegistryObject<Block> ATM = BLOCKS.register("atm", aspadien.universalcoins.block.BlockATM::new);

  public static final RegistryObject<Block> VENDOR = BLOCKS.register("vendor_block", aspadien.universalcoins.block.BlockVendor::new);

  public static final RegistryObject<BlockVendorFrame> VENDOR_FRAME = BLOCKS.register("vendor_frame", BlockVendorFrame::new);

  public static final RegistryObject<Block> STANDING_UC_SIGN = BLOCKS.register("standing_ucsign", aspadien.universalcoins.block.BlockUCStandingSign::new);

  public static final RegistryObject<Block> WALL_UC_SIGN = BLOCKS.register("wall_ucsign", aspadien.universalcoins.block.BlockUCWallSign::new);

  public static final RegistryObject<Block> TRADE_STATION = BLOCKS.register("tradestation", aspadien.universalcoins.block.BlockTradeStation::new);

  public static final RegistryObject<Block> SAFE = BLOCKS.register("safe", aspadien.universalcoins.block.BlockSafe::new);

  public static final RegistryObject<Block> PACKAGER = BLOCKS.register("packager", aspadien.universalcoins.block.BlockPackager::new);

  public static final RegistryObject<Block> POWER_TRANSMITTER = BLOCKS.register("power_transmitter", aspadien.universalcoins.block.BlockPowerTransmitter::new);

  public static final RegistryObject<Block> POWER_RECEIVER = BLOCKS.register("power_receiver", aspadien.universalcoins.block.BlockPowerReceiver::new);

  public static final RegistryObject<Block> SIGNAL = BLOCKS.register("signalblock", aspadien.universalcoins.block.BlockSignal::new);


   public static void register(IEventBus eventBus) {
       BLOCKS.register(eventBus);
   }
 }