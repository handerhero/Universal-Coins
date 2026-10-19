 package aspadien.universalcoins.net;

 import java.util.function.Supplier;

 import net.minecraft.core.BlockPos;
 import net.minecraft.network.FriendlyByteBuf;
 import aspadien.universalcoins.blockentities.ATM;
 import net.minecraft.server.level.ServerPlayer;
 import net.minecraft.world.level.Level;
 import net.minecraft.world.level.block.entity.BlockEntity;
 import net.minecraftforge.network.NetworkEvent;

 public class ATMWithdrawalMessage
 {
   private final BlockPos pos;
   private final int withdrawalAmount;

   public ATMWithdrawalMessage(BlockPos pos, int amount) {
     this.pos = pos;
     this.withdrawalAmount = amount;
   }

   public static void encode(ATMWithdrawalMessage msg, FriendlyByteBuf buf) {
     buf.writeBlockPos(msg.pos);
     buf.writeInt(msg.withdrawalAmount);
   }

   public static ATMWithdrawalMessage decode(FriendlyByteBuf buf) {
     return new ATMWithdrawalMessage(buf.readBlockPos(), buf.readInt());
   }

   public static void handle(ATMWithdrawalMessage msg, Supplier<NetworkEvent.Context> ctx) {
     ctx.get().enqueueWork(() -> {
           ServerPlayer player = ctx.get().getSender();
           if (player == null)
             return;
           Level world = player.level();
           if (world.isLoaded(msg.pos)) {
             BlockEntity te = world.getBlockEntity(msg.pos);
             if (te instanceof ATM)
               ((ATM)te).startCoinWithdrawal(msg.withdrawalAmount);
           }
         });
     ctx.get().setPacketHandled(true);
   }
 }

