/*     */ package aspadien.universalcoins.gui;
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
/*     */ import java.text.DecimalFormat;
/*     */ import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*     */ import net.minecraft.client.gui.widget.TextFieldWidget;
/*     */ import net.minecraft.client.gui.widget.Widget;
/*     */
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import aspadien.universalcoins.container.ContainerATM;
import aspadien.universalcoins.tileentity.TileATM;

/*     */
/*     */ public class ATMScreen extends ContainerScreen<ContainerATM> {
/*  18 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/atm.png");
/*     */   
/*     */   private final TileATM tileEntity;
/*     */   
/*     */   private TextFieldWidget textField;
/*  23 */   public int menuState = 0;
/*  24 */   private static final String[] menuStateName = new String[] { "welcome", "auth", "main", "additional", "balance", "deposit", "withdraw", "newcard", "transferaccount", "takecard", "takecoins", "insufficient", "invalid", "badcard", "unauthorized", "newaccount", "processing" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private int barProgress = 0;
/*  32 */   private final DecimalFormat formatter = new DecimalFormat("#,###,###,###,###,###,###");
/*     */   
/*     */   public ATMScreen(ContainerATM container, PlayerInventory inv, ITextComponent title) {
/*  35 */     super((ContainerATM) container, inv, title);
/*  36 */     this.tileEntity = container.getTile();
/*  37 */     this.imageWidth = 196;
/*  38 */     this.imageHeight = 201;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void init() {
/*  43 */     super.init();
/*     */ 
/*     */     
/*  46 */     int[] sideButtonY = { 30, 50, 70, 90 };
/*     */     
/*  48 */     for (int i = 0; i < 4; i++) {
/*  49 */       int id = i;
/*     */       
/*  51 */       addButton((Widget)new SlimButton(this.leftPos + 8, this.topPos + sideButtonY[i], 18, 12, StringTextComponent.EMPTY, button -> handleButtonClick(id)));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  57 */     this.textField = new TextFieldWidget(this.font, this.leftPos + 34, this.topPos + 52, 120, 12, StringTextComponent.EMPTY);
/*  58 */     this.textField.setMaxLength(9);
/*  59 */     this.textField.setBordered(false);
/*  60 */     this.textField.setVisible(false);
/*  61 */     this.textField.setTextColor(4210752);
/*  62 */     this.children.add(this.textField);
/*     */   }
/*     */   
/*     */   private void handleButtonClick(int buttonId) {
/*  66 */     int functionID = 0;
/*     */     
/*  68 */     boolean isOwner = (((ContainerATM)this.menu).getSyncedValue(7) == 1);
/*     */     
/*  70 */     switch (this.menuState) { case 0:
/*  71 */         functionID = 5; this.menuState = 1; this.barProgress = 0; break;
/*     */       case 2:
/*  73 */         if (buttonId == 0) this.menuState = 4; 
/*  74 */         if (buttonId == 1) { functionID = 3; this.menuState = 5; }
/*  75 */          if (buttonId == 2) { this.textField.setValue("0"); this.menuState = 6; }
/*  76 */          if (buttonId == 3) this.menuState = 3; 
/*     */         break;
/*     */       case 3:
/*  79 */         if (buttonId == 0)
/*  80 */           if (!isOwner) { this.menuState = 14; }
/*  81 */           else { functionID = 1; this.menuState = 9; }
/*     */            
/*  83 */         if (buttonId == 1)
/*  84 */           if (!isOwner) { this.menuState = 14; }
/*  85 */           else { this.menuState = 8; }
/*     */            
/*  87 */         if (buttonId == 3) this.menuState = 2;  break;
/*     */       case 4:
/*     */       case 5:
/*  90 */         if (buttonId == 3) this.menuState = 2;  break;
/*     */       case 6:
/*  92 */         if (buttonId == 2)
/*     */           
/*  94 */           try { int amount = Integer.parseInt(this.textField.getValue());
/*  95 */             if (amount > 0 && amount <= this.tileEntity.accountBalance)
/*  96 */             { this.tileEntity.sendServerUpdatePacket(amount);
/*  97 */               functionID = 4; this.menuState = 10; }
/*  98 */             else { this.menuState = 12; }  }
/*  99 */           catch (Exception e) { this.menuState = 12; }
/*     */            
/* 101 */         if (buttonId == 3) { this.textField.setValue("0"); this.menuState = 2; }
/*     */          break;
/*     */       case 7:
/* 104 */         if (buttonId == 2) { functionID = 1; this.menuState = 9; }
/* 105 */          if (buttonId == 3) this.menuState = 2; 
/*     */         break;
/*     */       case 8:
/* 108 */         if (buttonId == 2) { functionID = 2; this.menuState = 9; }
/* 109 */          if (buttonId == 3) this.menuState = 2; 
/*     */         break;
/*     */       case 9:
/* 112 */         if (buttonId == 3) this.menuState = 0;  break;
/*     */       case 10: case 11: case 12:
/* 114 */         if (buttonId == 3) this.menuState = 6;  break;
/*     */       case 15:
/* 116 */         if (buttonId == 2) {
/* 117 */           functionID = 1;
/* 118 */           this.menuState = 16;
/*     */         } 
/* 120 */         if (buttonId == 3) this.menuState = 0;
/*     */         
/*     */         break; }
/*     */     
/* 124 */     if (functionID > 0) {
/* 125 */       this.tileEntity.sendButtonMessage(functionID, hasShiftDown());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 131 */     renderBackground(matrixStack);
/* 132 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 133 */     renderTooltip(matrixStack, mouseX, mouseY);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/* 138 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/* 139 */     this.minecraft.getTextureManager().bind(TEXTURE);
/* 140 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*     */     
/* 142 */     if (this.menuState == 0 && !this.tileEntity.getItem(1).isEmpty()) {
/* 143 */       this.menuState = 2;
/*     */     }
/*     */     
/* 146 */     if (this.menuState == 1) {
/* 147 */       this.barProgress++;
/*     */       
/* 149 */       blit(matrixStack, this.leftPos + 171, this.topPos + 19, 196, 0, 18, 18);
/*     */       
/* 151 */       blit(matrixStack, this.leftPos + 34, this.topPos + 43, 0, 201, Math.min(this.barProgress, 128), 5);
/*     */       
/* 153 */       if (this.barProgress > 130) {
/* 154 */         String key = !this.tileEntity.accountNumber.equals("none") ? "atm.auth.success" : "atm.auth.fail";
/* 155 */         drawCenteredLine(matrixStack, (new TranslationTextComponent(key)).getString(), 72);
/* 156 */         if (this.barProgress > 170) {
/* 157 */           if (!this.tileEntity.accountNumber.equals("none")) { this.menuState = 2; }
/* 158 */           else { this.menuState = 15; }
/* 159 */            this.barProgress = 0;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 165 */     if (this.menuState == 12 || this.menuState == 13 || this.menuState == 14 || this.menuState == 16) {
/* 166 */       this.barProgress++;
/* 167 */       if (this.barProgress > 100) {
/* 168 */         if (this.menuState == 12) { this.menuState = 6; }
/* 169 */         else if (this.menuState == 13) { this.menuState = 0; }
/* 170 */         else if (this.menuState == 14) { this.menuState = 2; }
/* 171 */         else if (this.menuState == 16) { this.menuState = 9; }
/* 172 */          this.barProgress = 0;
/*     */       } 
/*     */     } 
/*     */     
/* 176 */     this.textField.setVisible((this.menuState == 6));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 182 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 6.0F, 5.0F, 4210752);
/*     */     
/* 184 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 6.0F, (this.imageHeight - 94), 4210752);
/*     */     
/* 186 */     if (this.menuState == 4 || this.menuState == 5) {
/* 187 */       this.font.draw(matrixStack, this.formatter.format(this.tileEntity.accountBalance), 34.0F, 52.0F, 4210752);
/*     */     }
/*     */     
/* 190 */     if (this.menuState == 6) {
/*     */       
/* 192 */       this.font.draw(matrixStack, this.formatter.format(this.tileEntity.accountBalance), 34.0F, 32.0F, 4210752);
/*     */       
/* 194 */       String textToDraw = this.textField.getValue() + ((System.currentTimeMillis() / 500L % 2L == 0L) ? "|" : "");
/* 195 */       this.font.draw(matrixStack, textToDraw, 34.0F, 52.0F, 4210752);
/*     */     } 
/*     */     
/* 198 */     drawMenuLines(matrixStack, this.menuState);
/*     */   }
/*     */   
/*     */   private void drawMenuLines(MatrixStack ms, int state) {
/* 202 */     int[] yCoords = { 22, 32, 42, 52, 62, 72, 82, 92 };
/* 203 */     String[] suffixes = { ".one", ".two", ".three", ".four", ".five", ".six", ".seven", ".eight" };
/*     */     
/* 205 */     for (int i = 0; i < 8; i++) {
/*     */       
/* 207 */       if (state != 6 || (i != 1 && i != 3))
/*     */       {
/* 209 */         if ((state != 4 && state != 5) || i != 3) {
/*     */           
/* 211 */           String key = "atm." + menuStateName[state] + suffixes[i];
/* 212 */           String text = (new TranslationTextComponent(key)).getString();
/*     */           
/* 214 */           if (text.startsWith("C:")) {
/* 215 */             drawCenteredLine(ms, text.substring(2), yCoords[i]);
/* 216 */           } else if (!text.equals(key)) {
/* 217 */             this.font.draw(ms, text, 34.0F, yCoords[i], 4210752);
/*     */           } 
/*     */         }  } 
/*     */     } 
/*     */   }
/*     */   private void drawCenteredLine(MatrixStack ms, String text, int y) {
/* 223 */     int width = this.font.width(text);
/*     */     
/* 225 */     float x = 98.0F - width / 2.0F;
/* 226 */     this.font.draw(ms, text, x, y, 4210752);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean charTyped(char codePoint, int modifiers) {
/* 231 */     if (this.menuState == 6 && this.textField.charTyped(codePoint, modifiers)) return true; 
/* 232 */     return super.charTyped(codePoint, modifiers);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
/* 237 */     if (this.menuState == 6 && this.textField.keyPressed(keyCode, scanCode, modifiers)) return true; 
/* 238 */     return super.keyPressed(keyCode, scanCode, modifiers);
/*     */   }
/*     */ }
