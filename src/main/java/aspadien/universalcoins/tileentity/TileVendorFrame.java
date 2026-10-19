/*    */ package aspadien.universalcoins.tileentity;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */ import net.minecraft.inventory.container.Container;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.tileentity.TileEntityType;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.util.text.TranslationTextComponent;
/*    */ import aspadien.universalcoins.container.ContainerVendor;
/*    */ import aspadien.universalcoins.container.ContainerVendorBuy;
/*    */ import aspadien.universalcoins.container.ContainerVendorSell;
/*    */ import aspadien.universalcoins.init.ModTileEntities;
/*    */ 
/*    */ public class TileVendorFrame
/*    */   extends TileVendor {
/* 21 */   private final String[] signLines = new String[] { "", "", "", "" };
/*    */   
/*    */   public TileVendorFrame() {
/* 24 */     super((TileEntityType)ModTileEntities.VENDOR_FRAME.get());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
/* 32 */     boolean isOwner = player.getName().getString().equals(this.blockOwner);
/*    */     
/* 34 */     if (isOwner) {
/* 35 */       return (Container)new ContainerVendor(windowId, playerInv, this, this.vendorData, this);
/*    */     }
/* 37 */     return this.sellMode ? (Container)new ContainerVendorSell(windowId, playerInv, this, this.vendorData, this) : (Container)new ContainerVendorBuy(windowId, playerInv, this, this.vendorData, this);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ITextComponent getDisplayName() {
/* 45 */     return (ITextComponent)new TranslationTextComponent("tile.aspadien.universalcoins.vendor_frame.name");
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick() {
/* 50 */     super.tick();
/* 51 */     if (this.level != null && !this.level.isClientSide && this.level.getGameTime() % 40L == 0L) {
/* 52 */       updateSigns();
/*    */     }
/*    */   }
/*    */   
/*    */   public void updateSigns() {
/* 57 */     if (this.level == null || this.level.isClientSide)
/* 58 */       return;  ItemStack tradeStack = getItem(9);
/* 59 */     if (tradeStack.isEmpty())
/*    */       return; 
/* 61 */     String colorCode = "&" + Integer.toHexString(this.textColor);
/* 62 */     this.signLines[0] = colorCode + (this.sellMode ? "Selling" : "Buying");
/*    */     
/* 64 */     updateSignAt(this.worldPosition.below());
/* 65 */     updateSignAt(this.worldPosition.above());
/*    */   }
/*    */ 
/*    */   
/*    */   private void updateSignAt(BlockPos targetPos) {
/* 70 */     if (this.level == null || !this.level.isLoaded(targetPos))
/*    */       return; 
/* 72 */     TileEntity te = this.level.getBlockEntity(targetPos);
/* 73 */     if (te instanceof TileUCSign) {
/* 74 */       TileUCSign ucsign = (TileUCSign)te;
/* 75 */       for (int i = 0; i < 4; i++) {
/* 76 */         ucsign.setMessage(i, (ITextComponent)new StringTextComponent(this.signLines[i]));
/*    */       }
/* 78 */       ucsign.updateSign();
/* 79 */       ucsign.setChanged();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/tileentity/TileVendorFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */