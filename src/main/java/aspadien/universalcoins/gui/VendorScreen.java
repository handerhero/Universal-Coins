package aspadien.universalcoins.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import aspadien.universalcoins.container.ContainerVendor;
import aspadien.universalcoins.tileentity.TileVendor;

public class VendorScreen extends ContainerScreen<ContainerVendor> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor.png");
    private final TileVendor tileEntity;
    private TextFieldWidget itemPriceField;
    private Button modeButton;
    private boolean textActive = false;

    // Массив для хранения кнопок монет, чтобы мы могли менять их статус
    private final Button[] coinButtons = new Button[5];

    public VendorScreen(ContainerVendor container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 200;
    }

    protected void init() {
        super.init();

        this.modeButton = (Button)addButton((Widget)new Button(this.leftPos + 8, this.topPos + 35, 62, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));

        addButton((Widget)new Button(this.leftPos + 79, this.topPos + 35, 44, 12, (ITextComponent)new TranslationTextComponent("general.button.edit"), b -> {
            this.itemPriceField.setValue(String.valueOf(this.tileEntity.itemPrice));
            this.textActive = true;
            this.itemPriceField.setFocus(true);
            this.tileEntity.sendButtonMessage(1, hasShiftDown());
        }));

        addButton((Widget)new Button(this.leftPos + 124, this.topPos + 35, 44, 12, (ITextComponent)new TranslationTextComponent("general.button.save"), b -> {
            try {
                this.tileEntity.itemPrice = Integer.parseInt(this.itemPriceField.getValue());
            } catch (NumberFormatException ignored) {}
            this.textActive = false;
            this.itemPriceField.setFocus(false);
            this.tileEntity.sendServerUpdateMessage();
            this.tileEntity.sendButtonMessage(2, hasShiftDown());
        }));

        // Создаем кнопки для монет через цикл и задаем им кастомный цвет текста
        String[] coinLabels = {"I", "G", "E", "D", "O"};
        for (int i = 0; i < 5; i++) {
            int index = i;
            this.coinButtons[i] = (Button)addButton((Widget)new Button(this.leftPos + 56 + (i * 18), this.topPos + 74, 18, 18, new StringTextComponent(coinLabels[i]), b -> this.tileEntity.sendButtonMessage(3 + index, hasShiftDown())) {
                @Override
                public int getFGColor() {
                    // Возвращает ЗЕЛЕНЫЙ цвет, если кнопка активна, и СЕРЫЙ, если не активна
                    return this.active ? 0x55FF55 : 0xA0A0A0;
                }
            });
        }

        addButton((Widget)new Button(this.leftPos + 7, this.topPos + 78, 12, 12, (ITextComponent)new StringTextComponent("-"), b -> this.tileEntity.sendButtonMessage(8, hasShiftDown())));
        addButton((Widget)new Button(this.leftPos + 33, this.topPos + 78, 12, 12, (ITextComponent)new StringTextComponent("+"), b -> this.tileEntity.sendButtonMessage(9, hasShiftDown())));

        this.itemPriceField = new TextFieldWidget(this.font, this.leftPos + 82, this.topPos + 22, 86, 12, StringTextComponent.EMPTY);
        this.itemPriceField.setMaxLength(10);
        this.itemPriceField.setBordered(false);
        this.itemPriceField.setVisible(true);
        this.itemPriceField.setTextColor(16777215);
        this.children.add(this.itemPriceField);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.modeButton.setMessage(this.tileEntity.sellMode ? (ITextComponent)new TranslationTextComponent("general.button.sell") : (ITextComponent)new TranslationTextComponent("general.button.buy"));

        // ОБНОВЛЯЕМ СТАТУС АКТИВНОСТИ КНОПОК ИЗ СИНХРОНИЗИРОВАННЫХ ДАННЫХ
        // В TileVendor.vendorData индексы с 10 по 14 отвечают за кнопки владельца
        for (int i = 0; i < 5; i++) {
            this.coinButtons[i].active = (((ContainerVendor)this.menu).getSyncedValue(10 + i) == 1);
        }
    }

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 6.0F, 5.0F, 4210752);

        String priceLabel = (new TranslationTextComponent("general.label.price")).getString();
        int labelWidth = this.font.width(priceLabel);
        this.font.draw(matrixStack, priceLabel, (78 - labelWidth), 22.0F, 4210752);

        if (!this.textActive) {
            this.font.draw(matrixStack, String.valueOf(this.tileEntity.itemPrice), 82.0F, 22.0F, 4210752);
        } else {
            this.itemPriceField.render(matrixStack, mouseX, mouseY, 0.0F);
        }

        String cSum = String.valueOf(this.tileEntity.coinSum);
        int cSumWidth = this.font.width(cSum);
        this.font.draw(matrixStack, cSum, (145 - cSumWidth), 60.0F, 4210752);

        this.font.draw(matrixStack, Integer.toHexString(this.tileEntity.textColor), 23.0F, 81.0F, 4210752);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.textActive && this.itemPriceField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (this.textActive && this.itemPriceField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}