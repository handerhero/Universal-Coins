/*    */ package aspadien.universalcoins.net;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraftforge.api.distmarker.Dist;
/*    */ import net.minecraftforge.api.distmarker.OnlyIn;
/*    */ import net.minecraftforge.fml.network.NetworkEvent;
/*    */ import aspadien.universalcoins.tileentity.TileUCSign;
/*    */ 
/*    */ public class UCTileSignMessage
/*    */ {
/*    */   private final BlockPos pos;
/*    */   private final ITextComponent[] signText;
/*    */   private final String blockOwner;
/*    */   
/*    */   public UCTileSignMessage(BlockPos pos, ITextComponent[] text, String owner) {
/* 21 */     this.pos = pos;
/* 22 */     this.signText = text;
/* 23 */     this.blockOwner = owner;
/*    */   }
/*    */   
/*    */   public static void encode(UCTileSignMessage msg, PacketBuffer buf) {
/* 27 */     buf.writeBlockPos(msg.pos);
/* 28 */     for (int i = 0; i < 4; i++) {
/* 29 */       buf.writeComponent(msg.signText[i]);
/*    */     }
/* 31 */     buf.writeUtf(msg.blockOwner);
/*    */   }
/*    */   
/*    */   public static UCTileSignMessage decode(PacketBuffer buf) {
/* 35 */     BlockPos pos = buf.readBlockPos();
/* 36 */     ITextComponent[] lines = new ITextComponent[4];
/* 37 */     for (int i = 0; i < 4; i++) {
/* 38 */       lines[i] = buf.readComponent();
/*    */     }
/* 40 */     String owner = buf.readUtf(32767);
/* 41 */     return new UCTileSignMessage(pos, lines, owner);
/*    */   }
/*    */   
/*    */   public static void handle(UCTileSignMessage msg, Supplier<NetworkEvent.Context> ctx) {
/* 45 */     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> processClient(msg));
/*    */ 
/*    */ 
/*    */     
/* 49 */     ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
/*    */   }
/*    */   
/*    */   @OnlyIn(Dist.CLIENT)
/*    */   private static void processClient(UCTileSignMessage msg) {
/* 54 */     TileEntity te = (Minecraft.getInstance()).level.getBlockEntity(msg.pos);
/* 55 */     if (te instanceof TileUCSign) {
/* 56 */       TileUCSign sign = (TileUCSign)te;
/* 57 */       for (int i = 0; i < 4; i++) {
/* 58 */         sign.setMessage(i, msg.signText[i]);
/*    */       }
/* 60 */       sign.blockOwner = msg.blockOwner;
/*    */     } 
/*    */   }
/*    */ }