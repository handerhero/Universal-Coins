package aspadien.universalcoins.mixins;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(AdvancementTab.class)
public abstract class AdvancementTabMixin {

    @Shadow private double scrollX;
    @Shadow private double scrollY;
    @Shadow private int minX;
    @Shadow private int maxX;
    @Shadow private int minY;
    @Shadow private int maxY;
    @Shadow private boolean centered;
    @Shadow @Final private DisplayInfo display;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private AdvancementWidget root;

    private static final Map<ResourceLocation, int[]> TEXTURE_SIZE_CACHE = new ConcurrentHashMap<>();
    private static final ResourceLocation DEFAULT_BACKGROUND = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png");

    @Inject(method = "drawContents", at = @At("HEAD"), cancellable = true)
    private void drawDynamicBackground(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        // Логика центрирования вкладки при первом открытии (из ванильного кода 1.20.1)
        if (!this.centered) {
            this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
            this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
            this.centered = true;
        }

        // В 1.20.1 для обрезки границ экрана используется аппаратный scissor вместо depth mask
        guiGraphics.enableScissor(x, y, x + 234, y + 113);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float)x, (float)y, 0.0F);

        ResourceLocation bgTexture = this.display.getBackground();
        if (bgTexture == null) {
            bgTexture = DEFAULT_BACKGROUND;
        }

        int[] size = getTextureSize(bgTexture);
        int texW = size[0];
        int texH = size[1];

        int scrollIntX = Mth.floor(this.scrollX);
        int scrollIntY = Mth.floor(this.scrollY);

        int offsetX = Math.floorMod(scrollIntX, texW);
        int offsetY = Math.floorMod(scrollIntY, texH);

        int tilesNeededX = (234 / texW) + 2;
        int tilesNeededY = (113 / texH) + 2;

        for (int tileX = -1; tileX <= tilesNeededX; ++tileX) {
            for (int tileY = -1; tileY <= tilesNeededY; ++tileY) {
                guiGraphics.blit(
                        bgTexture,
                        offsetX + texW * tileX,
                        offsetY + texH * tileY,
                        0.0F, 0.0F,
                        texW, texH,
                        texW, texH
                );
            }
        }

        // Отрисовка линий и самих иконок достижений
        this.root.drawConnectivity(guiGraphics, scrollIntX, scrollIntY, true);
        this.root.drawConnectivity(guiGraphics, scrollIntX, scrollIntY, false);
        this.root.draw(guiGraphics, scrollIntX, scrollIntY);

        guiGraphics.pose().popPose();
        guiGraphics.disableScissor();

        ci.cancel();
    }

    private int[] getTextureSize(ResourceLocation texture) {
        if (texture == null) return new int[]{16, 16};

        return TEXTURE_SIZE_CACHE.computeIfAbsent(texture, loc -> {
            try {
                Optional<Resource> resourceOpt = this.minecraft.getResourceManager().getResource(loc);
                if (resourceOpt.isPresent()) {
                    try (InputStream is = resourceOpt.get().open();
                         NativeImage image = NativeImage.read(is)) {
                        return new int[]{ image.getWidth(), image.getHeight() };
                    }
                }
            } catch (Exception ignored) {
            }
            return new int[]{16, 16};
        });
    }
}