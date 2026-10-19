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
import aspadien.universalcoins.container.ContainerVendorWrench;
import aspadien.universalcoins.tileentity.TileVendor;

public class VendorWrenchScreen extends ContainerScreen<ContainerVendorWrench> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_wrench.png");
    private final TileVendor tileEntity;
    private TextFieldWidget blockOwnerField;
    private Button infiniteButton;

    public VendorWrenchScreen(ContainerVendorWrench container, PlayerInventory inv, ITextComponent title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 166;
        this.imageHeight = 80;
    }

    protected void init() {
        super.init();

        // БЕЗОПАСНОЕ ПОЛУЧЕНИЕ ИМЕНИ: если блок без владельца, ставим пустую строку
        String ownerName = (this.tileEntity != null && this.tileEntity.blockOwner != null) ? this.tileEntity.blockOwner : "";

        this.blockOwnerField = new TextFieldWidget(this.font, this.leftPos + 12, this.topPos + 20, 138, 12, StringTextComponent.EMPTY);
        this.blockOwnerField.setMaxLength(100);
        this.blockOwnerField.setValue(ownerName);
        this.blockOwnerField.setBordered(true);
        this.children.add(this.blockOwnerField);

        addButton((Widget)new SlimButton(this.leftPos + 68, this.topPos + 34, 40, 12, (ITextComponent)new TranslationTextComponent("general.button.edit"), b -> this.blockOwnerField.setFocus(true)));

        addButton((Widget)new SlimButton(this.leftPos + 110, this.topPos + 34, 40, 12, (ITextComponent)new TranslationTextComponent("general.button.save"), b -> {
            if (this.tileEntity != null) {
                this.tileEntity.blockOwner = this.blockOwnerField.getValue();
                this.tileEntity.sendServerUpdateMessage();
            }
            this.blockOwnerField.setFocus(false);
        }));

        this.infiniteButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 9, this.topPos + 54, 148, 12, StringTextComponent.EMPTY, b -> {
            if (this.tileEntity != null) {
                this.tileEntity.infiniteMode = !this.tileEntity.infiniteMode;
                this.tileEntity.sendServerUpdateMessage();
                updateButtonText();
            }
        }));

        updateButtonText();
    }

    private void updateButtonText() {
        boolean isInfinite = this.tileEntity != null && this.tileEntity.infiniteMode;
        this.infiniteButton.setMessage(isInfinite ? (ITextComponent)new TranslationTextComponent("vendor.wrench.infiniteon") : (ITextComponent)new TranslationTextComponent("vendor.wrench.infiniteoff"));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.blockOwnerField.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("vendor.wrench.owner"), 6.0F, 5.0F, 4210752);
    }

    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.blockOwnerField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (this.blockOwnerField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }
}