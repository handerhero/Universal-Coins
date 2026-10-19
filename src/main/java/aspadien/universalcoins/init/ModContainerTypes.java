package aspadien.universalcoins.init;

import aspadien.universalcoins.container.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class ModContainerTypes {
    // В 1.20.1 CONTAINERS переименованы в MENU_TYPES
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<ContainerVendor>> VENDOR = CONTAINERS
            .register("vendor", () -> IForgeMenuType.create(ContainerVendor::new));

    public static final RegistryObject<MenuType<ContainerVendorBuy>> VENDOR_BUY = CONTAINERS
            .register("vendor_buy", () -> IForgeMenuType.create(ContainerVendorBuy::new));

    public static final RegistryObject<MenuType<ContainerVendorSell>> VENDOR_SELL = CONTAINERS
            .register("vendor_sell", () -> IForgeMenuType.create(ContainerVendorSell::new));

    public static final RegistryObject<MenuType<ContainerATM>> ATM = CONTAINERS
            .register("atm", () -> IForgeMenuType.create(ContainerATM::new));

    public static final RegistryObject<MenuType<ContainerTradeStation>> TRADE_STATION = CONTAINERS
            .register("trade_station", () -> IForgeMenuType.create(ContainerTradeStation::new));

    public static final RegistryObject<MenuType<ContainerPackager>> PACKAGER = CONTAINERS
            .register("packager", () -> IForgeMenuType.create(ContainerPackager::new));

    public static final RegistryObject<MenuType<ContainerPowerReceiver>> POWER_RECEIVER = CONTAINERS
            .register("power_receiver", () -> IForgeMenuType.create(ContainerPowerReceiver::new));

    public static final RegistryObject<MenuType<ContainerPowerTransmitter>> POWER_TRANSMITTER = CONTAINERS
            .register("power_transmitter", () -> IForgeMenuType.create(ContainerPowerTransmitter::new));

    public static final RegistryObject<MenuType<ContainerSignal>> SIGNAL = CONTAINERS
            .register("signal", () -> IForgeMenuType.create(ContainerSignal::new));

    public static final RegistryObject<MenuType<ContainerSafe>> SAFE = CONTAINERS
            .register("safe", () -> IForgeMenuType.create(ContainerSafe::new));

    public static final RegistryObject<MenuType<ContainerVendorWrench>> VENDOR_WRENCH = CONTAINERS
            .register("vendor_wrench", () -> IForgeMenuType.create(ContainerVendorWrench::new));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}