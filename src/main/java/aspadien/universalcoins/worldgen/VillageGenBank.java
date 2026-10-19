package aspadien.universalcoins.worldgen;

import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import aspadien.universalcoins.UniversalCoins;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VillageGenBank
{
  @SubscribeEvent
  public static void setup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
          int weight = UniversalCoins.bankGenWeight;
          ResourceLocation bankStructurePlains = new ResourceLocation(MOD_ID, "village/bank_plains");
          ResourceLocation bankStructureTaiga = new ResourceLocation(MOD_ID, "village/bank_taiga");
          ResourceLocation bankStructureDesert = new ResourceLocation(MOD_ID, "village/bank_desert");
          ResourceLocation bankStructureSavanna = new ResourceLocation(MOD_ID, "village/bank_savanna");
          ResourceLocation bankStructureSnowy = new ResourceLocation(MOD_ID, "village/bank_snowy");
          addBuildingToPool(new ResourceLocation("minecraft", "village/plains/houses"), bankStructurePlains, weight);
          addBuildingToPool(new ResourceLocation("minecraft", "village/desert/houses"), bankStructureDesert, weight-1);
          addBuildingToPool(new ResourceLocation("minecraft", "village/savanna/houses"), bankStructureSavanna, weight);
          addBuildingToPool(new ResourceLocation("minecraft", "village/taiga/houses"), bankStructureTaiga, weight+1);
          addBuildingToPool(new ResourceLocation("minecraft", "village/snowy/houses"), bankStructureSnowy, weight);
        });
  }





  private static void addBuildingToPool(ResourceLocation poolId, ResourceLocation nbtLocation, int weight) {
    JigsawPattern pool = WorldGenRegistries.TEMPLATE_POOL.get(poolId);
    if (pool == null)
      return;
    JigsawPiece piece = JigsawPiece.legacy(nbtLocation.toString()).apply(JigsawPattern.PlacementBehaviour.RIGID);



    try {
      Field templatesField = ObfuscationReflectionHelper.findField(JigsawPattern.class, "field_214953_e");
      templatesField.setAccessible(true);


      Field rawTemplatesField = ObfuscationReflectionHelper.findField(JigsawPattern.class, "field_214952_d");
      rawTemplatesField.setAccessible(true);

      List<JigsawPiece> templates = (List<JigsawPiece>)templatesField.get(pool);
      List<Pair<JigsawPiece, Integer>> rawTemplates = (List<Pair<JigsawPiece, Integer>>)rawTemplatesField.get(pool);


      if (templates.getClass().getName().contains("Immutable") || templates.getClass().getName().contains("Arrays$ArrayList")) {
        templates = new ArrayList<>(templates);
        templatesField.set(pool, templates);
      }
      if (rawTemplates.getClass().getName().contains("Immutable") || rawTemplates.getClass().getName().contains("Arrays$ArrayList")) {
        rawTemplates = new ArrayList<>(rawTemplates);
        rawTemplatesField.set(pool, rawTemplates);
      }


      rawTemplates.add(Pair.of(piece, weight));
      for (int i = 0; i < weight; i++) {
        templates.add(piece);
      }
    }
    catch (Exception e) {
        UniversalCoins.LOGGER.error("Failed to inject village bank structure into {}", poolId, e);
    }
  }
}
