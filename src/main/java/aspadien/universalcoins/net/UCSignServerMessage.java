/*    */ package aspadien.universalcoins.net;
/*    */ 
/*    */ import java.util.function.Supplier;
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.network.PacketBuffer;
/*    */ import net.minecraft.tileentity.TileEntity;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.util.text.ITextComponent;
/*    */ import net.minecraft.util.text.StringTextComponent;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkEvent;
/*    */ import aspadien.universalcoins.tileentity.TileUCSign;
/*    */ 
/*    */ public class UCSignServerMessage
/*    */ {
/*    */   private final BlockPos pos;
/*    */   private final ITextComponent[] lines;
/*    */   
/*    */   public UCSignServerMessage(BlockPos pos, ITextComponent[] lines) {
/* 20 */     this.pos = pos;
/* 21 */     this.lines = lines;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void encode(UCSignServerMessage msg, PacketBuffer buf) {
/* 26 */     buf.writeBlockPos(msg.pos);
/* 27 */     for (int i = 0; i < 4; i++) {
/*    */       
/* 29 */       ITextComponent line = (msg.lines[i] != null) ? msg.lines[i] : (ITextComponent)new StringTextComponent("");
/* 30 */       buf.writeComponent(line);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static UCSignServerMessage decode(PacketBuffer buf) {
/* 36 */     BlockPos pos = buf.readBlockPos();
/* 37 */     ITextComponent[] lines = new ITextComponent[4];
/* 38 */     for (int i = 0; i < 4; i++) {
/* 39 */       lines[i] = buf.readComponent();
/*    */     }
/* 41 */     return new UCSignServerMessage(pos, lines);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void handle(UCSignServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
/* 46 */     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
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
/*    */             if (tileEntity instanceof TileUCSign) {
/*    */               TileUCSign ucsign = (TileUCSign)tileEntity;
/*    */               for (int i = 0; i < 4; i++) {
/*    */                 ucsign.setMessage(i, msg.lines[i]);
/*    */               }
/*    */               ucsign.updateSign();
/*    */               ucsign.setChanged();
/*    */             } 
/*    */           } 
/*    */         });
/* 68 */     ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
/*    */   }
/*    */ }
