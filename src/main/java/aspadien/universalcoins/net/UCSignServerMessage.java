 package aspadien.universalcoins.net;

 import java.util.function.Supplier;

 import net.minecraft.core.BlockPos;
 import net.minecraft.network.FriendlyByteBuf;
 import aspadien.universalcoins.blockentities.UCSign;
 import net.minecraft.network.chat.Component;
 import net.minecraft.server.level.ServerPlayer;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraftforge.network.NetworkEvent;

 public class UCSignServerMessage
 {
   private final BlockPos pos;
   private final Component[] lines;

   public UCSignServerMessage(BlockPos pos, Component[] lines) {
     this.pos = pos;
     this.lines = lines;
   }


   public static void encode(UCSignServerMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     for (int i = 0; i < 4; i++) {

       Component line = (msg.lines[i] != null) ? msg.lines[i] : Component.literal("");
       buf.writeComponent(line);
     }
   }


   public static UCSignServerMessage decode(FriendlyByteBuf buf) {
     BlockPos pos = buf.readBlockPos();
     Component[] lines = new Component[4];
     for (int i = 0; i < 4; i++) {
       lines[i] = buf.readComponent();
     }
     return new UCSignServerMessage(pos, lines);
   }


   public static void handle(UCSignServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayer player = ctx.get().getSender();

           if (player == null) {
             return;
           }

           Level level = player.level();

           if (level.isLoaded(msg.pos)) {
             BlockEntity tileEntity = level.getBlockEntity(msg.pos);

             if (tileEntity instanceof UCSign) {
               UCSign ucsign = (UCSign)tileEntity;
               for (int i = 0; i < 4; i++) {
                 ucsign.setMessage(i, msg.lines[i]);
               }
               ucsign.updateSign();
               ucsign.setChanged();
             }
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }
