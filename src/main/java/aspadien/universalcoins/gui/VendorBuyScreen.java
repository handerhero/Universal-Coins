package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.container.ContainerVendorBuy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorBuyScreen extends AbstractContainerScreen<ContainerVendorBuy> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_buy.png");
    private final Vendor tileEntity;
    private Button buyButton;
    private final Button[] coinButtons = new Button[5];

    public VendorBuyScreen(ContainerVendorBuy container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 189;
    }

    @Override
    protected void init() {
        super.init();

        this.buyButton = this.addRenderableWidget(new SlimButton(this.leftPos + 126, this.topPos + 42, 42, 12,
                Component.translatable("general.button.buy"),
                b -> this.tileEntity.sendButtonMessage(11, hasShiftDown())));

        for (int i = 0; i < 5; i++) {
            int index = i;
            this.coinButtons[i] = this.addRenderableWidget(new CoinButton(this.leftPos + 56 + i * 18, this.topPos + 74, index,
                    b -> this.tileEntity.sendButtonMessage(12 + index, hasShiftDown())));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.buyButton.active = (((ContainerVendorBuy) this.menu).getSyncedValue(8) == 1);
        for (int i = 0; i < 5; i++) {
            this.coinButtons[i].active = (((ContainerVendorBuy) this.menu).getSyncedValue(15 + i) == 1);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 97, 4210752, false);

        String price = String.valueOf(((ContainerVendorBuy) this.menu).getSyncedValue(2));
        guiGraphics.drawString(this.font, price, 59, 29, 4210752, false);

        String userBalance = String.valueOf(((ContainerVendorBuy) this.menu).getSyncedValue(1));
        guiGraphics.drawString(this.font, userBalance, 59, 62, 4210752, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}