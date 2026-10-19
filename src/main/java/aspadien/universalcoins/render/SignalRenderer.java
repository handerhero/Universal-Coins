package aspadien.universalcoins.render;

import aspadien.universalcoins.block.BlockSignal;
import aspadien.universalcoins.blockentities.Signal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

public class SignalRenderer implements BlockEntityRenderer<Signal> {

    public SignalRenderer(BlockEntityRendererProvider.Context context) {
        // BlockEntityRenderer теперь интерфейс, super() не требуется
    }

    @Override
    public void render(Signal te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Direction facing = te.getBlockState().getValue(BlockSignal.FACING);
        Font font = Minecraft.getInstance().font;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(0.0D, 0.1D, -0.501D);
        poseStack.scale(0.015F, -0.015F, 0.015F);

        Matrix4f matrix4f = poseStack.last().pose();

        if (te.secondsLeft > 0) {
            String s = te.secondsLeft + " s";
            font.drawInBatch(s, (-font.width(s) / 2.0F), 0.0F, 0xFFFFFF, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
        } else {
            String s1 = te.fee + " C";
            String s2 = te.duration + " s";
            font.drawInBatch(s1, (-font.width(s1) / 2.0F), -5.0F, 0xFFFFFF, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
            font.drawInBatch(s2, (-font.width(s2) / 2.0F), 5.0F, 0xFFFFFF, false, matrix4f, buffer, Font.DisplayMode.NORMAL, 0, combinedLight);
        }

        poseStack.popPose();
    }
}