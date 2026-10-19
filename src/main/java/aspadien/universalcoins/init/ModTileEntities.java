package aspadien.universalcoins.init;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import aspadien.universalcoins.tileentity.*;


public class ModTileEntities {
  public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "universalcoins");


  public static final RegistryObject<TileEntityType<TileATM>> ATM = TILE_ENTITIES.register("atm", () -> TileEntityType.Builder.of(TileATM::new, new Block[] {ModBlocks.ATM.get()}).build(null));



  public static final RegistryObject<TileEntityType<TileVendorBlock>> VENDOR_BLOCK = TILE_ENTITIES
    .register("vendor_block", () -> TileEntityType.Builder.of(TileVendorBlock::new, new Block[] { ModBlocks.VENDOR.get() }).build(null));



  public static final RegistryObject<TileEntityType<TileVendorFrame>> VENDOR_FRAME = TILE_ENTITIES
    .register("vendor_frame", () -> TileEntityType.Builder.of(TileVendorFrame::new, new Block[] { ModBlocks.VENDOR_FRAME.get() }).build(null));


  public static final RegistryObject<TileEntityType<TileTradeStation>> TRADE_STATION = TILE_ENTITIES
    .register("trade_station", () -> TileEntityType.Builder.of(TileTradeStation::new, new Block[] { ModBlocks.TRADE_STATION.get() }).build(null));


  public static final RegistryObject<TileEntityType<TileSafe>> SAFE = TILE_ENTITIES
    .register("safe", () -> TileEntityType.Builder.of(TileSafe::new, new Block[] { ModBlocks.SAFE.get() }).build(null));


  public static final RegistryObject<TileEntityType<TilePackager>> PACKAGER = TILE_ENTITIES
    .register("packager", () -> TileEntityType.Builder.of(TilePackager::new, new Block[] { ModBlocks.PACKAGER.get() }).build(null));


  public static final RegistryObject<TileEntityType<TilePowerTransmitter>> POWER_TRANSMITTER = TILE_ENTITIES
    .register("power_transmitter", () -> TileEntityType.Builder.of(TilePowerTransmitter::new, new Block[] { ModBlocks.POWER_TRANSMITTER.get() }).build(null));


  public static final RegistryObject<TileEntityType<TilePowerReceiver>> POWER_RECEIVER = TILE_ENTITIES
    .register("power_receiver", () -> TileEntityType.Builder.of(TilePowerReceiver::new, new Block[] { ModBlocks.POWER_RECEIVER.get() }).build(null));


  public static final RegistryObject<TileEntityType<TileSignal>> SIGNAL = TILE_ENTITIES
    .register("signal", () -> TileEntityType.Builder.of(TileSignal::new, new Block[] { ModBlocks.SIGNAL.get() }).build(null));


  public static final RegistryObject<TileEntityType<aspadien.universalcoins.tileentity.TileUCSign>> UC_SIGN = TILE_ENTITIES.register("uc_sign", () -> TileEntityType.Builder.of(TileUCSign::new, new Block[] { ModBlocks.STANDING_UC_SIGN.get(), ModBlocks.WALL_UC_SIGN.get() }).build(null));







  public static void register(IEventBus eventBus) {
    TILE_ENTITIES.register(eventBus);
  }
}
