package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Signal;
import aspadien.universalcoins.container.ContainerSignal;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SignalScreen extends AbstractContainerScreen<ContainerSignal> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/signal.png");
    private final Signal tileEntity;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");

    private Button coinButton;

    public static final int idCoinButton = 0;
    public static final int idDurMinusButton = 1;
    public static final int idDurPlusButton = 2;
    public static final int idFeeMinusButton = 3;
    public static final int idFeePlusButton = 4;

    public SignalScreen(ContainerSignal container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 201;
    }

    @Override
    protected void init() {
        super.init();

        this.coinButton = this.addRenderableWidget(new SlimButton(this.leftPos + 111, this.topPos + 92, 32, 12,
                Component.translatable("general.button.coin"),
                b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));

        this.addRenderableWidget(new SlimButton(this.leftPos + 95, this.topPos + 48, 12, 12,
                Component.literal("-"),
                b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));

        this.addRenderableWidget(new SlimButton(this.leftPos + 144, this.topPos + 48, 12, 12,
                Component.literal("+"),
                b -> this.tileEntity.sendButtonMessage(2, hasShiftDown())));

        this.addRenderableWidget(new SlimButton(this.leftPos + 95, this.topPos + 26, 12, 12,
                Component.literal("-"),
                b -> this.tileEntity.sendButtonMessage(3, hasShiftDown())));

        this.addRenderableWidget(new SlimButton(this.leftPos + 144, this.topPos + 26, 12, 12,
                Component.literal("+"),
                b -> this.tileEntity.sendButtonMessage(4, hasShiftDown())));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 8, 5, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 96, 4210752, false);

        String feeVal = this.formatter.format(((ContainerSignal) this.menu).getSyncedValue(1));
        guiGraphics.drawString(this.font, feeVal, 138 - this.font.width(feeVal), 28, 4210752, false);

        String durVal = this.formatter.format(((ContainerSignal) this.menu).getSyncedValue(2));
        guiGraphics.drawString(this.font, durVal, 138 - this.font.width(durVal), 50, 4210752, false);

        String coinSumStr = this.formatter.format(((ContainerSignal) this.menu).getSyncedValue(0));
        guiGraphics.drawString(this.font, coinSumStr, 138 - this.font.width(coinSumStr), 78, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.coinButton.active = (this.tileEntity.coinSum > 0);
    }
}