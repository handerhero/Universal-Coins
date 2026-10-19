/*    */ package aspadien.universalcoins.net;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkEvent;
/*    */ import aspadien.universalcoins.tileentity.TilePackager;
/*    */ 
/*    */ public class UCPackagerServerMessage
/*    */ {
/*    */   private final BlockPos pos;
/*    */   private final String packageTarget;
/*    */   private final boolean tabPressed;
/*    */   
/*    */   public UCPackagerServerMessage(BlockPos pos, String target, boolean tab) {
/* 19 */     this.pos = pos;
/* 20 */     this.packageTarget = target;
/* 21 */     this.tabPressed = tab;
/*    */   }
/*    */   
/*    */   public static void encode(UCPackagerServerMessage msg, PacketBuffer buf) {
/* 25 */     buf.writeBlockPos(msg.pos);
/* 26 */     buf.writeUtf(msg.packageTarget);
/* 27 */     buf.writeBoolean(msg.tabPressed);
/*    */   }
/*    */   
/*    */   public static UCPackagerServerMessage decode(PacketBuffer buf) {
/* 31 */     return new UCPackagerServerMessage(buf.readBlockPos(), buf.readUtf(32767), buf.readBoolean());
/*    */   }
/*    */   
/*    */   public static void handle(UCPackagerServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
/* 35 */     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
/*    */           ServerPlayerEntity player = ((NetworkEvent.Context)ctx.get()).getSender();
/*    */           if (player == null) {
/*    */             return;
/*    */           }
/*    */           World world = player.level;
/*    */           if (world.isLoaded(msg.pos)) {
/*    */             TileEntity te = world.getBlockEntity(msg.pos);
/*    */             if (te instanceof TilePackager)
/*    */               ((TilePackager)te).playerLookup(msg.packageTarget, msg.tabPressed); 
/*    */           } 
/*    */         });
/* 47 */     ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
/*    */   }
/*    */ }