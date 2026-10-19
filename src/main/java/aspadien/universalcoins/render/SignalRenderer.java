/*    */ package aspadien.universalcoins.render;
/*    */ 
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.gui.FontRenderer;
/*    */ import net.minecraft.client.renderer.IRenderTypeBuffer;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
/*    */ import net.minecraft.state.Property;
/*    */
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.math.vector.Matrix4f;
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ import aspadien.universalcoins.block.BlockSignal;
/*    */ import aspadien.universalcoins.tileentity.TileSignal;
/*    */ 
/*    */ public class SignalRenderer extends TileEntityRenderer<TileSignal> {
/*    */   public SignalRenderer(TileEntityRendererDispatcher dispatcher) {
/* 19 */     super(dispatcher);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(TileSignal te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
/* 24 */     Direction facing = (Direction)te.getBlockState().getValue((Property)BlockSignal.FACING);
/* 25 */     FontRenderer font = (Minecraft.getInstance()).font;
/*    */     
/* 27 */     matrixStack.pushPose();
/* 28 */     matrixStack.translate(0.5D, 0.5D, 0.5D);
/* 29 */     matrixStack.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
/* 30 */     matrixStack.translate(0.0D, 0.1D, -0.501D);
/* 31 */     matrixStack.scale(0.015F, -0.015F, 0.015F);
/*    */     
/* 33 */     Matrix4f matrix4f = matrixStack.last().pose();
/*    */     
/* 35 */     if (te.secondsLeft > 0) {
/* 36 */       String s = te.secondsLeft + " s";
/* 37 */       font.drawInBatch(s, (-font.width(s) / 2), 0.0F, 16777215, false, matrix4f, buffer, false, 0, combinedLight);
/*    */     } else {
/* 39 */       String s1 = te.fee + " C";
/* 40 */       String s2 = te.duration + " s";
/* 41 */       font.drawInBatch(s1, (-font.width(s1) / 2), -5.0F, 16777215, false, matrix4f, buffer, false, 0, combinedLight);
/* 42 */       font.drawInBatch(s2, (-font.width(s2) / 2), 5.0F, 16777215, false, matrix4f, buffer, false, 0, combinedLight);
/*    */     } 
/*    */     
/* 45 */     matrixStack.popPose();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/render/SignalRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */