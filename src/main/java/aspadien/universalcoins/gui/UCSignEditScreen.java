/*    */ package aspadien.universalcoins.gui;
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import net.minecraft.client.gui.screen.Screen;
/*    */ import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.tileentity.TileUCSign;
/*    */ 
/*    */ public class UCSignEditScreen extends Screen {
/* 12 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/sign.png");
/*    */   private final TileUCSign tileSign;
/* 14 */   private int editLine = 0;
/* 15 */   private int updateCounter = 0;
/*    */   
/*    */   public UCSignEditScreen(TileUCSign tileEntity) {
/* 18 */     super((ITextComponent)new TranslationTextComponent("sign.edit"));
/* 19 */     this.tileSign = tileEntity;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 24 */     this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
/* 25 */     addButton((Widget)new Button(this.width / 2 - 50, this.height / 2 + 50, 100, 20, (ITextComponent)new StringTextComponent("Done"), b -> onClose()));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onClose() {
/* 32 */     this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
/* 33 */     this.tileSign.blockOwner = this.minecraft.player.getName().getString();
/* 34 */     this.tileSign.sendServerUpdateMessage();
/* 35 */     super.onClose();
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick() {
/* 40 */     this.updateCounter++;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
/* 45 */     if (keyCode == 265) {
/* 46 */       this.editLine = this.editLine - 1 & 0x3;
/* 47 */       return true;
/*    */     } 
/* 49 */     if (keyCode == 264 || keyCode == 257) {
/* 50 */       this.editLine = this.editLine + 1 & 0x3;
/* 51 */       return true;
/*    */     } 
/* 53 */     if (keyCode == 259) {
/* 54 */       String s = this.tileSign.getMessage(this.editLine).getString();
/* 55 */       if (s.length() > 0) {
/* 56 */         this.tileSign.setMessage(this.editLine, (ITextComponent)new StringTextComponent(s.substring(0, s.length() - 1)));
/*    */       }
/* 58 */       return true;
/*    */     } 
/* 60 */     return super.keyPressed(keyCode, scanCode, modifiers);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean charTyped(char codePoint, int modifiers) {
/* 65 */     String s = this.tileSign.getMessage(this.editLine).getString();
/* 66 */     if (s.length() < 15) {
/* 67 */       this.tileSign.setMessage(this.editLine, (ITextComponent)new StringTextComponent(s + codePoint));
/*    */     }
/* 69 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 74 */     renderBackground(matrixStack);
/* 75 */     drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 40, 16777215);
/*    */     
/* 77 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 78 */     int x = (this.width - 100) / 2;
/* 79 */     int y = (this.height - 50) / 2;
/* 80 */     blit(matrixStack, x, y, 0, 0, 100, 50);
/*    */     
/* 82 */     int[] lineY = { -20, -8, 4, 16 };
/* 83 */     for (int i = 0; i < 4; i++) {
/* 84 */       String s = this.tileSign.getMessage(i).getString();
/* 85 */       int sw = this.font.width(s);
/* 86 */       int color = 0;
/*    */       
/* 88 */       this.font.draw(matrixStack, s, this.width / 2.0F - (sw / 2), (this.height / 2 + lineY[i]), color);
/*    */ 
/*    */       
/* 91 */       if (i == this.editLine && this.updateCounter / 6 % 2 == 0) {
/* 92 */         this.font.draw(matrixStack, ">", this.width / 2.0F - (sw / 2) - 10.0F, (this.height / 2 + lineY[i]), color);
/* 93 */         this.font.draw(matrixStack, "<", this.width / 2.0F + (sw / 2) + 5.0F, (this.height / 2 + lineY[i]), color);
/*    */       } 
/*    */     } 
/* 96 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/UCSignEditScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */