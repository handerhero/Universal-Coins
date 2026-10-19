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
/*    */ import aspadien.universalcoins.container.ContainerSignal;
/*    */ import aspadien.universalcoins.tileentity.TileSignal;
/*    */ 
/*    */ public class SignalScreen extends ContainerScreen<ContainerSignal> {
/* 17 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/signal.png");
/*    */   private final TileSignal tileEntity;
/* 19 */   private final DecimalFormat formatter = new DecimalFormat("#,###,###,###");
/*    */   
/*    */   private Button coinButton;
/*    */   
/*    */   public static final int idCoinButton = 0;
/*    */   public static final int idDurMinusButton = 1;
/*    */   public static final int idDurPlusButton = 2;
/*    */   public static final int idFeeMinusButton = 3;
/*    */   public static final int idFeePlusButton = 4;
/*    */   
/*    */   public SignalScreen(ContainerSignal container, PlayerInventory inv, ITextComponent title) {
/* 30 */     super((ContainerSignal) container, inv, title);
/* 31 */     this.tileEntity = container.getTile();
/* 32 */     this.imageWidth = 176;
/* 33 */     this.imageHeight = 201;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 38 */     super.init();
/*    */ 
/*    */     
/* 41 */     this.coinButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 111, this.topPos + 92, 32, 12, (ITextComponent)new TranslationTextComponent("general.button.coin"), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 47 */     addButton((Widget)new SlimButton(this.leftPos + 95, this.topPos + 48, 12, 12, (ITextComponent)new StringTextComponent("-"), b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
/*    */ 
/*    */     
/* 50 */     addButton((Widget)new SlimButton(this.leftPos + 144, this.topPos + 48, 12, 12, (ITextComponent)new StringTextComponent("+"), b -> this.tileEntity.sendButtonMessage(2, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 55 */     addButton((Widget)new SlimButton(this.leftPos + 95, this.topPos + 26, 12, 12, (ITextComponent)new StringTextComponent("-"), b -> this.tileEntity.sendButtonMessage(3, hasShiftDown())));
/*    */ 
/*    */     
/* 58 */     addButton((Widget)new SlimButton(this.leftPos + 144, this.topPos + 26, 12, 12, (ITextComponent)new StringTextComponent("+"), b -> this.tileEntity.sendButtonMessage(4, hasShiftDown())));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 65 */     renderBackground(matrixStack);
/* 66 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 67 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 73 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 8.0F, 5.0F, 4210752);
/*    */     
/* 75 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 8.0F, 96.0F, 4210752);
/*    */ 
/*    */     
/* 78 */     String feeVal = this.formatter.format(((ContainerSignal)this.menu).getSyncedValue(1));
/* 79 */     this.font.draw(matrixStack, feeVal, (138 - this.font.width(feeVal)), 28.0F, 4210752);
/*    */ 
/*    */     
/* 82 */     String durVal = this.formatter.format(((ContainerSignal)this.menu).getSyncedValue(2));
/* 83 */     this.font.draw(matrixStack, durVal, (138 - this.font.width(durVal)), 50.0F, 4210752);
/*    */ 
/*    */     
/* 86 */     String coinSumStr = this.formatter.format(((ContainerSignal)this.menu).getSyncedValue(0));
/* 87 */     this.font.draw(matrixStack, coinSumStr, (138 - this.font.width(coinSumStr)), 78.0F, 4210752);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 93 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 94 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 95 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */ 
/*    */     
/* 98 */     this.coinButton.active = (this.tileEntity.coinSum > 0);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/SignalScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */