/*    */ package aspadien.universalcoins.gui;
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import java.text.DecimalFormat;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*    */ import net.minecraft.client.gui.widget.Widget;
/*    */ import net.minecraft.client.gui.widget.button.Button;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.container.ContainerPowerTransmitter;
/*    */ import aspadien.universalcoins.tileentity.TilePowerTransmitter;
/*    */ 
/*    */ public class PowerTransmitterScreen extends ContainerScreen<ContainerPowerTransmitter> {
/* 17 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/power_transmitter.png");
/*    */   private final TilePowerTransmitter tileEntity;
/* 19 */   private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
/*    */   private Button accessModeButton;
/*    */   
/*    */   public PowerTransmitterScreen(ContainerPowerTransmitter container, PlayerInventory inv, ITextComponent title) {
/* 23 */     super((ContainerPowerTransmitter) container, inv, title);
/* 24 */     this.tileEntity = container.getTile();
/* 25 */     this.imageWidth = 176;
/* 26 */     this.imageHeight = 157;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 31 */     super.init();
/*    */     
/* 33 */     addButton((Widget)new SlimButton(this.leftPos + 123, this.topPos + 60, 46, 12, (ITextComponent)new TranslationTextComponent("general.button.coin"), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */     this.accessModeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 122, this.topPos + 4, 50, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 46 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 6.0F, 5.0F, 4210752);
/* 47 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 6.0F, 63.0F, 4210752);
/*    */ 
/*    */ 
/*    */     
/* 51 */     long low = ((ContainerPowerTransmitter)this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
/* 52 */     long high = ((ContainerPowerTransmitter)this.menu).getSyncedValue(1) << 32L;
/* 53 */     long currentCoinSum = high | low;
/*    */ 
/*    */     
/* 56 */     int currentKfeSold = ((ContainerPowerTransmitter)this.menu).getSyncedValue(3);
/*    */ 
/*    */     
/* 59 */     String formattedkfe = this.formatter.format(currentKfeSold) + " kFE";
/* 60 */     this.font.draw(matrixStack, formattedkfe, (130 - this.font.width(formattedkfe)), 26.0F, 4210752);
/*    */ 
/*    */     
/* 63 */     String formattedBalance = this.formatter.format(currentCoinSum);
/* 64 */     this.font.draw(matrixStack, formattedBalance, (131 - this.font.width(formattedBalance)), 48.0F, 4210752);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 69 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 70 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 71 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */ 
/*    */     
/* 74 */     boolean isPublic = (((ContainerPowerTransmitter)this.menu).getSyncedValue(4) == 1);
/* 75 */     this.accessModeButton.setMessage(isPublic ? (ITextComponent)new TranslationTextComponent("general.label.public") : (ITextComponent)new TranslationTextComponent("general.label.private"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 82 */     renderBackground(matrixStack);
/* 83 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 84 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/PowerTransmitterScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */