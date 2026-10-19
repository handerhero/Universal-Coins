 package aspadien.universalcoins.net;

 import java.util.function.Supplier;
 import net.minecraft.entity.player.ServerPlayerEntity;
 import net.minecraft.network.PacketBuffer;
 import net.minecraft.tileentity.TileEntity;
 import net.minecraft.util.math.BlockPos;
 import net.minecraft.world.World;
 import net.minecraftforge.fml.network.NetworkEvent;
 import aspadien.universalcoins.tileentity.TileATM;

 public class ATMWithdrawalMessage
 {
   private final BlockPos pos;
   private final int withdrawalAmount;

   public ATMWithdrawalMessage(BlockPos pos, int amount) {
     this.pos = pos;
     this.withdrawalAmount = amount;
   }

   public static void encode(ATMWithdrawalMessage msg, PacketBuffer buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeInt(msg.withdrawalAmount);
   }

   public static ATMWithdrawalMessage decode(PacketBuffer buf) {
     return new ATMWithdrawalMessage(buf.readBlockPos(), buf.readInt());
   }

   public static void handle(ATMWithdrawalMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayerEntity player = ctx.get().getSender();
           if (player == null)
             return;
           World world = player.level;
           if (world.isLoaded(msg.pos)) {
             TileEntity te = world.getBlockEntity(msg.pos);
             if (te instanceof TileATM)
               ((TileATM)te).startCoinWithdrawal(msg.withdrawalAmount);
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }

