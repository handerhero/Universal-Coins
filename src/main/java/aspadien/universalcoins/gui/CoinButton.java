package aspadien.universalcoins.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;


class CoinButton extends Button {
  private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/buttons.png");
  private final int coinIndex;

  public CoinButton(int x, int y, int index, Button.IPressable pressedAction) {
    super(x, y, 18, 18, StringTextComponent.EMPTY, pressedAction);
    this.coinIndex = index;
  }


  public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    Minecraft mc = Minecraft.getInstance();
    mc.getTextureManager().bind(TEXTURE);

    int i = isHovered() ? 1 : 0;
            if (!this.active) {
        // Делаем текстуру тёмно-серой, если кнопка выключена
                RenderSystem.color4f(0.3F, 0.3F, 0.3F, 1.0F);
            } else {
        // Обычный яркий цвет, если активна
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
    blit(matrixStack, this.x, this.y, this.coinIndex * this.width, i * this.height, this.width, this.height);
  }
}
