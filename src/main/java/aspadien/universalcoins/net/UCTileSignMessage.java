 package aspadien.universalcoins.net;

 import java.util.function.Supplier;
 import net.minecraft.client.Minecraft;
 import net.minecraft.core.BlockPos;
 import net.minecraft.network.FriendlyByteBuf;
 import net.minecraft.network.chat.Component;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraftforge.api.distmarker.Dist;
 import net.minecraftforge.api.distmarker.OnlyIn;
 import aspadien.universalcoins.blockentities.UCSign;
 import net.minecraftforge.network.NetworkEvent;

 public class UCTileSignMessage
 {
   private final BlockPos pos;
   private final Component[] signText;
   private final String blockOwner;

   public UCTileSignMessage(BlockPos pos, Component[] text, String owner) {
     this.pos = pos;
     this.signText = text;
     this.blockOwner = owner;
   }

   public static void encode(UCTileSignMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     for (int i = 0; i < 4; i++) {
       buf.writeComponent(msg.signText[i]);
     }
     buf.writeUtf(msg.blockOwner);
   }

   public static UCTileSignMessage decode(FriendlyByteBuf buf) {
     BlockPos pos = buf.readBlockPos();
     Component[] lines = new Component[4];
     for (int i = 0; i < 4; i++) {
       lines[i] = buf.readComponent();
     }
     String owner = buf.readUtf(32767);
     return new UCTileSignMessage(pos, lines, owner);
   }

   public static void handle(UCTileSignMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> processClient(msg));



     ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
   }

   @OnlyIn(Dist.CLIENT)
   private static void processClient(UCTileSignMessage msg) {
     BlockEntity te = (Minecraft.getInstance()).level.getBlockEntity(msg.pos);
     if (te instanceof UCSign) {
       UCSign sign = (UCSign)te;
       for (int i = 0; i < 4; i++) {
         sign.setMessage(i, msg.signText[i]);
       }
       sign.blockOwner = msg.blockOwner;
     }
   }
 }