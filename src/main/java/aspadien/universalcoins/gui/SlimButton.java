package aspadien.universalcoins.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SlimButton extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/buttons.png");

    public SlimButton(int x, int y, int width, int height, Component title, OnPress pressedAction, CreateNarration narration) {
        super(x, y, width, height, title, pressedAction, narration);
    }

    // Удобный перегруженный конструктор с дефолтной озвучкой (как обычно делают в 1.20)
    public SlimButton(int x, int y, int width, int height, Component title, OnPress pressedAction) {
        this(x, y, width, height, title, pressedAction, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        // 0: обычная, 1: наведённая, 2: неактивная (disabled)
        int k = !this.active ? 2 : (this.isHoveredOrFocused() ? 1 : 0);
        int vOffset = 54 + k * 12;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // В 1.20.1 цвет шейдера настраивается через setShaderColor
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        int halfWidth = this.width / 2;

        // guiGraphics.blit первым аргументом принимает ResourceLocation
        // Рисуем левую половину кнопки
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), 0, vOffset, halfWidth, this.height);
        // Рисуем правую половину кнопки (с учётом возможной нечётной ширины)
        guiGraphics.blit(TEXTURE, this.getX() + halfWidth, this.getY(), 90 - (this.width - halfWidth), vOffset, this.width - halfWidth, this.height);

        // Сбрасываем цвет шейдера обратно на белый
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Отрисовка текста
        int textColor = this.active ? (this.isHoveredOrFocused() ? 16777120 : 14737632) : 10526880;

        guiGraphics.drawCenteredString(
                mc.font,
                this.getMessage(),
                this.getX() + this.width / 2,
                this.getY() + (this.height - 8) / 2,
                textColor
        );
    }
}