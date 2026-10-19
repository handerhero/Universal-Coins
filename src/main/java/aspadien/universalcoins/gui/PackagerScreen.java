/*     */ package aspadien.universalcoins.gui;
/*     */ import com.mojang.blaze3d.matrix.MatrixStack;
/*     */ import java.text.DecimalFormat;
/*     */ import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
/*     */ import net.minecraft.client.gui.widget.TextFieldWidget;
/*     */ import net.minecraft.client.gui.widget.Widget;
/*     */ import net.minecraft.client.gui.widget.button.Button;
/*     */ import net.minecraft.client.util.InputMappings;
/*     */ import net.minecraft.entity.player.PlayerInventory;
/*     */
/*     */ import net.minecraft.inventory.container.Slot;
/*     */ import net.minecraft.util.ResourceLocation;
/*     */ import net.minecraft.util.text.ITextComponent;
/*     */ import net.minecraft.util.text.StringTextComponent;
/*     */ import net.minecraft.util.text.TranslationTextComponent;
/*     */ import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
/*     */ import aspadien.universalcoins.UniversalCoins;
import aspadien.universalcoins.container.ContainerPackager;
/*     */ import aspadien.universalcoins.tileentity.TilePackager;
/*     */ 
/*     */ public class PackagerScreen extends ContainerScreen<ContainerPackager> {
/*  21 */   private static final ResourceLocation TEXTURE = new ResourceLocation("universalcoins", "textures/gui/packager.png"); private final TilePackager tileEntity;
/*     */   private Button smallButton;
/*     */   private Button medButton;
/*     */   private Button largeButton;
/*  25 */   private final int hideCoord = 10000; private Button actionButton; private Button coinButton; private Button modeButton; private TextFieldWidget receiverField;
/*  26 */   private final int[] defaultSlotX = new int[] { 8, 26, 44, 62 };
/*     */   
/*  28 */   private int lastSyncedMode = -1;
/*     */   
/*     */   public PackagerScreen(ContainerPackager container, PlayerInventory inv, ITextComponent title) {
/*  31 */     super((ContainerPackager) container, inv, title);
/*  32 */     this.tileEntity = container.getTile();
/*  33 */     this.imageWidth = 176;
/*  34 */     this.imageHeight = 190;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void init() {
/*  39 */     super.init();
/*     */ 
/*     */     
/*  42 */     this.modeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 110, this.topPos + 4, 60, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(5, hasShiftDown())));
/*     */ 
/*     */ 
/*     */     
/*  46 */     this.smallButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 87, this.topPos + 20, 50, 12, (ITextComponent)new TranslationTextComponent("packager.button.small"), b -> this.tileEntity.sendButtonMessage(2, hasShiftDown())));
/*  47 */     this.medButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 87, this.topPos + 32, 50, 12, (ITextComponent)new TranslationTextComponent("packager.button.medium"), b -> this.tileEntity.sendButtonMessage(3, hasShiftDown())));
/*  48 */     this.largeButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 87, this.topPos + 44, 50, 12, (ITextComponent)new TranslationTextComponent("packager.button.large"), b -> this.tileEntity.sendButtonMessage(4, hasShiftDown())));
/*     */     
/*  50 */     this.actionButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 123, this.topPos + 58, 46, 12, StringTextComponent.EMPTY, b -> this.tileEntity.sendButtonMessage(0, hasShiftDown())));
/*  51 */     this.coinButton = (Button)addButton((Widget)new SlimButton(this.leftPos + 123, this.topPos + 91, 46, 12, (ITextComponent)new TranslationTextComponent("general.button.coin"), b -> this.tileEntity.sendButtonMessage(1, hasShiftDown())));
/*     */     
/*  53 */     this.receiverField = new TextFieldWidget(this.font, this.leftPos + 30, this.topPos + 30, 100, 12, StringTextComponent.EMPTY);
/*  54 */     this.receiverField.setMaxLength(24);
/*  55 */     this.receiverField.setBordered(false);
/*  56 */     this.children.add(this.receiverField);
/*  57 */     updateUI((((ContainerPackager)this.menu).getSyncedValue(3) == 1));
/*     */   }
/*     */   
/*     */   private void updateUI(boolean isSendMode) {
/*  61 */     if (isSendMode) {
/*  62 */       this.modeButton.setMessage((ITextComponent)new TranslationTextComponent("packager.button.mode.send"));
/*  63 */       this.largeButton.visible = false;
/*  64 */       this.receiverField.setVisible(true);
/*  65 */       updateSlots(3);
/*     */     } else {
/*  67 */       this.modeButton.setMessage((ITextComponent)new TranslationTextComponent("packager.button.mode.buy"));
/*  68 */       this.largeButton.visible = true;
/*  69 */       this.receiverField.setVisible(false);
/*  70 */       updateSlots(((ContainerPackager)this.menu).getSyncedValue(2));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
/*  76 */     RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
/*  77 */     this.minecraft.getTextureManager().bind(TEXTURE);
/*  78 */     blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
/*     */ 
/*     */     
/*  81 */     boolean isSendMode = (((ContainerPackager)this.menu).getSyncedValue(3) == 1);
/*  82 */     int currentSize = ((ContainerPackager)this.menu).getSyncedValue(2);
/*     */     
/*  84 */     this.actionButton.active = (((ContainerPackager)this.menu).getSyncedValue(4) == 1);
/*  85 */     this.coinButton.active = (((ContainerPackager)this.menu).getSyncedValue(5) == 1);
/*     */     
/*  87 */     if (isSendMode) {
/*  88 */       blit(matrixStack, this.leftPos + 3, this.topPos + 20, 0, 190, 169, 40);
/*  89 */       this.modeButton.setMessage((ITextComponent)new TranslationTextComponent("packager.button.mode.send"));
/*  90 */       this.actionButton.setMessage((ITextComponent)new TranslationTextComponent("general.button.send"));
/*  91 */       this.largeButton.visible = false;
/*  92 */       this.receiverField.setVisible(true);
/*  93 */       updateSlots(3);
/*     */     } else {
/*  95 */       this.modeButton.setMessage((ITextComponent)new TranslationTextComponent("packager.button.mode.buy"));
/*  96 */       this.actionButton.setMessage((ITextComponent)new TranslationTextComponent("general.button.buy"));
/*  97 */       this.largeButton.visible = true;
/*  98 */       this.receiverField.setVisible(false);
/*  99 */       updateSlots(currentSize);
/*     */ 
/*     */       
/* 102 */       int[] yHighlight = { 24, 36, 48 };
/* 103 */       blit(matrixStack, this.leftPos + 80, this.topPos + yHighlight[currentSize], 176, 0, 6, 6);
/* 104 */       if (currentSize == 1) blit(matrixStack, this.leftPos + 25, this.topPos + 21, 176, 6, 18, 36); 
/* 105 */       if (currentSize == 2) blit(matrixStack, this.leftPos + 7, this.topPos + 21, 176, 6, 36, 36);
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
/* 111 */     this.font.draw(matrixStack, this.tileEntity.getDisplayName(), 8.0F, 5.0F, 4210752);
/* 112 */     this.font.draw(matrixStack, (ITextComponent)new TranslationTextComponent("container.inventory"), 8.0F, 96.0F, 4210752);
/*     */     
/* 114 */     int cost = -1;
/* 115 */     if (this.smallButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager)this.menu).getSyncedValue(6); }
/* 116 */     else if (this.medButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager)this.menu).getSyncedValue(7); }
/* 117 */     else if (this.largeButton.isMouseOver(mouseX, mouseY)) { cost = ((ContainerPackager)this.menu).getSyncedValue(8); }
/*     */     
/* 119 */     if (cost != -1 && ((ContainerPackager)this.menu).getSyncedValue(3) == 0) {
/* 120 */       String text = "Cost: " + cost;
/* 121 */       this.font.draw(matrixStack, text, (144 - this.font.width(text)), 78.0F, 4210752);
/*     */     } else {
/*     */       
/* 124 */       long low = ((ContainerPackager)this.menu).getSyncedValue(0) & 0xFFFFFFFFL;
/* 125 */       long high = ((ContainerPackager)this.menu).getSyncedValue(1) << 32L;
/* 126 */       long currentBalance = high | low;
/*     */       
/* 128 */       String coinStr = (new DecimalFormat("#,###,###,###")).format(currentBalance);
/* 129 */       this.font.draw(matrixStack, coinStr, (144 - this.font.width(coinStr)), 78.0F, 4210752);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setSlotPos(Slot slot, int x, int y) {
/*     */     try {
/* 135 */       ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, Integer.valueOf(x), "field_75223_e");
/* 136 */       ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, Integer.valueOf(y), "field_75221_f");
/* 137 */     } catch (Exception e) {
/* 138 */       UniversalCoins.LOGGER.error("Failed to move slot: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateSlots(int mode) {
/* 145 */     if (getMenu() == null)
/*     */       return; 
/* 147 */     int[] slotX = { 8, 26, 44, 62 };
/* 148 */     int[] slotY = { 22, 40 };
/*     */     
/* 150 */     for (int i = 0; i < 8; i++) {
/* 151 */       Slot slot = ((ContainerPackager)getMenu()).getSlot(i);
/* 152 */       int newX = 10000;
/* 153 */       int newY = slotY[i % 2];
/*     */       
/* 155 */       if (mode == 0) {
/* 156 */         if (i >= 4) newX = slotX[i / 2]; 
/* 157 */       } else if (mode == 1) {
/* 158 */         if (i >= 2) newX = slotX[i / 2]; 
/* 159 */       } else if (mode == 2) {
/* 160 */         newX = slotX[i / 2];
/*     */       } 
/* 162 */       setSlotPos(slot, newX, newY);
/*     */     } 
/*     */ 
/*     */     
/* 166 */     int inputX = (mode == 3) ? 8 : 10000;
/* 167 */     int inputY = (mode == 3) ? 26 : 22;
/* 168 */     setSlotPos(((ContainerPackager)getMenu()).getSlot(11), inputX, inputY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
/* 174 */     if (this.receiverField.isVisible() && this.receiverField.isFocused()) {
/*     */       
/* 176 */       if (keyCode == 258) {
/* 177 */         this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), true);
/* 178 */         return true;
/*     */       } 
/*     */ 
/*     */       
/* 182 */       if (keyCode == 256) {
/* 183 */         return super.keyPressed(keyCode, scanCode, modifiers);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 188 */       if (this.minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
/* 189 */         return true;
/*     */       }
/*     */ 
/*     */       
/* 193 */       if (this.receiverField.keyPressed(keyCode, scanCode, modifiers)) {
/* 194 */         this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), false);
/* 195 */         return true;
/*     */       } 
/*     */     } 
/* 198 */     return super.keyPressed(keyCode, scanCode, modifiers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
/* 204 */     int currentMode = ((ContainerPackager)this.menu).getSyncedValue(3);
/* 205 */     if (currentMode != this.lastSyncedMode) {
/* 206 */       updateUI((currentMode == 1));
/* 207 */       this.lastSyncedMode = currentMode;
/*     */     } 
/*     */     
/* 210 */     renderBackground(matrixStack);
/* 211 */     super.render(matrixStack, mouseX, mouseY, partialTicks);
/* 212 */     renderTooltip(matrixStack, mouseX, mouseY);
/* 213 */     this.receiverField.render(matrixStack, mouseX, mouseY, partialTicks);
/*     */   } public boolean charTyped(char c, int m) {
/* 215 */     if (this.receiverField.isVisible() && this.receiverField.charTyped(c, m)) { this.tileEntity.sendServerUpdateMessage(this.receiverField.getValue(), false); return true; }  return super.charTyped(c, m);
/*     */   }
/*     */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/gui/PackagerScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */