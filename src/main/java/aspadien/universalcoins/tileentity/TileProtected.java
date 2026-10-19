/*    */ package aspadien.universalcoins.tileentity;
/*    */ 
/*    */ import net.minecraft.block.BlockState;
/*    */ import net.minecraft.nbt.CompoundNBT;
/*    */ import net.minecraft.network.NetworkManager;
/*    */ import net.minecraft.network.play.server.SUpdateTileEntityPacket;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.tileentity.TileEntityType;
/*    */ import aspadien.universalcoins.net.ModPacketHandler;
/*    */ import aspadien.universalcoins.net.UCButtonMessage;
/*    */ 
/*    */ public abstract class TileProtected
/*    */   extends TileEntity
/*    */ {
/* 15 */   public String blockOwner = "none";
/* 16 */   public String playerName = "";
/*    */   public boolean inUse = false;
/*    */   
/*    */   public TileProtected(TileEntityType<?> type) {
/* 20 */     super(type);
/*    */   }
/*    */ 
/*    */   
/*    */   public void load(BlockState state, CompoundNBT tag) {
/* 25 */     super.load(state, tag);
/* 26 */     this.inUse = tag.getBoolean("InUse");
/* 27 */     this.playerName = tag.contains("playerName") ? tag.getString("playerName") : "none";
/* 28 */     this.blockOwner = tag.contains("blockOwner") ? tag.getString("blockOwner") : "none";
/*    */   }
/*    */ 
/*    */   
/*    */   public CompoundNBT save(CompoundNBT tag) {
/* 33 */     super.save(tag);
/* 34 */     tag.putBoolean("InUse", this.inUse);
/* 35 */     tag.putString("playerName", this.playerName);
/* 36 */     tag.putString("blockOwner", this.blockOwner);
/* 37 */     return tag;
/*    */   }
/*    */   public void inUseCleanup() {
/* 40 */     if (this.level != null && this.level.isClientSide)
/*    */       return; 
/* 42 */     this.inUse = false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public CompoundNBT getUpdateTag() {
/* 48 */     return save(new CompoundNBT());
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleUpdateTag(BlockState state, CompoundNBT tag) {
/* 53 */     load(state, tag);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SUpdateTileEntityPacket getUpdatePacket() {
/* 60 */     return new SUpdateTileEntityPacket(this.worldPosition, 0, getUpdateTag());
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
/* 65 */     if (this.level != null)
/*    */     {
/* 67 */       load(this.level.getBlockState(this.worldPosition), pkt.getTag());
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void updateTE() {
/* 75 */     if (this.level != null) {
/* 76 */       BlockState state = this.level.getBlockState(this.worldPosition);
/* 77 */       this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
/* 78 */       setChanged();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void sendButtonMessage(int functionID, boolean shiftPressed) {
/* 83 */     sendButtonMessage(functionID, shiftPressed, 0);
/*    */   }
/*    */   
/*    */   public void sendButtonMessage(int functionID, boolean shiftPressed, int mouseButton) {
/* 87 */     ModPacketHandler.INSTANCE.sendToServer(new UCButtonMessage(this.worldPosition, functionID, shiftPressed, mouseButton));
/*    */   }
/*    */ }
