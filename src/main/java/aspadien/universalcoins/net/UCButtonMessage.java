/*    */ package aspadien.universalcoins.net;
/*    */ import java.util.function.Supplier;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkEvent;
/*    */ import aspadien.universalcoins.tileentity.*;
/*    */
/*    */
/*    */
/*    */
/*    */

/*    */
/*    */ public class UCButtonMessage {
/*    */   private final BlockPos pos;
/*    */   private final int buttonId;
/*    */   
/*    */   public UCButtonMessage(BlockPos pos, int buttonId, boolean shiftPressed, int mouseButton) {
/* 21 */     this.pos = pos;
/* 22 */     this.buttonId = buttonId;
/* 23 */     this.shiftPressed = shiftPressed;
/* 24 */     this.mouseButton = mouseButton;
/*    */   }
/*    */   private final boolean shiftPressed; private final int mouseButton;
/*    */   public static void encode(UCButtonMessage msg, PacketBuffer buf) {
/* 28 */     buf.writeBlockPos(msg.pos);
/* 29 */     buf.writeInt(msg.buttonId);
/* 30 */     buf.writeBoolean(msg.shiftPressed);
/* 31 */     buf.writeInt(msg.mouseButton);
/*    */   }
/*    */   
/*    */   public static UCButtonMessage decode(PacketBuffer buf) {
/* 35 */     return new UCButtonMessage(buf.readBlockPos(), buf.readInt(), buf.readBoolean(), buf.readInt());
/*    */   }
/*    */ 
/*    */   
/*    */   public static void handle(UCButtonMessage msg, Supplier<NetworkEvent.Context> ctx) {
/* 40 */     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
/*    */           ServerPlayerEntity player = ((NetworkEvent.Context)ctx.get()).getSender();
/*    */           
/*    */           if (player == null) {
/*    */             return;
/*    */           }
/*    */           
/*    */           World world = player.level;
/*    */           
/*    */           if (world.isLoaded(msg.pos)) {
/*    */             TileEntity tileEntity = world.getBlockEntity(msg.pos);
/*    */             
/*    */             if (tileEntity instanceof TileTradeStation) {
/*    */               ((TileTradeStation)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed, msg.mouseButton);
/*    */             } else if (tileEntity instanceof TileATM) {
/*    */               ((TileATM)tileEntity).onButtonPressed(msg.buttonId);
/*    */             } else if (tileEntity instanceof TilePackager) {
/*    */               ((TilePackager)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
/*    */             } else if (tileEntity instanceof TileVendor) {
/*    */               ((TileVendor)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed, msg.mouseButton);
/*    */             } else if (tileEntity instanceof TileSignal) {
/*    */               ((TileSignal)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
/*    */             } else if (tileEntity instanceof TilePowerReceiver) {
/*    */               ((TilePowerReceiver)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
/*    */             } else if (tileEntity instanceof TilePowerTransmitter) {
/*    */               ((TilePowerTransmitter)tileEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
/*    */             } 
/*    */           } 
/*    */         });
/* 69 */     ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
/*    */   }
/*    */ }