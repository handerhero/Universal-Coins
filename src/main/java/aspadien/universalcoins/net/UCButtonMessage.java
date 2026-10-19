 package aspadien.universalcoins.net;
 import java.util.function.Supplier;
 import net.minecraft.core.BlockPos;
 import aspadien.universalcoins.blockentities.*;
 import net.minecraft.network.FriendlyByteBuf;
 import net.minecraft.server.level.ServerPlayer;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraftforge.network.NetworkEvent;


 public class UCButtonMessage {
   private final BlockPos pos;
   private final int buttonId;

   public UCButtonMessage(BlockPos pos, int buttonId, boolean shiftPressed, int mouseButton) {
     this.pos = pos;
     this.buttonId = buttonId;
     this.shiftPressed = shiftPressed;
     this.mouseButton = mouseButton;
   }
   private final boolean shiftPressed; private final int mouseButton;
       public static void encode(UCButtonMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeInt(msg.buttonId);
     buf.writeBoolean(msg.shiftPressed);
     buf.writeInt(msg.mouseButton);
   }

   public static UCButtonMessage decode(FriendlyByteBuf buf) {
     return new UCButtonMessage(buf.readBlockPos(), buf.readInt(), buf.readBoolean(), buf.readInt());
   }


   public static void handle(UCButtonMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayer player = ctx.get().getSender();

           if (player == null) {
             return;
           }

           Level level = player.level();

           if (level.isLoaded(msg.pos)) {
             BlockEntity blockEntity = level.getBlockEntity(msg.pos);

             if (blockEntity instanceof TradeStation) {
               ((TradeStation)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed, msg.mouseButton);
             } else if (blockEntity instanceof ATM) {
               ((ATM)blockEntity).onButtonPressed(msg.buttonId);
             } else if (blockEntity instanceof Packager) {
               ((Packager)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
             } else if (blockEntity instanceof Vendor) {
               ((Vendor)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed, msg.mouseButton);
             } else if (blockEntity instanceof Signal) {
               ((Signal)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
             } else if (blockEntity instanceof PowerReceiver) {
               ((PowerReceiver)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
             } else if (blockEntity instanceof PowerTransmitter) {
               ((PowerTransmitter)blockEntity).onButtonPressed(msg.buttonId, msg.shiftPressed);
             }
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }