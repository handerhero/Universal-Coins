package aspadien.universalcoins.render;

import aspadien.universalcoins.blockentities.Vendor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class VendorBlockRenderer implements BlockEntityRenderer<Vendor> {

    public VendorBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(Vendor te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.getItem(9);
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.55D, 0.5D);
        poseStack.scale(0.75F, 0.75F, 0.75F);

        float angle = ((float) (te.getLevel() != null ? te.getLevel().getGameTime() : 0) + partialTicks) * 3.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        // В 1.20.1 ItemCameraTransforms.TransformType -> ItemDisplayContext, и добавлены level и seed
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                combinedLight,
                combinedOverlay,
                poseStack,
                buffer,
                te.getLevel(),
                0
        );

        poseStack.popPose();
    }
}