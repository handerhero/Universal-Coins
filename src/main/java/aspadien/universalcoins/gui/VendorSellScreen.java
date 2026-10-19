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
/*    */ import aspadien.universalcoins.container.ContainerVendorSell;
import aspadien.universalcoins.tileentity.TileVendor;

/*    */
/*    */ public class VendorSellScreen extends ContainerScreen<ContainerVendorSell> {
/* 15 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/vendor_sell.png");
/*    */   private final TileVendor tileEntity;
/*    */   private Button sellButton;
/* 18 */   private final Button[] coinButtons = new Button[5];
/*    */   
/*    */   public VendorSellScreen(ContainerVendorSell container, PlayerInventory inv, ITextComponent title) {
/* 21 */     super((ContainerVendorSell) container, inv, title);
/* 22 */     this.tileEntity = container.getTile();
/* 23 */     this.imageWidth = 176;
/*    */     
/* 25 */     this.imageHeight = 198;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void init() {
/* 30 */     super.init();
/*    */     
/* 32 */     this.sellButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 126, this.topPos + 46, 42, 12, (ITextComponent)new TranslationTextComponent("general.button.sell"), b -> this.tileEntity.sendButtonMessage(10, hasShiftDown())));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 38 */     for (int i = 0; i < 5; i++) {
/* 39 */       int index = i;
/* 40 */       this.coinButtons[i] = (Button)addButton((Widget)new CoinButton(this.leftPos + 60 + i * 18, this.topPos + 82, index, b -> this.tileEntity.sendButtonMessage(12 + index, hasShiftDown())));
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 48 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 49 */     this.minecraft.getTextureManager().bind(TEXTURE);
/*    */ 
/*    */     
/* 52 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*    */     
/* 54 */     this.sellButton.active = (((ContainerVendorSell)this.menu).getSyncedValue(9) == 1);
/* 55 */     for (int i = 0; i < 5; i++) {
/* 56 */       (this.coinButtons[i]).active = (((ContainerVendorSell)this.menu).getSyncedValue(15 + i) == 1);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 63 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 8.0F, 6.0F, 4210752);
/*    */ 
/*    */     
/* 66 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 8.0F, 105.0F, 4210752);
/*    */     
/* 68 */     String price = String.valueOf(((ContainerVendorSell)this.menu).getSyncedValue(2));
/* 69 */     this.font.draw(matrixStack, price, 48.0F, 29.0F, 4210752);
/*    */ 
/*    */     
/* 72 */     String userBalance = String.valueOf(((ContainerVendorSell)this.menu).getSyncedValue(1));
/* 73 */     this.font.draw(matrixStack, userBalance, 63.0F, 69.0F, 4210752);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 78 */     renderBackground(matrixStack);
/* 79 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 80 */     renderTooltip(matrixStack, mouseX, mouseY);
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/VendorSellScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */