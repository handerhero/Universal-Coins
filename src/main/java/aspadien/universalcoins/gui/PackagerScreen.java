package aspadien.universalcoins.gui;

import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.blockentities.Packager;
import aspadien.universalcoins.container.ContainerPackager;
import com.mojang.blaze3d.platform.InputConstants;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public class PackagerScreen extends AbstractContainerScreen<ContainerPackager> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/packager.png");
    private final Packager tileEntity;

    private Button smallButton;
    private Button medButton;
    private Button largeButton;
    private Button actionButton;
    private Button coinButton;
    private Button modeButton;
    private EditBox receiverField;

    private int lastSyncedMode = -1;

    public PackagerScreen(ContainerPackager container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 176;
        this.imageHeight = 190;
    }

    @Override
    protected void init() {
        super.init();

        this.modeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 110, this.topPos + 4, 60, 12,
                Component.empty(), b -> this.tileEntity.sendButtonMessage(5, hasShiftDown())));

        this.smallButton = this.addRenderableWidget(new SlimButton(this.leftPos + 87, this.topPos + 20, 50, 12,
                Component.translatable("packager.button.small"), b -> this.tileEntity.sendButtonMessage(2, hasShiftDown())));
        this.medButton = this.addRenderableWidget(new SlimButton(this.leftPos + 87, this.topPos + 32, 50, 12,
                Component.translatable("packager.button.medium"), b -> this.tileEntity.sendButtonMessage(3, hasShiftDown())));
        this.largeButton = this.addRenderableWidget(new SlimButton(this.leftPos + 87, this.topPos + 44, 50, 12,
                Component.translatable("packager.button.large"), b -> this.tileEntity.sendButtonMessage(4, hasShiftDown())));

        this.actionButton = this.addRenderableWidget(new SlimButton(this.leftPos + 123, this.topPos + 58, 46, 12,
                Component.empty(), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));
        this.coinButton = this.addRenderableWidget(new SlimButton(this.leftPos + 123, this.topPos + 91, 46, 12,
                Component.translatable("general.button.coin"), b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));

        this.receiverField = new EditBox(this.font, this.leftPos + 30, this.topPos + 30, 100, 12, Component.empty());
        this.receiverField.setMaxLength(24);
        this.receiverField.setBordered(false);
        this.addRenderableWidget(this.receiverField);

        updateUI((((ContainerPackager) this.menu).getSyncedValue(3) == 1));
    }

    private void updateUI(boolean isSendMode) {
        if (isSendMode) {
            this.modeButton.setMessage(Component.translatable("packager.button.mode.send"));
            this.largeButton.visible = false;
            this.receiverField.setVisible(true);
            updateSlots(3);
        } else {
            this.modeButton.setMessage(Component.translatable("packager.button.mode.buy"));
            this.largeButton.visible = true;
            this.receiverField.setVisible(false);
            updateSlots(((ContainerPackager) this.menu).getSyncedValue(2));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        boolean isSendMode = (((ContainerPackager) this.menu).getSyncedValue(3) == 1);
        int currentSize = ((ContainerPackager) this.menu).getSyncedValue(2);

        this.actionButton.active = (((ContainerPackager) this.menu).getSyncedValue(4) == 1);
        this.coinButton.active = (((ContainerPackager) this.menu).getSyncedValue(5) == 1);

        if (isSendMode) {
            guiGraphics.blit(TEXTURE, this.leftPos + 3, this.topPos + 20, 0, 190, 169, 40);
            this.modeButton.setMessage(Component.translatable("packager.button.mode.send"));
            this.actionButton.setMessage(Component.translatable("general.button.send"));
            this.largeButton.visible = false;
            this.receiverField.setVisible(true);
            updateSlots(3);
        } else {
            this.modeButton.setMessage(Component.translatable("packager.button.mode.buy"));
            this.actionButton.setMessage(Component.translatable("general.button.buy"));
            this.largeButton.visible = true;
            this.receiverField.setVisible(false);
            updateSlots(currentSize);

            int[] yHighlight = { 24, 36, 48 };
            if (currentSize >= 0 && currentSize < yHighlight.length) {
                guiGraphics.blit(TEXTURE, this.leftPos + 80, this.topPos + yHighlight[currentSize], 176, 0, 6, 6);
            }
            if (currentSize == 1) guiGraphics.blit(TEXTURE, this.leftPos + 25, this.topPos + 21, 176, 6, 18, 36);
            if (currentSize == 2) guiGraphics.blit(TEXTURE, this.leftPos + 7, this.topPos + 21, 176, 6, 36, 36);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 8, 5, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 96, 4210752, false);

        int cost = -1;
        if (this.smallButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager) this.menu).getSyncedValue(6); }
        else if (this.medButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager) this.menu).getSyncedValue(7); }
        else if (this.largeButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager) this.menu).getSyncedValue(8); }

        if (cost != -1 && ((ContainerPackager) this.menu).getSyncedValue(3) == 0) {
            String text = "Cost: " + cost;
            guiGraphics.drawString(this.font, text, 144 - this.font.width(text), 78, 4210752, false);
        } else {
            long low = ((ContainerPackager) this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
            long high = ((long) ((ContainerPackager) this.menu).getSyncedValue(1)) << 32L;
            long currentBalance = high | low;

            String coinStr = (new DecimalFormat("#,###,###,###")).format(currentBalance);
            guiGraphics.drawString(this.font, coinStr, 144 - this.font.width(coinStr), 78, 4210752, false);
        }
    }

    private void setSlotPos(Slot slot, int x, int y) {
        try {
            // SRG-названия полей для 1.20.1: f_40220_ (x), f_40221_ (y)
            ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, x, "f_40220_");
            ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, y, "f_40221_");
        } catch (Exception e) {
            UniversalCoins.LOGGER.error("Failed to move slot: " + e.getMessage());
        }
    }

    private void updateSlots(int mode) {
        if (getMenu() == null) return;

        int[] slotX = { 8, 26, 44, 62 };
        int[] slotY = { 22, 40 };

        for (int i = 0; i < 8; i++) {
            Slot slot = ((ContainerPackager) getMenu()).getSlot(i);
            int newX = 10000;
            int newY = slotY[i % 2];

            if (mode == 0) {
                if (i >= 4) newX = slotX[i / 2];
            } else if (mode == 1) {
                if (i >= 2) newX = slotX[i / 2];
            } else if (mode == 2) {
                newX = slotX[i / 2];
            }
            setSlotPos(slot, newX, newY);
        }

        int inputX = (mode == 3) ? 8 : 10000;
        int inputY = (mode == 3) ? 26 : 22;
        setSlotPos(((ContainerPackager) getMenu()).getSlot(11), inputX, inputY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.receiverField.isVisible() && this.receiverField.isFocused()) {
            if (keyCode == 258) { // Tab
                this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), true);
                return true;
            }

            if (keyCode == 256) { // Esc
                return super.keyPressed(keyCode, scanCode, modifiers);
            }

            if (this.minecraft != null && this.minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
                return true; // Блокируем закрытие GUI на клавишу инвентаря
            }

            if (this.receiverField.keyPressed(keyCode, scanCode, modifiers)) {
                this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), false);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int m) {
        if (this.receiverField.isVisible() && this.receiverField.charTyped(c, m)) {
            this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), false);
            return true;
        }
        return super.charTyped(c, m);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int currentMode = ((ContainerPackager) this.menu).getSyncedValue(3);
        if (currentMode != this.lastSyncedMode) {
            updateUI(currentMode == 1);
            this.lastSyncedMode = currentMode;
        }

        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}