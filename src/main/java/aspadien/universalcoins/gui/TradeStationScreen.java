package aspadien.universalcoins.gui;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.blockentities.TradeStation;
import aspadien.universalcoins.container.ContainerTradeStation;
import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCButtonMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class TradeStationScreen extends AbstractContainerScreen<ContainerTradeStation> {
  private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/trade_station.png");
  private final TradeStation tileEntity;

  public static final int idBuyButton = 0;
  public static final int idSellButton = 1;
  public static final int idCoinButton = 2;
  public static final int idCoinModeButton = 7;
  public static final int idAutoModeButton = 8;
  public static final int idAccessModeButton = 9;

  private final Button[] coinButtons = new Button[6];
  private Button buyButton;
  private Button sellButton;
  private Button accessModeButton;
  private Button autoModeButton;
  private Button coinModeButton;

  public TradeStationScreen(ContainerTradeStation container, Inventory inv, Component title) {
    super(container, inv, title);
    this.tileEntity = container.getTile();
    this.imageWidth = 184;
    this.imageHeight = 200;
  }

  @Override
  protected void init() {
    super.init();

    this.buyButton = this.addRenderableWidget(new SlimButton(this.leftPos + 50, this.topPos + 21, 48, 12,
            Component.translatable("general.button.buy"),
            b -> this.tileEntity.sendButtonMessage(0, hasShiftDown(), 0)));
    this.buyButton.visible = UniversalCoins.tradeStationBuyEnabled;

    this.sellButton = this.addRenderableWidget(new SlimButton(this.leftPos + 50, this.topPos + 38, 48, 12,
            Component.translatable("general.button.sell"),
            b -> this.tileEntity.sendButtonMessage(1, hasShiftDown(), 0)));

    for (int i = 0; i < 6; i++) {
      int index = i;
      this.coinButtons[i] = this.addRenderableWidget(new CoinButton(this.leftPos + 88 + i * 18, this.topPos + 74, index,
              b -> this.tileEntity.sendButtonMessage(2 + index, hasShiftDown(), 0)));
    }

    this.coinModeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 110, this.topPos + 98, 46, 12,
            Component.translatable("general.button.coin"),
            b -> this.tileEntity.sendButtonMessage(7, hasShiftDown(), 0)));

    this.accessModeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 127, this.topPos + 4, 52, 12,
            Component.empty(),
            b -> this.tileEntity.sendButtonMessage(9, hasShiftDown(), 0)));

    if (this.tileEntity.autoModeButtonActive) {
      this.autoModeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 6, this.topPos + 84, 28, 12,
              Component.translatable("tradestation.gui.button.mode"),
              b -> this.tileEntity.sendButtonMessage(8, hasShiftDown(), 0)));
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 1 && this.coinModeButton != null && this.coinModeButton.isMouseOver(mouseX, mouseY)) {
      this.coinModeButton.playDownSound(Minecraft.getInstance().getSoundManager());
      ModPacketHandler.sendToServer(new UCButtonMessage(this.tileEntity.getBlockPos(), 7, hasShiftDown(), 1));
      return true;
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    this.buyButton.active = this.tileEntity.buyButtonActive;
    this.sellButton.active = this.tileEntity.sellButtonActive;

    this.coinButtons[0].active = (((ContainerTradeStation) this.menu).getSyncedValue(7) == 1);
    this.coinButtons[1].active = (((ContainerTradeStation) this.menu).getSyncedValue(8) == 1);
    this.coinButtons[2].active = (((ContainerTradeStation) this.menu).getSyncedValue(9) == 1);
    this.coinButtons[3].active = (((ContainerTradeStation) this.menu).getSyncedValue(10) == 1);
    this.coinButtons[4].active = (((ContainerTradeStation) this.menu).getSyncedValue(11) == 1);
    this.coinButtons[5].active = (((ContainerTradeStation) this.menu).getSyncedValue(12) == 1);

    this.accessModeButton.setMessage(this.tileEntity.publicAccess
            ? Component.translatable("general.label.public")
            : Component.translatable("general.label.private"));

    if (this.tileEntity.autoModeButtonActive) {
      guiGraphics.blit(TEXTURE, this.leftPos + 35, this.topPos + 83, 184, 0, 50, 15);
    }

    int[] xHighlight = {0, 89, 107, 125, 143, 161, 179};
    if (this.tileEntity.coinMode > 0 && this.tileEntity.coinMode < xHighlight.length) {
      guiGraphics.blit(TEXTURE, this.leftPos + xHighlight[this.tileEntity.coinMode], this.topPos + 94, 184, 15, 16, 2);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 6, 5, 4210752, false);
    guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 6, this.imageHeight - 94, 4210752, false);

    guiGraphics.drawString(this.font, String.valueOf(this.tileEntity.coinSum), 114, 57, 4210752, false);

    String priceLabel = Component.translatable("general.label.price").getString();
    int labelWidth = this.font.width(priceLabel);
    guiGraphics.drawString(this.font, priceLabel, 48 - labelWidth, 57, 4210752, false);

    if (this.tileEntity.itemPrice > 0) {
      int displayPrice = this.tileEntity.itemPrice;
      if (this.sellButton.isMouseOver(mouseX, mouseY) || !UniversalCoins.tradeStationBuyEnabled) {
        displayPrice = (int) (this.tileEntity.itemPrice * UniversalCoins.itemSellRatio);
      }
      guiGraphics.drawString(this.font, String.valueOf(displayPrice), 48, 57, 4210752, false);
    } else {
      guiGraphics.drawString(this.font, Component.translatable("tradestation.gui.warning.noitem"), 48, 57, 4210752, false);
    }

    if (this.tileEntity.autoModeButtonActive) {
      guiGraphics.drawString(this.font, Component.translatable("tradestation.gui.label.autobuy"), 6, 74, 4210752, false);

      String[] autoLabels = {
              Component.translatable("tradestation.gui.autolabel.off").getString(),
              Component.translatable("tradestation.gui.autolabel.buy").getString(),
              Component.translatable("tradestation.gui.autolabel.sell").getString()
      };

      guiGraphics.drawString(this.font, autoLabels[this.tileEntity.autoMode % 3], 38, 87, 4210752, false);
    }
  }
}