package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.PowerReceiver;
import aspadien.universalcoins.container.ContainerPowerReceiver;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PowerReceiverScreen extends AbstractContainerScreen<ContainerPowerReceiver> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/power_receiver.png");
    private final PowerReceiver tileEntity;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
    private Button accessModeButton;

    public PowerReceiverScreen(ContainerPowerReceiver container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 184;
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new SlimButton(this.leftPos + 123, this.topPos + 89, 46, 12,
                Component.translatable("general.button.coin"), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));

        this.accessModeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 122, this.topPos + 4, 50, 12,
                Component.empty(), b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);

        long low = ((ContainerPowerReceiver) this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
        long high = ((long) ((ContainerPowerReceiver) this.menu).getSyncedValue(1)) << 32L;
        long coinSum = high | low;

        int feLevel = ((ContainerPowerReceiver) this.menu).getSyncedValue(2);
        int feOutput = ((ContainerPowerReceiver) this.menu).getSyncedValue(3);

        long wLow = ((ContainerPowerReceiver) this.menu).getSyncedValue(4) & 0xFFFFFFFFL;
        long wHigh = ((long) ((ContainerPowerReceiver) this.menu).getSyncedValue(5)) << 32L;
        long worldPower = wHigh | wLow;

        guiGraphics.drawString(this.font, "Coins: " + this.formatter.format(coinSum), 22, 24, 4210752, false);
        guiGraphics.drawString(this.font, "Internal: " + feLevel + " FE", 54, 43, 4210752, false);
        guiGraphics.drawString(this.font, "Output: " + feOutput + " FE/t", 54, 59, 4210752, false);
        guiGraphics.drawString(this.font, "Network: " + worldPower + " FE", 54, 76, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.accessModeButton.setMessage(this.tileEntity.publicAccess
                ? Component.translatable("general.label.public")
                : Component.translatable("general.label.private"));
    }
}