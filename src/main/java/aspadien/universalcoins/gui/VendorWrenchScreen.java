package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.Vendor;
import aspadien.universalcoins.container.ContainerVendorWrench;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class VendorWrenchScreen extends AbstractContainerScreen<ContainerVendorWrench> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_wrench.png");
    private final Vendor tileEntity;
    private EditBox blockOwnerField;
    private Button infiniteButton;

    public VendorWrenchScreen(ContainerVendorWrench container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 166;
        this.imageHeight = 80;
    }

    @Override
    protected void init() {
        super.init();

        String ownerName = (this.tileEntity != null && this.tileEntity.blockOwner != null) ? this.tileEntity.blockOwner : "";

        this.blockOwnerField = new EditBox(this.font, this.leftPos + 12, this.topPos + 20, 138, 12, Component.empty());
        this.blockOwnerField.setMaxLength(100);
        this.blockOwnerField.setValue(ownerName);
        this.blockOwnerField.setBordered(true);
        this.addRenderableWidget(this.blockOwnerField);

        this.addRenderableWidget(new SlimButton(this.leftPos + 68, this.topPos + 34, 40, 12,
                Component.translatable("general.button.edit"), b -> {
            this.blockOwnerField.setFocused(true);
            this.setFocused(this.blockOwnerField);
        }));

        this.addRenderableWidget(new SlimButton(this.leftPos + 110, this.topPos + 34, 40, 12,
                Component.translatable("general.button.save"), b -> {
            if (this.tileEntity != null) {
                this.tileEntity.blockOwner = this.blockOwnerField.getValue();
                this.tileEntity.sendServerUpdateMessage();
            }
            this.blockOwnerField.setFocused(false);
        }));

        this.infiniteButton = this.addRenderableWidget(new SlimButton(this.leftPos + 9, this.topPos + 54, 148, 12,
                Component.empty(), b -> {
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
        this.infiniteButton.setMessage(isInfinite
                ? Component.translatable("vendor.wrench.infiniteon")
                : Component.translatable("vendor.wrench.infiniteoff"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, Component.translatable("vendor.wrench.owner"), 6, 5, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.blockOwnerField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.blockOwnerField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }
}