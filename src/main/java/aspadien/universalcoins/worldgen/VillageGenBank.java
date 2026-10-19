package aspadien.universalcoins.worldgen;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import aspadien.universalcoins.UniversalCoins;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static aspadien.universalcoins.UniversalCoins.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VillageGenBank {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY =
            ResourceKey.create(Registries.PROCESSOR_LIST, ResourceLocation.parse("minecraft:empty"));

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry = event.getServer().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = event.getServer().registryAccess().registryOrThrow(Registries.PROCESSOR_LIST);

        Holder<StructureProcessorList> emptyProcessorList = processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        int weight = UniversalCoins.bankGenWeight;

        addBuildingToPool(templatePoolRegistry, emptyProcessorList,
                ResourceLocation.parse("minecraft:village/plains/houses"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "village/bank_plains"), weight);
        addBuildingToPool(templatePoolRegistry, emptyProcessorList,
                ResourceLocation.parse("minecraft:village/desert/houses"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "village/bank_desert"), weight - 1);
        addBuildingToPool(templatePoolRegistry, emptyProcessorList,
                ResourceLocation.parse("minecraft:village/savanna/houses"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "village/bank_savanna"), weight);
        addBuildingToPool(templatePoolRegistry, emptyProcessorList,
                ResourceLocation.parse("minecraft:village/taiga/houses"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "village/bank_taiga"), weight + 1);
        addBuildingToPool(templatePoolRegistry, emptyProcessorList,
                ResourceLocation.parse("minecraft:village/snowy/houses"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "village/bank_snowy"), weight);
    }

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                          Holder<StructureProcessorList> processorList,
                                          ResourceLocation poolId,
                                          ResourceLocation nbtLocation,
                                          int weight) {
        StructureTemplatePool pool = templatePoolRegistry.get(poolId);
        if (pool == null) return;

        // Создаем SinglePoolElement (в 1.20.1 legacy используется для деревень)
        SinglePoolElement piece = SinglePoolElement.legacy(nbtLocation.toString(), processorList)
                .apply(StructureTemplatePool.Projection.RIGID);

        try {
            // В официальных маппингах 1.20.1 это поля templates и rawTemplates
            // SRG имена: f_210560_ (templates) и f_210559_ (rawTemplates)
            Field templatesField = ObfuscationReflectionHelper.findField(StructureTemplatePool.class, "f_210560_");
            Field rawTemplatesField = ObfuscationReflectionHelper.findField(StructureTemplatePool.class, "f_210559_");

            templatesField.setAccessible(true);
            rawTemplatesField.setAccessible(true);

            ObjectArrayList<StructurePoolElement> templates = (ObjectArrayList<StructurePoolElement>) templatesField.get(pool);
            List<Pair<StructurePoolElement, Integer>> rawTemplates = (List<Pair<StructurePoolElement, Integer>>) rawTemplatesField.get(pool);

            // rawTemplates часто бывает иммутабельным списком
            List<Pair<StructurePoolElement, Integer>> mutableRaw = new ArrayList<>(rawTemplates);
            mutableRaw.add(Pair.of(piece, weight));
            rawTemplatesField.set(pool, mutableRaw);

            // templates — это ObjectArrayList, куда элемент дублируется weight раз для взвешенного рандома
            for (int i = 0; i < weight; i++) {
                templates.add(piece);
            }
        } catch (Exception e) {
            UniversalCoins.LOGGER.error("Failed to inject village bank structure into {}", poolId, e);
        }
    }
}