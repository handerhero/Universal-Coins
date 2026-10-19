package aspadien.universalcoins.init;

import aspadien.universalcoins.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class ModBlocks {
 public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

 public static final RegistryObject<Block> ATM = BLOCKS.register("atm", BlockATM::new);
 public static final RegistryObject<Block> VENDOR = BLOCKS.register("vendor_block", BlockVendor::new);
 public static final RegistryObject<BlockVendorFrame> VENDOR_FRAME = BLOCKS.register("vendor_frame", BlockVendorFrame::new);
 public static final RegistryObject<Block> STANDING_UC_SIGN = BLOCKS.register("standing_ucsign", BlockUCStandingSign::new);
 public static final RegistryObject<Block> WALL_UC_SIGN = BLOCKS.register("wall_ucsign", BlockUCWallSign::new);
 public static final RegistryObject<Block> TRADE_STATION = BLOCKS.register("tradestation", BlockTradeStation::new);
 public static final RegistryObject<Block> SAFE = BLOCKS.register("safe", BlockSafe::new);
 public static final RegistryObject<Block> PACKAGER = BLOCKS.register("packager", BlockPackager::new);
 public static final RegistryObject<Block> POWER_TRANSMITTER = BLOCKS.register("power_transmitter", BlockPowerTransmitter::new);
 public static final RegistryObject<Block> POWER_RECEIVER = BLOCKS.register("power_receiver", BlockPowerReceiver::new);
 public static final RegistryObject<Block> SIGNAL = BLOCKS.register("signalblock", BlockSignal::new);

 public static void register(IEventBus eventBus) {
  BLOCKS.register(eventBus);
 }
}