 package aspadien.universalcoins.net;

 import java.util.function.Supplier;

 import net.minecraft.core.BlockPos;
 import net.minecraft.network.FriendlyByteBuf;
 import aspadien.universalcoins.blockentities.Packager;
 import net.minecraft.server.level.ServerPlayer;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraftforge.network.NetworkEvent;

 public class UCPackagerServerMessage
 {
   private final BlockPos pos;
   private final String packageTarget;
   private final boolean tabPressed;

   public UCPackagerServerMessage(BlockPos pos, String target, boolean tab) {
     this.pos = pos;
     this.packageTarget = target;
     this.tabPressed = tab;
   }

   public static void encode(UCPackagerServerMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeUtf(msg.packageTarget);
     buf.writeBoolean(msg.tabPressed);
   }

   public static UCPackagerServerMessage decode(FriendlyByteBuf buf) {
     return new UCPackagerServerMessage(buf.readBlockPos(), buf.readUtf(32767), buf.readBoolean());
   }

   public static void handle(UCPackagerServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
           ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
           if (player == null) {
             return;
           }
           Level level = player.level();
           if (level.isLoaded(msg.pos)) {
             BlockEntity te = level.getBlockEntity(msg.pos);
             if (te instanceof Packager)
               ((Packager)te).playerLookup(msg.packageTarget, msg.tabPressed);
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }