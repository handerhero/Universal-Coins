/*    */ package aspadien.universalcoins.render;
/*    */ import com.mojang.blaze3d.matrix.MatrixStack;
/*    */ import com.mojang.blaze3d.vertex.IVertexBuilder;
/*    */ import net.minecraft.block.BlockState;
/*    */ import net.minecraft.block.StandingSignBlock;
/*    */ import net.minecraft.block.WallSignBlock;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.gui.FontRenderer;
/*    */ import net.minecraft.client.renderer.IRenderTypeBuffer;
/*    */ import net.minecraft.client.renderer.RenderType;
/*    */ import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
/*    */ import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
/*    */ import net.minecraft.state.Property;
/*    */
/*    */ import net.minecraft.util.Direction;
/*    */ import net.minecraft.util.ResourceLocation;
/*    */ import net.minecraft.util.math.vector.Vector3f;
/*    */ import aspadien.universalcoins.tileentity.TileUCSign;
/*    */ 
/*    */ public class UCSignRenderer extends TileEntityRenderer<TileUCSign> {
/* 21 */   private final SignTileEntityRenderer.SignModel model = new SignTileEntityRenderer.SignModel();
/* 22 */   private static final ResourceLocation TEXTURE = new ResourceLocation("aspadien.universalcoins:textures/entity/uc_sign.png");
/*    */   
/*    */   public UCSignRenderer(TileEntityRendererDispatcher dispatcher) {
/* 25 */     super(dispatcher);
/*    */   }
/*    */ 
/*    */   
/*    */   public void render(TileUCSign te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
/* 30 */     BlockState state = te.getBlockState();
/* 31 */     matrixStack.pushPose();
/*    */     
/* 33 */     if (state.getBlock() instanceof StandingSignBlock) {
/* 34 */       matrixStack.translate(0.5D, 0.5D, 0.5D);
/* 35 */       float angle = -((((Integer)state.getValue((Property)StandingSignBlock.ROTATION)).intValue() * 360) / 16.0F);
/* 36 */       matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
/* 37 */       this.model.stick.visible = true;
/*    */     } else {
/* 39 */       matrixStack.translate(0.5D, 0.5D, 0.5D);
/* 40 */       float angle = -((Direction)state.getValue((Property)WallSignBlock.FACING)).toYRot();
/* 41 */       matrixStack.mulPose(Vector3f.YP.rotationDegrees(angle));
/* 42 */       matrixStack.translate(0.0D, -0.3125D, -0.4375D);
/* 43 */       this.model.stick.visible = false;
/*    */     } 
/*    */ 
/*    */     
/* 47 */     matrixStack.pushPose();
/* 48 */     matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
/* 49 */     IVertexBuilder ivertexbuilder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
/* 50 */     this.model.sign.render(matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
/* 51 */     this.model.stick.render(matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
/* 52 */     matrixStack.popPose();
/*    */ 
/*    */     
/* 55 */     FontRenderer font = (Minecraft.getInstance()).font;
/* 56 */     matrixStack.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
/* 57 */     matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
/*    */     
/* 59 */     for (int i = 0; i < 4; i++) {
/* 60 */       String s = te.getMessage(i).getString();
/* 61 */       float x = (-font.width(s) / 2);
/* 62 */       font.drawInBatch(s, x, (i * 10 - 20), 0, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
/*    */     } 
/*    */     
/* 65 */     matrixStack.popPose();
/*    */   }
/*    */ }


/* Location:              /home/aspadien/.gradle/caches/forge_gradle/deobf_dependencies/blank/examplemod/1.0.0_mapped_official_1.16.5/examplemod-1.0.0_mapped_official_1.16.5.jar!/aspadien.universalcoins/render/UCSignRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */