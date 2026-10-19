package aspadien.universalcoins.init;

import aspadien.universalcoins.UniversalCoins;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTab {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
          DeferredRegister.create(Registries.CREATIVE_MODE_TAB, UniversalCoins.MOD_ID);

  public static final RegistryObject<CreativeModeTab> UNIVERSAL_COINS_TAB = CREATIVE_MODE_TABS.register("universal_coins_tab",
          () -> CreativeModeTab.builder()
                  .title(Component.translatable("itemGroup.universalcoins.tab"))
                  .icon(() -> new ItemStack(ModItems.GOLD_COIN.get()))
                  // Автоматически добавляет в эту вкладку ВСЕ зарегистрированные предметы мода
                  .displayItems((parameters, output) -> {
                    ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                  })
                  .build()
  );

  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}