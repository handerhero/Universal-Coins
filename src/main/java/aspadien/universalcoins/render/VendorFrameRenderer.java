package aspadien.universalcoins.render;

import aspadien.universalcoins.blockentities.VendorFrame;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class VendorFrameRenderer implements BlockEntityRenderer<VendorFrame> {

    public VendorFrameRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(VendorFrame te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = te.getItem(9);
        if (stack.isEmpty()) return;

        // HorizontalBlock -> HorizontalDirectionalBlock
        Direction facing = te.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);

        float angle = -facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));

        poseStack.translate(0.0D, 0.0D, 0.455D);
        poseStack.scale(0.5F, 0.5F, 0.5F);

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