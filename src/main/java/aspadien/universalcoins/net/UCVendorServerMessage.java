 package aspadien.universalcoins.net;

 import java.util.function.Supplier;

 import aspadien.universalcoins.blockentities.Vendor;
 import net.minecraft.core.BlockPos;
 import net.minecraft.network.FriendlyByteBuf;
 import net.minecraft.server.level.ServerPlayer;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraft.world.level.block.state.BlockState;
 import net.minecraftforge.network.NetworkEvent;


 public class UCVendorServerMessage
 {
   private final BlockPos pos;
   private final int itemPrice;
   private final String blockOwner;
   private final boolean infinite;

   public UCVendorServerMessage(BlockPos pos, int price, String blockOwner, boolean infinite) {
     this.pos = pos;
     this.itemPrice = price;
     this.blockOwner = blockOwner;
     this.infinite = infinite;
   }


   public static void encode(UCVendorServerMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeInt(msg.itemPrice);
     buf.writeUtf(msg.blockOwner);
     buf.writeBoolean(msg.infinite);
   }


   public static UCVendorServerMessage decode(FriendlyByteBuf buf) {
     return new UCVendorServerMessage(buf
         .readBlockPos(), buf
         .readInt(), buf
         .readUtf(32767), buf
         .readBoolean());
   }



   public static void handle(UCVendorServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayer player = ctx.get().getSender();

           if (player == null) {
             return;
           }

           Level world = player.level();

           if (world.isLoaded(msg.pos)) {
             BlockEntity tileEntity = world.getBlockEntity(msg.pos);

             if (tileEntity instanceof Vendor) {
               Vendor vendor = (Vendor)tileEntity;

               vendor.itemPrice = msg.itemPrice;

               vendor.blockOwner = msg.blockOwner;

               vendor.infiniteMode = msg.infinite;

               vendor.setChanged();

               vendor.updateTE();
               BlockState state = world.getBlockState(msg.pos);
               world.sendBlockUpdated(msg.pos, state, state, 3);
             }
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }