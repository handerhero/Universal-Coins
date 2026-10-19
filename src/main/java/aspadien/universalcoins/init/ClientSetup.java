package aspadien.universalcoins.init;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public class ClientSetup
{
  public static void init(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
          RenderTypeLookup.setRenderLayer(ModBlocks.VENDOR.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.VENDOR_FRAME.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.SIGNAL.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.PACKAGER.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.ATM.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.SAFE.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.TRADE_STATION.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.STANDING_UC_SIGN.get(), RenderType.cutout());
          RenderTypeLookup.setRenderLayer(ModBlocks.WALL_UC_SIGN.get(), RenderType.cutout());
          ScreenManager.register(ModContainerTypes.VENDOR.get(), aspadien.universalcoins.gui.VendorScreen::new);
          ScreenManager.register(ModContainerTypes.VENDOR_BUY.get(), aspadien.universalcoins.gui.VendorBuyScreen::new);
          ScreenManager.register(ModContainerTypes.VENDOR_SELL.get(), aspadien.universalcoins.gui.VendorSellScreen::new);
          ScreenManager.register(ModContainerTypes.ATM.get(), aspadien.universalcoins.gui.ATMScreen::new);
          ScreenManager.register(ModContainerTypes.SAFE.get(), aspadien.universalcoins.gui.SafeScreen::new);
          ScreenManager.register(ModContainerTypes.TRADE_STATION.get(), aspadien.universalcoins.gui.TradeStationScreen::new);
          ScreenManager.register(ModContainerTypes.PACKAGER.get(), aspadien.universalcoins.gui.PackagerScreen::new);
          ScreenManager.register(ModContainerTypes.SIGNAL.get(), aspadien.universalcoins.gui.SignalScreen::new);
          ScreenManager.register(ModContainerTypes.POWER_RECEIVER.get(), aspadien.universalcoins.gui.PowerReceiverScreen::new);
          ScreenManager.register(ModContainerTypes.POWER_TRANSMITTER.get(), aspadien.universalcoins.gui.PowerTransmitterScreen::new);
          ScreenManager.register(ModContainerTypes.VENDOR_WRENCH.get(), aspadien.universalcoins.gui.VendorWrenchScreen::new);
          ClientRegistry.bindTileEntityRenderer(ModTileEntities.VENDOR_BLOCK.get(), aspadien.universalcoins.render.VendorBlockRenderer::new);
          ClientRegistry.bindTileEntityRenderer(ModTileEntities.VENDOR_FRAME.get(), aspadien.universalcoins.render.VendorFrameRenderer::new);
          ClientRegistry.bindTileEntityRenderer(ModTileEntities.SIGNAL.get(), aspadien.universalcoins.render.SignalRenderer::new);
          ClientRegistry.bindTileEntityRenderer(ModTileEntities.UC_SIGN.get(), aspadien.universalcoins.render.UCSignRenderer::new);
        });
  }
}
