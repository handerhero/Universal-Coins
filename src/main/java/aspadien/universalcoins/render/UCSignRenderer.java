package aspadien.universalcoins.render;

import aspadien.universalcoins.blockentities.UCSign;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class UCSignRenderer implements BlockEntityRenderer<UCSign> {
    private final SignRenderer.SignModel model;
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("universalcoins", "textures/entity/uc_sign.png");

    public UCSignRenderer(BlockEntityRendererProvider.Context context) {
        // Модель теперь запекается через слой дубового знака из Context
        this.model = new SignRenderer.SignModel(context.bakeLayer(ModelLayers.createSignModelName(WoodType.OAK)));
    }

    @Override
    public void render(UCSign te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState state = te.getBlockState();
        poseStack.pushPose();

        if (state.getBlock() instanceof StandingSignBlock) {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float angle = -((state.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            this.model.stick.visible = true;
        } else {
            poseStack.translate(0.5D, 0.5D, 0.5D);
            float angle = -state.getValue(WallSignBlock.FACING).toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.translate(0.0D, -0.3125D, -0.4375D);
            this.model.stick.visible = false;
        }

        // Рендер модели знака
        poseStack.pushPose();
        poseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.root.render(poseStack, vertexConsumer, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        // Рендер текста
        Font font = Minecraft.getInstance().font;
        poseStack.translate(0.0D, 0.33333334D, 0.04666667D);
        poseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);

        for (int i = 0; i < 4; i++) {
            // Если в вашем TileUCSign метод называется getMessage(i), оставляем его
            String s = te.getMessage(i).getString();
            float x = (-font.width(s) / 2.0F);
            font.drawInBatch(s, x, (i * 10 - 20), 0, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
        }

        poseStack.popPose();
    }
}