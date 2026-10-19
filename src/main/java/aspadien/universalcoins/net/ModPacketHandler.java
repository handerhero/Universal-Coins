/*    */ package aspadien.universalcoins.net;
/*    */ 
/*    */ import net.minecraft.entity.player.ServerPlayerEntity;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.math.BlockPos;
/*    */ import net.minecraft.world.World;
/*    */ import net.minecraftforge.fml.network.NetworkDirection;
/*    */ import net.minecraftforge.fml.network.NetworkRegistry;
/*    */ import net.minecraftforge.fml.network.PacketDistributor;
/*    */ import net.minecraftforge.fml.network.simple.SimpleChannel;
/*    */ 
/*    */ public class ModPacketHandler {
/* 13 */   private static int packetId = 0;
/*    */   
/*    */   private static final String PROTOCOL_VERSION = "1";
/* 16 */   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("universalcoins", "main_channel"), () -> "1", "1"::equals, "1"::equals);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void registerMessages() {
/* 25 */     int id = 0;
/*    */     
/* 27 */     INSTANCE.registerMessage(id++, UCButtonMessage.class, UCButtonMessage::encode, UCButtonMessage::decode, UCButtonMessage::handle);
/*    */     
/* 29 */     INSTANCE.registerMessage(id++, UCVendorServerMessage.class, UCVendorServerMessage::encode, UCVendorServerMessage::decode, UCVendorServerMessage::handle);
/*    */     
/* 31 */     INSTANCE.registerMessage(id++, ATMWithdrawalMessage.class, ATMWithdrawalMessage::encode, ATMWithdrawalMessage::decode, ATMWithdrawalMessage::handle);
/*    */     
/* 33 */     INSTANCE.registerMessage(id++, UCPackagerServerMessage.class, UCPackagerServerMessage::encode, UCPackagerServerMessage::decode, UCPackagerServerMessage::handle);
/*    */     
/* 35 */     INSTANCE.registerMessage(id++, UCTileSignMessage.class, UCTileSignMessage::encode, UCTileSignMessage::decode, UCTileSignMessage::handle);
/*    */     
/* 37 */     INSTANCE.registerMessage(id++, UCSignServerMessage.class, UCSignServerMessage::encode, UCSignServerMessage::decode, UCSignServerMessage::handle);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void sendToServer(Object msg) {
/* 44 */     INSTANCE.sendToServer(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void sendToPlayer(Object msg, ServerPlayerEntity player) {
/* 51 */     INSTANCE.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void sendToAllAround(Object msg, World world, BlockPos pos) {
/* 58 */     if (!(world instanceof net.minecraft.world.server.ServerWorld))
/* 59 */       return;  INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.dimension())), msg);
/*    */   }
/*    */ }