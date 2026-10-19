package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.container.ContainerVendorSell;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorSellScreen extends AbstractContainerScreen<ContainerVendorSell> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_sell.png");
    private final Vendor tileEntity;
    private Button sellButton;
    private final Button[] coinButtons = new Button[5];

    public VendorSellScreen(ContainerVendorSell container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 198;
    }

    @Override
    protected void init() {
        super.init();

        this.sellButton = this.addRenderableWidget(new SlimButton(this.leftPos + 126, this.topPos + 46, 42, 12,
                Component.translatable("general.button.sell"), b -> this.tileEntity.sendButtonMessage(10, hasShiftDown())));

        for (int i = 0; i < 5; i++) {
            int index = i;
            this.coinButtons[i] = this.addRenderableWidget(new CoinButton(this.leftPos + 60 + i * 18, this.topPos + 82, index,
                    b -> this.tileEntity.sendButtonMessage(12 + index, hasShiftDown())));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.sellButton.active = (((ContainerVendorSell) this.menu).getSyncedValue(9) == 1);
        for (int i = 0; i < 5; i++) {
            this.coinButtons[i].active = (((ContainerVendorSell) this.menu).getSyncedValue(15 + i) == 1);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 105, 4210752, false);

        String price = String.valueOf(((ContainerVendorSell) this.menu).getSyncedValue(2));
        guiGraphics.drawString(this.font, price, 48, 29, 4210752, false);

        String userBalance = String.valueOf(((ContainerVendorSell) this.menu).getSyncedValue(1));
        guiGraphics.drawString(this.font, userBalance, 63, 69, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}