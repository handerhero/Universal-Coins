/*    */ package aspadien.universalcoins.render;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.renderer.IRenderTypeBuffer;
/*    */ import net.minecraft.client.renderer.model.ItemCameraTransforms;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
/*    */ import net.minecraft.item.ItemStack;
/*    */
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ import aspadien.universalcoins.tileentity.TileVendor;
/*    */ 
/*    */ public class VendorBlockRenderer extends TileEntityRenderer<TileVendor> {
/*    */   public VendorBlockRenderer(TileEntityRendererDispatcher dispatcher) {
/* 16 */     super(dispatcher);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(TileVendor te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
/* 21 */     ItemStack stack = te.getItem(9);
/* 22 */     if (stack.isEmpty())
/*    */       return; 
/* 24 */     matrixStack.pushPose();
/*    */     
/* 26 */     matrixStack.translate(0.5D, 0.55D, 0.5D);
/* 27 */     matrixStack.scale(0.75F, 0.75F, 0.75F);
/*    */ 
/*    */     
/* 30 */     float angle = ((float)te.getLevel().getGameTime() + partialTicks) * 3.0F;
/* 31 */     matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
/*    */     
/* 33 */     Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
/*    */ 
/*    */     
/* 36 */     matrixStack.popPose();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/render/VendorBlockRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */