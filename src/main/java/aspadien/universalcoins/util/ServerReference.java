package aspadien.universalcoins.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber
public class ServerReference
{
  private static ServerWorld overworld;

  public static ServerWorld getOverworld() {
    if (overworld == null) {
      throw new IllegalStateException("Server not started yet!");
    }
    return overworld;
  }

  @SubscribeEvent
  public static void onServerStart(FMLServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    overworld = server.overworld();
  }
}
