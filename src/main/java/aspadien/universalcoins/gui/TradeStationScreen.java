/*     */ package aspadien.universalcoins.gui;
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*     */ import net.minecraft.client.gui.widget.Widget;
/*     */ import net.minecraft.client.gui.widget.button.Button;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import aspadien.universalcoins.UniversalCoins;
/*     */ import aspadien.universalcoins.container.ContainerTradeStation;
/*     */ import aspadien.universalcoins.net.ModPacketHandler;
import aspadien.universalcoins.net.UCButtonMessage;
/*     */ import aspadien.universalcoins.tileentity.TileTradeStation;
/*     */ 
/*     */ public class TradeStationScreen extends ContainerScreen<ContainerTradeStation> {
/*  18 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/trade_station.png"); private final TileTradeStation tileEntity;
/*     */   public static final int idBuyButton = 0;
/*     */   public static final int idSellButton = 1;
/*     */   public static final int idCoinButton = 2;
/*     */   public static final int idCoinModeButton = 7;
/*     */   public static final int idAutoModeButton = 8;
/*  24 */   private final Button[] coinButtons = new Button[6]; public static final int idAccessModeButton = 9; private Button buyButton; private Button sellButton; private Button accessModeButton; private Button autoModeButton; private Button coinModeButton;
/*     */   
/*     */   public TradeStationScreen(ContainerTradeStation container, PlayerInventory inv, ITextComponent title) {
/*  27 */     super((ContainerTradeStation) container, inv, title);
/*  28 */     this.tileEntity = container.getTile();
/*  29 */     this.imageWidth = 184;
/*  30 */     this.imageHeight = 200;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void init() {
/*  35 */     super.init();
/*     */     
/*  37 */     this.buyButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 50, this.topPos + 21, 48, 12, (ITextComponent)new TranslationTextComponent("general.button.buy"), b -> this.tileEntity.sendButtonMessage(0, hasShiftDown(), 0)));
/*     */ 
/*     */     
/*  40 */     this.buyButton.visible = UniversalCoins.tradeStationBuyEnabled;
/*     */     
/*  42 */     this.sellButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 50, this.topPos + 38, 48, 12, (ITextComponent)new TranslationTextComponent("general.button.sell"), b -> this.tileEntity.sendButtonMessage(1, hasShiftDown(), 0)));
/*     */ 
/*     */ 
/*     */     
/*  46 */     for (int i = 0; i < 6; i++) {
/*  47 */       int index = i;
/*  48 */       this.coinButtons[i] = (Button)addButton((Widget)new CoinButton(this.leftPos + 88 + i * 18, this.topPos + 74, index, b -> this.tileEntity.sendButtonMessage(2 + index, hasShiftDown(), 0)));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     this.coinModeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 110, this.topPos + 98, 46, 12, (ITextComponent)new TranslationTextComponent("general.button.coin"), b -> this.tileEntity.sendButtonMessage(7, hasShiftDown(), 0)));
/*     */ 
/*     */ 
/*     */     
/*  58 */     this.accessModeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 127, this.topPos + 4, 52, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(9, hasShiftDown(), 0)));
/*     */ 
/*     */ 
/*     */     
/*  62 */     if (this.tileEntity.autoModeButtonActive) {
/*  63 */       this.autoModeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 6, this.topPos + 84, 28, 12, (ITextComponent)new TranslationTextComponent("tradestation.gui.button.mode"), b -> this.tileEntity.sendButtonMessage(8, hasShiftDown(), 0)));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
/*  73 */     if (mouseButton == 1 && this.coinModeButton != null && this.coinModeButton.isMouseOver(mouseX, mouseY)) {
/*     */       
/*  75 */       this.coinModeButton.playDownSound(Minecraft.getInstance().getSoundManager());
/*     */       
/*  77 */       ModPacketHandler.sendToServer(new UCButtonMessage(this.tileEntity.getBlockPos(), 7, hasShiftDown(), 1));
/*  78 */       return true;
/*     */     } 
/*     */ 
/*     */     
/*  82 */     return super.mouseClicked(mouseX, mouseY, mouseButton);
/*     */   }
/*     */ 
/*     */   
/*     */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/*  87 */     renderBackground(matrixStack);
/*  88 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/*  89 */     renderTooltip(matrixStack, mouseX, mouseY);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/*  94 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  95 */     this.minecraft.getTextureManager().bind(TEXTURE);
/*  96 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*     */     
/*  98 */     this.buyButton.active = this.tileEntity.buyButtonActive;
/*  99 */     this.sellButton.active = this.tileEntity.sellButtonActive;
/*     */     
/* 101 */     (this.coinButtons[0]).active = (((ContainerTradeStation)this.menu).getSyncedValue(7) == 1);
/* 102 */     (this.coinButtons[1]).active = (((ContainerTradeStation)this.menu).getSyncedValue(8) == 1);
/* 103 */     (this.coinButtons[2]).active = (((ContainerTradeStation)this.menu).getSyncedValue(9) == 1);
/* 104 */     (this.coinButtons[3]).active = (((ContainerTradeStation)this.menu).getSyncedValue(10) == 1);
/* 105 */     (this.coinButtons[4]).active = (((ContainerTradeStation)this.menu).getSyncedValue(11) == 1);
/* 106 */     (this.coinButtons[5]).active = (((ContainerTradeStation)this.menu).getSyncedValue(12) == 1);
/*     */     
/* 108 */     this.accessModeButton.setMessage(this.tileEntity.publicAccess ? (ITextComponent)new TranslationTextComponent("general.label.public") : (ITextComponent)new TranslationTextComponent("general.label.private"));
/*     */ 
/*     */ 
/*     */     
/* 112 */     if (this.tileEntity.autoModeButtonActive) {
/* 113 */       blit(matrixStack, this.leftPos + 35, this.topPos + 83, 184, 0, 50, 15);
/*     */     }
/*     */     
/* 116 */     int[] xHighlight = { 0, 89, 107, 125, 143, 161, 179 };
/* 117 */     if (this.tileEntity.coinMode > 0 && this.tileEntity.coinMode < xHighlight.length) {
/* 118 */       blit(matrixStack, this.leftPos + xHighlight[this.tileEntity.coinMode], this.topPos + 94, 184, 15, 16, 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 124 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 6.0F, 5.0F, 4210752);
/* 125 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 6.0F, (this.imageHeight - 94), 4210752);
/*     */     
/* 127 */     this.font.draw(matrixStack, String.valueOf(this.tileEntity.coinSum), 114.0F, 57.0F, 4210752);
/*     */     
/* 129 */     String priceLabel = (new TranslationTextComponent("general.label.price")).getString();
/* 130 */     int labelWidth = this.font.width(priceLabel);
/* 131 */     this.font.draw(matrixStack, priceLabel, (48 - labelWidth), 57.0F, 4210752);
/*     */     
/* 133 */     if (this.tileEntity.itemPrice > 0) {
/* 134 */       int displayPrice = this.tileEntity.itemPrice;
/* 135 */       if (this.sellButton.isMouseOver(mouseX, mouseY) || !UniversalCoins.tradeStationBuyEnabled) {
/* 136 */         displayPrice = (int)(this.tileEntity.itemPrice * UniversalCoins.itemSellRatio);
/*     */       }
/* 138 */       this.font.draw(matrixStack, String.valueOf(displayPrice), 48.0F, 57.0F, 4210752);
/*     */     } else {
/* 140 */       this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("tradestation.gui.warning.noitem"), 48.0F, 57.0F, 4210752);
/*     */     } 
/*     */     
/* 143 */     if (this.tileEntity.autoModeButtonActive) {
/* 144 */       this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("tradestation.gui.label.autobuy"), 6.0F, 74.0F, 4210752);
/*     */ 
/*     */ 
/*     */       
/* 148 */       String[] autoLabels = { (new TranslationTextComponent("tradestation.gui.autolabel.off")).getString(), (new TranslationTextComponent("tradestation.gui.autolabel.buy")).getString(), (new TranslationTextComponent("tradestation.gui.autolabel.sell")).getString() };
/*     */       
/* 150 */       this.font.draw(matrixStack, autoLabels[this.tileEntity.autoMode % 3], 38.0F, 87.0F, 4210752);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/TradeStationScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */