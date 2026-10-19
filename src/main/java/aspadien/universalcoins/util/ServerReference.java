package aspadien.universalcoins.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ServerReference
{
  private static ServerLevel overworld;

  public static ServerLevel getOverworld() {
    if (overworld == null) {
      throw new IllegalStateException("Server not started yet!");
    }
    return overworld;
  }

  @SubscribeEvent
  public static void onServerStart(ServerStartingEvent event) {
    MinecraftServer server = event.getServer();
    overworld = server.overworld();
  }
}
