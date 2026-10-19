package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Safe;
import aspadien.universalcoins.container.ContainerSafe;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SafeScreen extends AbstractContainerScreen<ContainerSafe> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/safe.png");
    private final Safe tileEntity;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");

    public SafeScreen(ContainerSafe container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 152;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 6, 5, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 6, 58, 4210752, false);

        long low = ((ContainerSafe) this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
        long high = ((ContainerSafe) this.menu).getSyncedValue(1);
        long currentBalance = (high << 32L) | low;

        String formattedBalance = this.formatter.format(currentBalance);
        int balLength = this.font.width(formattedBalance);

        guiGraphics.drawString(this.font, formattedBalance, 154 - balLength, 22, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}