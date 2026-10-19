package aspadien.universalcoins.init;

import aspadien.universalcoins.blockentities.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
          DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);

  public static final RegistryObject<BlockEntityType<ATM>> ATM = BLOCK_ENTITIES
          .register("atm", () -> BlockEntityType.Builder.of(ATM::new, ModBlocks.ATM.get()).build(null));

  public static final RegistryObject<BlockEntityType<VendorBlock>> VENDOR_BLOCK = BLOCK_ENTITIES
          .register("vendor_block", () -> BlockEntityType.Builder.of(VendorBlock::new, ModBlocks.VENDOR.get()).build(null));

  public static final RegistryObject<BlockEntityType<VendorFrame>> VENDOR_FRAME = BLOCK_ENTITIES
          .register("vendor_frame", () -> BlockEntityType.Builder.of(VendorFrame::new, ModBlocks.VENDOR_FRAME.get()).build(null));

  public static final RegistryObject<BlockEntityType<TradeStation>> TRADE_STATION = BLOCK_ENTITIES
          .register("trade_station", () -> BlockEntityType.Builder.of(TradeStation::new, ModBlocks.TRADE_STATION.get()).build(null));

  public static final RegistryObject<BlockEntityType<Safe>> SAFE = BLOCK_ENTITIES
          .register("safe", () -> BlockEntityType.Builder.of(Safe::new, ModBlocks.SAFE.get()).build(null));

  public static final RegistryObject<BlockEntityType<Packager>> PACKAGER = BLOCK_ENTITIES
          .register("packager", () -> BlockEntityType.Builder.of(Packager::new, ModBlocks.PACKAGER.get()).build(null));

  public static final RegistryObject<BlockEntityType<PowerTransmitter>> POWER_TRANSMITTER = BLOCK_ENTITIES
          .register("power_transmitter", () -> BlockEntityType.Builder.of(PowerTransmitter::new, ModBlocks.POWER_TRANSMITTER.get()).build(null));

  public static final RegistryObject<BlockEntityType<PowerReceiver>> POWER_RECEIVER = BLOCK_ENTITIES
          .register("power_receiver", () -> BlockEntityType.Builder.of(PowerReceiver::new, ModBlocks.POWER_RECEIVER.get()).build(null));

  public static final RegistryObject<BlockEntityType<Signal>> SIGNAL = BLOCK_ENTITIES
          .register("signal", () -> BlockEntityType.Builder.of(Signal::new, ModBlocks.SIGNAL.get()).build(null));

  public static final RegistryObject<BlockEntityType<UCSign>> UC_SIGN = BLOCK_ENTITIES
          .register("uc_sign", () -> BlockEntityType.Builder.of(UCSign::new, ModBlocks.STANDING_UC_SIGN.get(), ModBlocks.WALL_UC_SIGN.get()).build(null));

  public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
  }
}