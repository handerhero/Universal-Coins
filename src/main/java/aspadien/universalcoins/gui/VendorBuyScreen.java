/*    */ package aspadien.universalcoins.gui;
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import com.mojang.blaze3d.systems.RenderSystem;
/*    */ import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*    */ import net.minecraft.client.gui.widget.Widget;
/*    */ import net.minecraft.client.gui.widget.button.Button;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.container.ContainerVendorBuy;
import aspadien.universalcoins.tileentity.TileVendor;

/*    */
/*    */ public class VendorBuyScreen extends ContainerScreen<ContainerVendorBuy> {
/* 15 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_buy.png");
/*    */   private final TileVendor tileEntity;
/*    */   private Button buyButton;
/* 18 */   private final Button[] coinButtons = new Button[5];
/*    */   
/*    */   public VendorBuyScreen(ContainerVendorBuy container, PlayerInventory inv, ITextComponent title) {
/* 21 */     super((ContainerVendorBuy) container, inv, title);
/* 22 */     this.tileEntity = container.getTile();
/* 23 */     this.imageWidth = 176;
/* 24 */     this.imageHeight = 189;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 29 */     super.init();
/*    */     
/* 31 */     this.buyButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 126, this.topPos + 42, 42, 12, (ITextComponent)new TranslationTextComponent("general.button.buy"), b -> this.tileEntity.sendButtonMessage(11, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 37 */     for (int i = 0; i < 5; i++) {
/* 38 */       int index = i;
/* 39 */       this.coinButtons[i] = (Button)addButton((Widget)new CoinButton(this.leftPos + 56 + i * 18, this.topPos + 74, index, b -> this.tileEntity.sendButtonMessage(12 + index, hasShiftDown())));
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 47 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 48 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 49 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */     
/* 51 */     this.buyButton.active = (((ContainerVendorBuy)this.menu).getSyncedValue(8) == 1);
/* 52 */     for (int i = 0; i < 5; i++) {
/* 53 */       (this.coinButtons[i]).active = (((ContainerVendorBuy)this.menu).getSyncedValue(15 + i) == 1);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 60 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 8.0F, 6.0F, 4210752);
/*    */ 
/*    */     
/* 63 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 8.0F, 97.0F, 4210752);
/*    */ 
/*    */     
/* 66 */     String price = String.valueOf(((ContainerVendorBuy)this.menu).getSyncedValue(2));
/* 67 */     this.font.draw(matrixStack, price, 59.0F, 29.0F, 4210752);
/*    */ 
/*    */     
/* 70 */     String userBalance = String.valueOf(((ContainerVendorBuy)this.menu).getSyncedValue(1));
/* 71 */     this.font.draw(matrixStack, userBalance, 59.0F, 62.0F, 4210752);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 76 */     renderBackground(matrixStack);
/* 77 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 78 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/VendorBuyScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */