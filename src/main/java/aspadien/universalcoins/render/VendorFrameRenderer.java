/*    */ package aspadien.universalcoins.render;
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import net.minecraft.block.HorizontalBlock;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.renderer.IRenderTypeBuffer;
/*    */ import net.minecraft.client.renderer.model.ItemCameraTransforms;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.state.Property;
/*    */
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ import aspadien.universalcoins.tileentity.TileVendorFrame;
/*    */ 
/*    */ public class VendorFrameRenderer extends TileEntityRenderer<TileVendorFrame> {
/*    */   public VendorFrameRenderer(TileEntityRendererDispatcher dispatcher) {
/* 18 */     super(dispatcher);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(TileVendorFrame te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
/* 23 */     ItemStack stack = te.getItem(9);
/* 24 */     if (stack.isEmpty()) {
/*    */       return;
/*    */     }
/* 27 */     Direction facing = (Direction)te.getBlockState().getValue((Property)HorizontalBlock.FACING);
/*    */     
/* 29 */     matrixStack.pushPose();
/*    */ 
/*    */     
/* 32 */     matrixStack.translate(0.5D, 0.5D, 0.5D);
/*    */ 
/*    */     
/* 35 */     float angle = -facing.toYRot();
/* 36 */     matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
/*    */     
/* 38 */     matrixStack.translate(0.0D, 0.0D, 0.455D);
/*    */ 
/*    */     
/* 41 */     matrixStack.scale(0.5F, 0.5F, 0.5F);
/*    */ 
/*    */     
/* 44 */     Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
/*    */ 
/*    */ 
/*    */     
/* 48 */     matrixStack.popPose();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/render/VendorFrameRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */