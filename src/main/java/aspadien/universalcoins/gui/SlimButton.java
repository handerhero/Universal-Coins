/*    */ package aspadien.universalcoins.gui;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.gui.widget.button.Button;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ 
/*    */ public class SlimButton extends Button {
/* 11 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/buttons.png");
/*    */   
/*    */   public SlimButton(int x, int y, int width, int height, ITextComponent title, Button.IPressable pressedAction) {
/* 14 */     super(x, y, width, height, title, pressedAction);
/*    */   }
/*    */ 
/*    */   
/*    */   public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 19 */     Minecraft mc = Minecraft.getInstance();
/* 20 */     mc.getTextureManager().bind(TEXTURE);
/*    */ 
/*    */     
/* 23 */     int k = !this.active ? 2 : (isHovered() ? 1 : 0);
/*    */     
/* 25 */     RenderSystem.enableBlend();
/* 26 */     RenderSystem.defaultBlendFunc();
/* 27 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
/*    */ 
/*    */ 
/*    */     
/* 31 */     int vOffset = 54 + k * 12;
/*    */ 
/*    */     
/* 34 */     blit(matrixStack, this.x, this.y, 0, vOffset, this.width / 2, this.height);
/* 35 */     blit(matrixStack, this.x + this.width / 2, this.y, 90 - this.width / 2, vOffset, this.width / 2, this.height);
/*    */     
/* 37 */     int textColor = this.active ? (isHovered() ? 16777120 : 14737632) : 10526880;
/* 38 */     drawCenteredString(matrixStack, mc.font, getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, textColor);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/SlimButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */