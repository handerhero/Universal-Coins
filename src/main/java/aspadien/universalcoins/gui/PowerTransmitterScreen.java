package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.PowerTransmitter;
import aspadien.universalcoins.container.ContainerPowerTransmitter;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PowerTransmitterScreen extends AbstractContainerScreen<ContainerPowerTransmitter> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/power_transmitter.png");
    private final PowerTransmitter tileEntity;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
    private Button accessModeButton;

    public PowerTransmitterScreen(ContainerPowerTransmitter container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 157;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new SlimButton(this.leftPos + 123, this.topPos + 60, 46, 12,
                Component.translatable("general.button.coin"),
                b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));

        this.accessModeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 122, this.topPos + 4, 50, 12,
                Component.empty(),
                b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 6, 5, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 6, 63, 4210752, false);

        long low = ((ContainerPowerTransmitter) this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
        long high = ((long) ((ContainerPowerTransmitter) this.menu).getSyncedValue(1)) << 32L;
        long currentCoinSum = high | low;

        int currentKfeSold = ((ContainerPowerTransmitter) this.menu).getSyncedValue(3);

        String formattedkfe = this.formatter.format(currentKfeSold) + " kFE";
        guiGraphics.drawString(this.font, formattedkfe, 130 - this.font.width(formattedkfe), 26, 4210752, false);

        String formattedBalance = this.formatter.format(currentCoinSum);
        guiGraphics.drawString(this.font, formattedBalance, 131 - this.font.width(formattedBalance), 48, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        boolean isPublic = (((ContainerPowerTransmitter) this.menu).getSyncedValue(4) == 1);
        this.accessModeButton.setMessage(isPublic ? Component.translatable("general.label.public") : Component.translatable("general.label.private"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}