/*    */ package aspadien.universalcoins.tileentity;
/*    */ 
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */ import net.minecraft.nbt.ListNBT;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.tileentity.TileEntityType;
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.init.ModItems;
/*    */ import aspadien.universalcoins.init.ModTileEntities;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TileVendorBlock
/*    */   extends TileVendor
/*    */ {
/*    */   private final String[] signLines;
/*    */   
/*    */   public TileVendorBlock() {
/* 25 */     super(ModTileEntities.VENDOR_BLOCK.get());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 33 */     this.signLines = new String[] { "", "", "", "" };
/*    */   }
/*    */   
/*    */   public void updateSigns() {
/* 37 */     if (this.level == null || this.level.isClientSide)
/*    */       return; 
/* 39 */     ItemStack tradeStack = getItem(9);
/* 40 */     if (!tradeStack.isEmpty()) {
/* 41 */       String colorCode = "&" + Integer.toHexString(this.textColor);
/*    */ 
/*    */       
/* 44 */       this.signLines[0] = colorCode + (this.sellMode ? "Selling" : "Buying");
/*    */       
/* 46 */       if (!this.infiniteMode) {
/* 47 */         if (this.sellMode && this.ooStockWarning) {
/* 48 */           this.signLines[0] = colorCode + (new TranslationTextComponent("sign.warning.stock")).getString();
/* 49 */         } else if (!this.sellMode && this.ooCoinsWarning) {
/* 50 */           this.signLines[0] = colorCode + (new TranslationTextComponent("sign.warning.coins")).getString();
/* 51 */         } else if (!this.sellMode && this.inventoryFullWarning) {
/* 52 */           this.signLines[0] = colorCode + (new TranslationTextComponent("sign.warning.inventoryfull")).getString();
/*    */         } 
/*    */       }
/*    */ 
/*    */       
/* 57 */       String displayName = tradeStack.getHoverName().getString();
/* 58 */       this.signLines[1] = colorCode + ((tradeStack.getCount() > 1) ? (tradeStack.getCount() + " " + displayName) : displayName);
/*    */ 
/*    */       
/* 61 */       this.signLines[2] = "";
/* 62 */       if (tradeStack.getItem() == ModItems.UC_PACKAGE.get()) {
/* 63 */         this.signLines[2] = colorCode;
/* 64 */         CompoundNBT packageTag = tradeStack.getTag();
/* 65 */         if (packageTag != null && packageTag.contains("Inventory", 9)) {
/* 66 */           ListNBT itemList = packageTag.getList("Inventory", 10);
/* 67 */           for (int i = 0; i < itemList.size(); i++) {
/* 68 */             ItemStack innerStack = ItemStack.of(itemList.getCompound(i));
/* 69 */             this.signLines[2] = this.signLines[2] + innerStack.getCount() + ":" + innerStack.getHoverName().getString() + " ";
/*    */           } 
/*    */         } 
/*    */       } 
/*    */ 
/*    */       
/* 75 */       this.signLines[3] = colorCode + "Price: " + this.itemPrice;
/*    */ 
/*    */       
/* 78 */       updateAdjacentSigns();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private void updateAdjacentSigns() {
/* 84 */     for (Direction dir : Direction.Plane.HORIZONTAL) {
/* 85 */       BlockPos signPos = this.worldPosition.relative(dir).below();
/* 86 */       TileEntity te = this.level.getBlockEntity(signPos);
/*    */       
/* 88 */       if (te instanceof TileUCSign) {
/* 89 */         TileUCSign ucsign = (TileUCSign)te;
/* 90 */         for (int i = 0; i < 4; i++)
/*    */         {
/* 92 */           ucsign.setMessage(i, (ITextComponent)new StringTextComponent(this.signLines[i]));
/*    */         }
/* 94 */         ucsign.updateSign();
/* 95 */         ucsign.setChanged();
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void tick() {
/*    */     super.tick();
/*    */   }
/*    */ }
