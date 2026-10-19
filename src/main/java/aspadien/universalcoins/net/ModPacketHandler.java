 package aspadien.universalcoins.net;


import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;


 public class ModPacketHandler {
   private static int packetId = 0;

   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("universalcoins", "main_channel"), () -> "1", "1"::equals, "1"::equals);

   public static void registerMessages() {
     int id = 0;

     INSTANCE.registerMessage(id++, UCButtonMessage.class, UCButtonMessage::encode, UCButtonMessage::decode, UCButtonMessage::handle);

     INSTANCE.registerMessage(id++, UCVendorServerMessage.class, UCVendorServerMessage::encode, UCVendorServerMessage::decode, UCVendorServerMessage::handle);

     INSTANCE.registerMessage(id++, ATMWithdrawalMessage.class, ATMWithdrawalMessage::encode, ATMWithdrawalMessage::decode, ATMWithdrawalMessage::handle);

     INSTANCE.registerMessage(id++, UCPackagerServerMessage.class, UCPackagerServerMessage::encode, UCPackagerServerMessage::decode, UCPackagerServerMessage::handle);

     INSTANCE.registerMessage(id++, UCTileSignMessage.class, UCTileSignMessage::encode, UCTileSignMessage::decode, UCTileSignMessage::handle);

     INSTANCE.registerMessage(id++, UCSignServerMessage.class, UCSignServerMessage::encode, UCSignServerMessage::decode, UCSignServerMessage::handle);
   }


   public static void sendToServer(Object msg) {
     INSTANCE.sendToServer(msg);
   }

   public static void sendToPlayer(Object msg, ServerPlayer player) {
     INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
   }

   public static void sendToAllAround(Object msg, Level level, BlockPos pos) {
     if (!(level instanceof ServerLevel))
       return;  INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 64.0D, level.dimension())), msg);
   }
 }