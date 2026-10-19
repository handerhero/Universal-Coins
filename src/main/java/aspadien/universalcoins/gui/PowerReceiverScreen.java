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
/*    */ import aspadien.universalcoins.container.ContainerPowerReceiver;
/*    */ import aspadien.universalcoins.tileentity.TilePowerReceiver;
/*    */ 
/*    */ public class PowerReceiverScreen extends ContainerScreen<ContainerPowerReceiver> {
/* 17 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/power_receiver.png");
/*    */   private final TilePowerReceiver tileEntity;
/* 19 */   private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
/*    */   private Button accessModeButton;
/*    */   
/*    */   public PowerReceiverScreen(ContainerPowerReceiver container, PlayerInventory inv, ITextComponent title) {
/* 23 */     super((ContainerPowerReceiver) container, inv, title);
/* 24 */     this.tileEntity = container.getTile();
/* 25 */     this.imageWidth = 176;
/* 26 */     this.imageHeight = 184;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 31 */     super.init();
/*    */ 
/*    */     
/* 34 */     addButton((Widget)new SlimButton(this.leftPos + 123, this.topPos + 89, 46, 12, (ITextComponent)new TranslationTextComponent("general.button.coin"), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 40 */     this.accessModeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 122, this.topPos + 4, 50, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 47 */     renderBackground(matrixStack);
/* 48 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 49 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 55 */     this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
/*    */ 
/*    */     
/* 58 */     long low = ((ContainerPowerReceiver)this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
/* 59 */     long high = ((ContainerPowerReceiver)this.menu).getSyncedValue(1) << 32L;
/* 60 */     long coinSum = high | low;
/*    */ 
/*    */     
/* 63 */     int feLevel = ((ContainerPowerReceiver)this.menu).getSyncedValue(2);
/* 64 */     int feOutput = ((ContainerPowerReceiver)this.menu).getSyncedValue(3);
/*    */ 
/*    */     
/* 67 */     long wLow = ((ContainerPowerReceiver)this.menu).getSyncedValue(4) & 0xFFFFFFFFL;
/* 68 */     long wHigh = ((ContainerPowerReceiver)this.menu).getSyncedValue(5) << 32L;
/* 69 */     long worldPower = wHigh | wLow;
/*    */ 
/*    */     
/* 72 */     this.font.draw(matrixStack, "Coins: " + coinSum, 22.0F, 24.0F, 4210752);
/* 73 */     this.font.draw(matrixStack, "Internal: " + feLevel + " FE", 54.0F, 43.0F, 4210752);
/* 74 */     this.font.draw(matrixStack, "Output: " + feOutput + " FE/t", 54.0F, 59.0F, 4210752);
/* 75 */     this.font.draw(matrixStack, "Network: " + worldPower + " FE", 54.0F, 76.0F, 4210752);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 80 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 81 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 82 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */     
/* 84 */     this.accessModeButton.setMessage(this.tileEntity.publicAccess ? (ITextComponent)new TranslationTextComponent("general.label.public") : (ITextComponent)new TranslationTextComponent("general.label.private"));
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/PowerReceiverScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */