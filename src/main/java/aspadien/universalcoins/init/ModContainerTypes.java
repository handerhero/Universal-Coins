package aspadien.universalcoins.init;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import aspadien.universalcoins.container.*;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;


/*    */
/*    */ public class ModContainerTypes {
/* 18 */   public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);
/*    */ 
/*    */   
/* 21 */   public static final RegistryObject<ContainerType<ContainerVendor>> VENDOR = CONTAINERS
/* 22 */     .register("vendor", () -> IForgeContainerType.create(ContainerVendor::new));
/*    */ 
/*    */   
/* 25 */   public static final RegistryObject<ContainerType<ContainerVendorBuy>> VENDOR_BUY = CONTAINERS
/* 26 */     .register("vendor_buy", () -> IForgeContainerType.create(ContainerVendorBuy::new));
/*    */ 
/*    */   
/* 29 */   public static final RegistryObject<ContainerType<ContainerVendorSell>> VENDOR_SELL = CONTAINERS
/* 30 */     .register("vendor_sell", () -> IForgeContainerType.create(ContainerVendorSell::new));
/*    */ 
/*    */   
/* 33 */   public static final RegistryObject<ContainerType<ContainerATM>> ATM = CONTAINERS
/* 34 */     .register("atm", () -> IForgeContainerType.create(ContainerATM::new));
/*    */   
/* 36 */   public static final RegistryObject<ContainerType<ContainerTradeStation>> TRADE_STATION = CONTAINERS
/* 37 */     .register("trade_station", () -> IForgeContainerType.create(ContainerTradeStation::new));
/*    */   
/* 39 */   public static final RegistryObject<ContainerType<ContainerPackager>> PACKAGER = CONTAINERS
/* 40 */     .register("packager", () -> IForgeContainerType.create(ContainerPackager::new));
/*    */   
/* 42 */   public static final RegistryObject<ContainerType<ContainerPowerReceiver>> POWER_RECEIVER = CONTAINERS
/* 43 */     .register("power_receiver", () -> IForgeContainerType.create(ContainerPowerReceiver::new));
/*    */   
/* 45 */   public static final RegistryObject<ContainerType<ContainerPowerTransmitter>> POWER_TRANSMITTER = CONTAINERS
/* 46 */     .register("power_transmitter", () -> IForgeContainerType.create(ContainerPowerTransmitter::new));
/*    */   
/* 48 */   public static final RegistryObject<ContainerType<ContainerSignal>> SIGNAL = CONTAINERS
/* 49 */     .register("signal", () -> IForgeContainerType.create(ContainerSignal::new));
/*    */   
/* 51 */   public static final RegistryObject<ContainerType<ContainerSafe>> SAFE = CONTAINERS
/* 52 */     .register("safe", () -> IForgeContainerType.create(ContainerSafe::new));
/*    */   
/* 54 */   public static final RegistryObject<ContainerType<ContainerVendorWrench>> VENDOR_WRENCH = CONTAINERS
/* 55 */     .register("vendor_wrench", () -> IForgeContainerType.create(ContainerVendorWrench::new));
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void register(IEventBus eventBus) {
/* 61 */     CONTAINERS.register(eventBus);
/*    */   }
/*    */ }
