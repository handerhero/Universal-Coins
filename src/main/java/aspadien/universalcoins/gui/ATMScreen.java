package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.ATM;
import aspadien.universalcoins.container.ContainerATM;
import java.text.DecimalFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ATMScreen extends AbstractContainerScreen<ContainerATM> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/atm.png");

    private final ATM tileEntity;
    private EditBox textField;
    public int menuState = 0;
    private static final String[] menuStateName = new String[] {
            "welcome", "auth", "main", "additional", "balance", "deposit", "withdraw",
            "newcard", "transferaccount", "takecard", "takecoins", "insufficient",
            "invalid", "badcard", "unauthorized", "newaccount", "processing"
    };

    private int barProgress = 0;
    private final DecimalFormat formatter = new DecimalFormat("#,###,###,###,###,###,###");

    public ATMScreen(ContainerATM container, Inventory inv, Component title) {
        super(container, inv, title);
        this.tileEntity = container.getTile();
        this.imageWidth = 196;
        this.imageHeight = 201;
    }

    @Override
    protected void init() {
        super.init();

        int[] sideButtonY = { 30, 50, 70, 90 };
        for (int i = 0; i < 4; i++) {
            int id = i;
            this.addRenderableWidget(new SlimButton(this.leftPos + 8, this.topPos + sideButtonY[i], 18, 12,
                    Component.empty(), button -> handleButtonClick(id)));
        }

        this.textField = new EditBox(this.font, this.leftPos + 34, this.topPos + 52, 120, 12, Component.empty());
        this.textField.setMaxLength(9);
        this.textField.setBordered(false);
        this.textField.setVisible(false);
        this.textField.setTextColor(4210752);
        this.addWidget(this.textField);
    }

    private void handleButtonClick(int buttonId) {
        int functionID = 0;
        boolean isOwner = (((ContainerATM) this.menu).getSyncedValue(7) == 1);

        switch (this.menuState) {
            case 0:
                functionID = 5;
                this.menuState = 1;
                this.barProgress = 0;
                break;
            case 2:
                if (buttonId == 0) this.menuState = 4;
                if (buttonId == 1) { functionID = 3; this.menuState = 5; }
                if (buttonId == 2) {
                    this.textField.setValue("0");
                    this.menuState = 6;
                    this.textField.setFocused(true);
                    this.setFocused(this.textField);
                }
                if (buttonId == 3) this.menuState = 3;
                break;
            case 3:
                if (buttonId == 0) {
                    if (!isOwner) { this.menuState = 14; }
                    else { functionID = 1; this.menuState = 9; }
                }
                if (buttonId == 1) {
                    if (!isOwner) { this.menuState = 14; }
                    else { this.menuState = 8; }
                }
                if (buttonId == 3) this.menuState = 2;
                break;
            case 4:
            case 5:
                if (buttonId == 3) this.menuState = 2;
                break;
            case 6:
                if (buttonId == 2) {
                    try {
                        int amount = Integer.parseInt(this.textField.getValue());
                        if (amount > 0 && amount <= this.tileEntity.accountBalance) {
                            this.tileEntity.sendServerUpdatePacket(amount);
                            functionID = 4;
                            this.menuState = 10;
                        } else {
                            this.menuState = 12;
                        }
                    } catch (Exception e) {
                        this.menuState = 12;
                    }
                }
                if (buttonId == 3) {
                    this.textField.setValue("0");
                    this.menuState = 2;
                }
                break;
            case 7:
                if (buttonId == 2) { functionID = 1; this.menuState = 9; }
                if (buttonId == 3) this.menuState = 2;
                break;
            case 8:
                if (buttonId == 2) { functionID = 2; this.menuState = 9; }
                if (buttonId == 3) this.menuState = 2;
                break;
            case 9:
                if (buttonId == 3) this.menuState = 0;
                break;
            case 10:
            case 11:
            case 12:
                if (buttonId == 3) this.menuState = 6;
                break;
            case 15:
                if (buttonId == 2) {
                    functionID = 1;
                    this.menuState = 16;
                }
                if (buttonId == 3) this.menuState = 0;
                break;
        }

        if (functionID > 0) {
            this.tileEntity.sendButtonMessage(functionID, hasShiftDown());
        }
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

        if (this.menuState == 0 && !this.tileEntity.getItem(1).isEmpty()) {
            this.menuState = 2;
        }

        if (this.menuState == 1) {
            this.barProgress++;
            guiGraphics.blit(TEXTURE, this.leftPos + 171, this.topPos + 19, 196, 0, 18, 18);
            guiGraphics.blit(TEXTURE, this.leftPos + 34, this.topPos + 43, 0, 201, Math.min(this.barProgress, 128), 5);

            if (this.barProgress > 130) {
                String key = !this.tileEntity.accountNumber.equals("none") ? "atm.auth.success" : "atm.auth.fail";
                drawCenteredLine(guiGraphics, Component.translatable(key).getString(), this.leftPos + 98, this.topPos + 72);
                if (this.barProgress > 170) {
                    if (!this.tileEntity.accountNumber.equals("none")) {
                        this.menuState = 2;
                    } else {
                        this.menuState = 15;
                    }
                    this.barProgress = 0;
                }
            }
        }

        if (this.menuState == 12 || this.menuState == 13 || this.menuState == 14 || this.menuState == 16) {
            this.barProgress++;
            if (this.barProgress > 100) {
                if (this.menuState == 12) { this.menuState = 6; }
                else if (this.menuState == 13) { this.menuState = 0; }
                else if (this.menuState == 14) { this.menuState = 2; }
                else if (this.menuState == 16) { this.menuState = 9; }
                this.barProgress = 0;
            }
        }

        this.textField.setVisible(this.menuState == 6);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.tileEntity.getDisplayName(), 6, 5, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 6, this.imageHeight - 94, 4210752, false);

        if (this.menuState == 4 || this.menuState == 5) {
            guiGraphics.drawString(this.font, this.formatter.format(this.tileEntity.accountBalance), 34, 52, 4210752, false);
        }

        if (this.menuState == 6) {
            guiGraphics.drawString(this.font, this.formatter.format(this.tileEntity.accountBalance), 34, 32, 4210752, false);
            String textToDraw = this.textField.getValue() + ((System.currentTimeMillis() / 500L % 2L == 0L) ? "|" : "");
            guiGraphics.drawString(this.font, textToDraw, 34, 52, 4210752, false);
        }

        drawMenuLines(guiGraphics, this.menuState);
    }

    private void drawMenuLines(GuiGraphics guiGraphics, int state) {
        int[] yCoords = { 22, 32, 42, 52, 62, 72, 82, 92 };
        String[] suffixes = { ".one", ".two", ".three", ".four", ".five", ".six", ".seven", ".eight" };

        for (int i = 0; i < 8; i++) {
            if (state != 6 || (i != 1 && i != 3)) {
                if ((state != 4 && state != 5) || i != 3) {
                    String key = "atm." + menuStateName[state] + suffixes[i];
                    String text = Component.translatable(key).getString();

                    if (text.startsWith("C:")) {
                        drawCenteredLine(guiGraphics, text.substring(2), 98, yCoords[i]);
                    } else if (!text.equals(key)) {
                        guiGraphics.drawString(this.font, text, 34, yCoords[i], 4210752, false);
                    }
                }
            }
        }
    }

    private void drawCenteredLine(GuiGraphics guiGraphics, String text, int xCenter, int y) {
        int width = this.font.width(text);
        guiGraphics.drawString(this.font, text, xCenter - width / 2, y, 4210752, false);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.menuState == 6 && this.textField.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.menuState == 6 && this.textField.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}