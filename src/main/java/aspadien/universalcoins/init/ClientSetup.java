package aspadien.universalcoins.init;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.gui.*;
import aspadien.universalcoins.render.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = UniversalCoins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

      @SubscribeEvent
      public static void init(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                  // RenderTypeLookup -> ItemBlockRenderTypes
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.VENDOR.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.VENDOR_FRAME.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.SIGNAL.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.PACKAGER.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.ATM.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.SAFE.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.TRADE_STATION.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.STANDING_UC_SIGN.get(), RenderType.cutout());
                  ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_UC_SIGN.get(), RenderType.cutout());

                  // ScreenManager -> MenuScreens
                  MenuScreens.register(ModContainerTypes.VENDOR.get(), VendorScreen::new);
                  MenuScreens.register(ModContainerTypes.VENDOR_BUY.get(), VendorBuyScreen::new);
                  MenuScreens.register(ModContainerTypes.VENDOR_SELL.get(), VendorSellScreen::new);
                  MenuScreens.register(ModContainerTypes.ATM.get(), ATMScreen::new);
                  MenuScreens.register(ModContainerTypes.SAFE.get(), SafeScreen::new);
                  MenuScreens.register(ModContainerTypes.TRADE_STATION.get(), TradeStationScreen::new);
                  MenuScreens.register(ModContainerTypes.PACKAGER.get(), PackagerScreen::new);
                  MenuScreens.register(ModContainerTypes.SIGNAL.get(), SignalScreen::new);
                  MenuScreens.register(ModContainerTypes.POWER_RECEIVER.get(), PowerReceiverScreen::new);
                  MenuScreens.register(ModContainerTypes.POWER_TRANSMITTER.get(), PowerTransmitterScreen::new);
                  MenuScreens.register(ModContainerTypes.VENDOR_WRENCH.get(), VendorWrenchScreen::new);
            });
      }

      // В 1.20.1 ClientRegistry.bindTileEntityRenderer больше нет, используется этот ивент:
      @SubscribeEvent
      public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.VENDOR_BLOCK.get(), VendorBlockRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.VENDOR_FRAME.get(), VendorFrameRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.SIGNAL.get(), SignalRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.UC_SIGN.get(), UCSignRenderer::new);
      }
}