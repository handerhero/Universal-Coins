/*    */ package aspadien.universalcoins.gui;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
/*    */ import java.text.DecimalFormat;
/*    */ import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.container.ContainerSafe;
/*    */ import aspadien.universalcoins.tileentity.TileSafe;
/*    */ 
/*    */ public class SafeScreen extends ContainerScreen<ContainerSafe> {
/* 16 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/safe.png");
/*    */   private final TileSafe tileEntity;
/* 18 */   private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
/*    */   
/*    */   public SafeScreen(ContainerSafe container, PlayerInventory inv, ITextComponent title) {
/* 21 */     super((ContainerSafe) container, inv, title);
/* 22 */     this.tileEntity = container.getTile();
/* 23 */     this.imageWidth = 176;
/* 24 */     this.imageHeight = 152;
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 29 */     renderBackground(matrixStack);
/* 30 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 31 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 36 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 6.0F, 5.0F, 4210752);
/* 37 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 6.0F, 58.0F, 4210752);
/*    */ 
/*    */     
/* 40 */     long low = ((ContainerSafe)this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
/* 41 */     long high = ((ContainerSafe)this.menu).getSyncedValue(1);
/* 42 */     long currentBalance = high << 32L | low;
/*    */     
/* 44 */     String formattedBalance = this.formatter.format(currentBalance);
/* 45 */     int balLength = this.font.width(formattedBalance);
/*    */ 
/*    */     
/* 48 */     this.font.draw(matrixStack, formattedBalance, (154 - balLength), 22.0F, 4210752);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 53 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 54 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 55 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/SafeScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */