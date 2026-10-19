/*    */ package aspadien.universalcoins.container;
/*    */ 
/*    */ import net.minecraft.entity.player.PlayerEntity;
/*    */ import net.minecraft.entity.player.PlayerInventory;
/*    */ import net.minecraft.inventory.container.Container;
/*    */ import net.minecraft.inventory.container.ContainerType;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.util.IIntArray;
/*    */ import net.minecraft.util.IntArray;
/*    */ import aspadien.universalcoins.init.ModContainerTypes;
/*    */ import aspadien.universalcoins.tileentity.TileVendor;
/*    */ 
/*    */ public class ContainerVendorWrench
/*    */   extends Container {
/*    */   private final TileVendor tileEntity;
/*    */   private final IIntArray data;
/*    */   
/*    */   public ContainerVendorWrench(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
/* 20 */     this(windowId, playerInv, (TileVendor)playerInv.player.level.getBlockEntity(extraData.readBlockPos()), (IIntArray)new IntArray(1));
/*    */   }
/*    */ 
/*    */   
/*    */   public ContainerVendorWrench(int windowId, PlayerInventory playerInv, TileVendor te, IIntArray data) {
/* 25 */     super((ContainerType)ModContainerTypes.VENDOR_WRENCH.get(), windowId);
/* 26 */     this.tileEntity = te;
/* 27 */     this.data = data;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 32 */     addDataSlots(data);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean stillValid(PlayerEntity player) {
/* 37 */     return (this.tileEntity != null && this.tileEntity.stillValid(player));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ItemStack quickMoveStack(PlayerEntity player, int index) {
/* 45 */     return ItemStack.EMPTY;
/*    */   }
/*    */ 
/*    */   
/*    */   public void removed(PlayerEntity player) {
/* 50 */     super.removed(player);
/*    */     
/* 52 */     if (!player.level.isClientSide && this.tileEntity != null) {
/* 53 */       this.tileEntity.inUseCleanup();
/*    */     }
/*    */   }
/*    */   
/*    */   public TileVendor getTile() {
/* 58 */     return this.tileEntity;
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/container/ContainerVendorWrench.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */