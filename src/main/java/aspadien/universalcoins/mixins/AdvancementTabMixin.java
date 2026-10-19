package aspadien.universalcoins.mixins;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.advancements.AdvancementEntryGui;
import net.minecraft.client.gui.advancements.AdvancementTabGui;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(AdvancementTabGui.class)
public abstract class AdvancementTabMixin extends AbstractGui {

    // Оставляем только жизненно необходимые поля с указанием remap
    @Shadow private double scrollX;
    @Shadow private double scrollY;
    @Shadow @Final private DisplayInfo display;
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private AdvancementEntryGui root;

    private static final Map<ResourceLocation, int[]> TEXTURE_SIZE_CACHE = new ConcurrentHashMap<>();

    @Inject(method = "drawContents", at = @At("HEAD"), cancellable = true)
    private void drawDynamicBackground(MatrixStack matrixStack, CallbackInfo ci) {
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(matrixStack, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);

        ResourceLocation bgTexture = this.display.getBackground();
        if (bgTexture != null) {
            this.minecraft.getTextureManager().bind(bgTexture);
        } else {
            this.minecraft.getTextureManager().bind(new ResourceLocation("minecraft", "textures/gui/advancements/backgrounds/stone.png"));
        }

        int[] size = getTextureSize(bgTexture);
        int texW = size[0];
        int texH = size[1];

        int scrollIntX = MathHelper.floor(this.scrollX);
        int scrollIntY = MathHelper.floor(this.scrollY);

        int offsetX = Math.floorMod(scrollIntX, texW);
        int offsetY = Math.floorMod(scrollIntY, texH);

        int tilesNeededX = (234 / texW) + 2;
        int tilesNeededY = (113 / texH) + 2;

        for (int tileX = -1; tileX <= tilesNeededX; ++tileX) {
            for (int tileY = -1; tileY <= tilesNeededY; ++tileY) {
                blit(matrixStack,
                        offsetX + texW * tileX,
                        offsetY + texH * tileY,
                        0.0F, 0.0F,
                        texW, texH,
                        texW, texH
                );
            }
        }

        this.root.drawConnectivity(matrixStack, scrollIntX, scrollIntY, true);
        this.root.drawConnectivity(matrixStack, scrollIntX, scrollIntY, false);
        this.root.draw(matrixStack, scrollIntX, scrollIntY);

        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();

        ci.cancel();
    }

    private int[] getTextureSize(ResourceLocation texture) {
        if (texture == null) return new int[]{16, 16};

        return TEXTURE_SIZE_CACHE.computeIfAbsent(texture, loc -> {
            try (IResource resource = this.minecraft.getResourceManager().getResource(loc);
                 NativeImage image = NativeImage.read(resource.getInputStream())) {
                return new int[]{ image.getWidth(), image.getHeight() };
            } catch (Exception e) {
                return new int[]{16, 16};
            }
        });
    }
}