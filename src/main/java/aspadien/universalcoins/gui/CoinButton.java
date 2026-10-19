package aspadien.universalcoins.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

public class CoinButton extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/buttons.png");
    private final int coinIndex;

    public CoinButton(int x, int y, int index, OnPress pressedAction) {
        super(x, y, 18, 18, Component.empty(), pressedAction, DEFAULT_NARRATION);
        this.coinIndex = index;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int i = this.isHoveredOrFocused() ? 1 : 0;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (!this.active) {
            // Тёмно-серый оттенок для неактивной кнопки
            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
        } else {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), this.coinIndex * this.width, i * this.height, this.width, this.height);

        // Обязательно сбрасываем цвет шейдера обратно на белый
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}