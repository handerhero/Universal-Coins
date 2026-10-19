package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.container.ContainerVendor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorScreen extends AbstractContainerScreen<ContainerVendor> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor.png");
    private final Vendor tileEntity;
    private EditBox itemPriceField;
    private Button modeButton;
    private boolean textActive = false;

    private final Button[] coinButtons = new Button[5];

    public VendorScreen(ContainerVendor container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();

        this.modeButton = this.addRenderableWidget(Button.builder(Component.empty(), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown()))
                .bounds(this.leftPos + 8, this.topPos + 35, 62, 12)
                .build());

        this.addRenderableWidget(Button.builder(Component.translatable("general.button.edit"), b -> {
            this.itemPriceField.setValue(String.valueOf(this.tileEntity.itemPrice));
            this.textActive = true;
            this.itemPriceField.setVisible(true);
            this.itemPriceField.setFocused(true);
            this.setFocused(this.itemPriceField);
            this.tileEntity.sendButtonMessage(1, hasShiftDown());
        }).bounds(this.leftPos + 79, this.topPos + 35, 44, 12).build());

        this.addRenderableWidget(Button.builder(Component.translatable("general.button.save"), b -> {
            try {
                this.tileEntity.itemPrice = Integer.parseInt(this.itemPriceField.getValue());
            } catch (NumberFormatException ignored) {}
            this.textActive = false;
            this.itemPriceField.setVisible(false);
            this.itemPriceField.setFocused(false);
            this.tileEntity.sendServerUpdateMessage();
            this.tileEntity.sendButtonMessage(2, hasShiftDown());
        }).bounds(this.leftPos + 124, this.topPos + 35, 44, 12).build());

        String[] coinLabels = {"I", "G", "E", "D", "O"};
        for (int i = 0; i < 5; i++) {
            int index = i;
            this.coinButtons[i] = this.addRenderableWidget(Button.builder(Component.literal(coinLabels[i]), b -> this.tileEntity.sendButtonMessage(3 + index, hasShiftDown()))
                    .bounds(this.leftPos + 56 + (i * 18), this.topPos + 74, 18, 18)
                    .build());
        }

        this.addRenderableWidget(Button.builder(Component.literal("-"), b -> this.tileEntity.sendButtonMessage(8, hasShiftDown()))
                .bounds(this.leftPos + 7, this.topPos + 78, 12, 12)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("+"), b -> this.tileEntity.sendButtonMessage(9, hasShiftDown()))
                .bounds(this.leftPos + 33, this.topPos + 78, 12, 12)
                .build());

        this.itemPriceField = new EditBox(this.font, this.leftPos + 82, this.topPos + 22, 86, 12, Component.empty());
        this.itemPriceField.setMaxLength(10);
        this.itemPriceField.setBordered(false);
        this.itemPriceField.setVisible(false);
        this.itemPriceField.setTextColor(16777215);
        this.addRenderableWidget(this.itemPriceField);
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

        this.modeButton.setMessage(this.tileEntity.sellMode
                ? Component.translatable("general.button.sell")
                : Component.translatable("general.button.buy"));

        for (int i = 0; i < 5; i++) {
            boolean active = (((ContainerVendor) this.menu).getSyncedValue(10 + i) == 1);
            this.coinButtons[i].active = active;
            // Установка цвета текста через метод Forge IAbstractWidgetExtension
            this.coinButtons[i].setFGColor(active ? 0x55FF55 : 0xA0A0A0);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 6, 5, 4210752, false);

        String priceLabel = Component.translatable("general.label.price").getString();
        int labelWidth = this.font.width(priceLabel);
        guiGraphics.drawString(this.font, priceLabel, 78 - labelWidth, 22, 4210752, false);

        if (!this.textActive) {
            guiGraphics.drawString(this.font, String.valueOf(this.tileEntity.itemPrice), 82, 22, 4210752, false);
        }

        String cSum = String.valueOf(this.tileEntity.coinSum);
        int cSumWidth = this.font.width(cSum);
        guiGraphics.drawString(this.font, cSum, 145 - cSumWidth, 60, 4210752, false);

        guiGraphics.drawString(this.font, Integer.toHexString(this.tileEntity.textColor), 23, 81, 4210752, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.textActive && this.itemPriceField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.textActive && this.itemPriceField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}