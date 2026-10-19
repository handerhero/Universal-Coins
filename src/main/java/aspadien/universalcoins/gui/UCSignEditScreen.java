package aspadien.universalcoins.gui;

import aspadien.universalcoins.blockentities.UCSign;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class UCSignEditScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/sign.png");
    private final UCSign tileSign;
    private int editLine = 0;
    private int updateCounter = 0;

    public UCSignEditScreen(UCSign tileEntity) {
        super(Component.translatable("sign.edit"));
        this.tileSign = tileEntity;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(this.width / 2 - 50, this.height / 2 + 50, 100, 20)
                .build());
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.tileSign.blockOwner = this.minecraft.player.getName().getString();
        }
        this.tileSign.sendServerUpdateMessage();
        super.onClose();
    }

    @Override
    public void tick() {
        super.tick();
        this.updateCounter++;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) { // Стрелка вверх
            this.editLine = (this.editLine - 1) & 0x3;
            return true;
        }
        if (keyCode == 264 || keyCode == 257) { // Стрелка вниз или Enter
            this.editLine = (this.editLine + 1) & 0x3;
            return true;
        }
        if (keyCode == 259) { // Backspace
            String s = this.tileSign.getMessage(this.editLine).getString();
            if (!s.isEmpty()) {
                this.tileSign.setMessage(this.editLine, Component.literal(s.substring(0, s.length() - 1)));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        String s = this.tileSign.getMessage(this.editLine).getString();
        if (s.length() < 15) {
            this.tileSign.setMessage(this.editLine, Component.literal(s + codePoint));
        }
        return true;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);

        int x = (this.width - 100) / 2;
        int y = (this.height - 50) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, 100, 50);

        int[] lineY = {-20, -8, 4, 16};
        for (int i = 0; i < 4; i++) {
            String s = this.tileSign.getMessage(i).getString();
            int sw = this.font.width(s);
            int color = 0;

            int lineX = (int) (this.width / 2.0F - (sw / 2.0F));
            int lineCurY = this.height / 2 + lineY[i];

            guiGraphics.drawString(this.font, s, lineX, lineCurY, color, false);

            if (i == this.editLine && (this.updateCounter / 6) % 2 == 0) {
                guiGraphics.drawString(this.font, ">", lineX - 10, lineCurY, color, false);
                guiGraphics.drawString(this.font, "<", lineX + sw + 5, lineCurY, color, false);
            }
        }
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
}