 package aspadien.universalcoins.net;

 import java.util.function.Supplier;
 import net.minecraft.block.BlockState;
 import net.minecraft.entity.player.ServerPlayerEntity;
 import net.minecraft.network.PacketBuffer;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkEvent;
 import aspadien.universalcoins.tileentity.TileVendor;



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


   public static void encode(UCVendorServerMessage msg, PacketBuffer buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeInt(msg.itemPrice);
     buf.writeUtf(msg.blockOwner);
     buf.writeBoolean(msg.infinite);
   }


   public static UCVendorServerMessage decode(PacketBuffer buf) {
     return new UCVendorServerMessage(buf
         .readBlockPos(), buf
         .readInt(), buf
         .readUtf(32767), buf
         .readBoolean());
   }



   public static void handle(UCVendorServerMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayerEntity player = ctx.get().getSender();

           if (player == null) {
             return;
           }

           World world = player.level;

           if (world.isLoaded(msg.pos)) {
             TileEntity tileEntity = world.getBlockEntity(msg.pos);

             if (tileEntity instanceof TileVendor) {
               TileVendor vendor = (TileVendor)tileEntity;

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