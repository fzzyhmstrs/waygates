package me.fzzyhmstrs.ai_odyssey.model

import me.fzzyhmstrs.ai_odyssey.AIO
import me.fzzyhmstrs.ai_odyssey.entity.WaygateEntity
import me.fzzyhmstrs.ai_odyssey.registry.RegisterRenderer
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3f

class WaygateEntityRenderer(ctx: EntityRendererFactory.Context) : EntityRenderer<WaygateEntity>(ctx) {

    private val model: WaygateEntityModel

    init {
        model = WaygateEntityModel(ctx.getPart(RegisterRenderer.WAYGATE_SPRITE))
    }

    override fun render(
        waygateEntity: WaygateEntity,
        f: Float,
        g: Float,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        i: Int
    ) {
        matrixStack.push()
        matrixStack.multiply(
            Vec3f.POSITIVE_Y.getDegreesQuaternion(
                MathHelper.lerp(
                    g,
                    waygateEntity.prevYaw,
                    waygateEntity.yaw
                ) - 90.0f
            )
        )
        matrixStack.multiply(
            Vec3f.POSITIVE_Z.getDegreesQuaternion(
                MathHelper.lerp(
                    g,
                    waygateEntity.prevPitch,
                    waygateEntity.pitch
                ) + 90.0f
            )
        )
        val vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(getTexture(waygateEntity), true))
        model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        matrixStack.pop()
        super.render(waygateEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }


    override fun getTexture(entity: WaygateEntity): Identifier {
        return Identifier(AIO.MOD_ID, "textures/entity/waygate_sprite.png")
    }

    override fun hasLabel(entity: WaygateEntity): Boolean {
        return entity.shouldRenderName()
    }
}
