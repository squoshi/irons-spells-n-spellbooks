package io.redspace.ironsspellbooks.entity.spells.void_tentacle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@OnlyIn(Dist.CLIENT)
public class VoidTentacleEmissiveLayer extends GeoRenderLayer<VoidTentacle> {
    public static final ResourceLocation TEXTURE = IronsSpellbooks.id("textures/entity/void_tentacle/void_tentacle_emissive.png");

    public VoidTentacleEmissiveLayer(GeoEntityRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(PoseStack poseStack, VoidTentacle animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        renderType = RenderType.eyes(TEXTURE);
        //renderType = RenderType.endGateway();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(renderType);
        poseStack.pushPose();
        float f = Mth.sin((float) ((animatable.tickCount + partialTick + ((animatable.getX() + animatable.getZ()) * 500)) * .15f)) * .5f + .5f;
        //IronsSpellbooks.LOGGER.debug("{}", f);
        this.getRenderer().actuallyRender(poseStack, animatable, bakedModel, renderType, bufferSource, vertexconsumer, true, partialTick,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, Utils.packRGB(new Vector3f(f,f,f)));
        poseStack.popPose();

    }
}