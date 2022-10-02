package me.fzzyhmstrs.waygates.model

import com.google.common.collect.ImmutableList
import me.fzzyhmstrs.waygates.entity.WaygateEntity
import net.minecraft.client.model.*
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.util.math.MatrixStack


class WaygateEntityModel(private val modelPart: ModelPart) : EntityModel<WaygateEntity>() {

    private val base: ModelPart = modelPart.getChild(EntityModelPartNames.CUBE)

    override fun render(
        matrices: MatrixStack,
        vertices: VertexConsumer,
        light: Int,
        overlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        base.render(
                matrices,
                vertices,
                light,
                overlay,
                red,
                green,
                blue,
                alpha
            )
    }

    override fun setAngles(
        entity: WaygateEntity,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        return
    }

    companion object {
        fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild(
                EntityModelPartNames.CUBE,
                ModelPartBuilder.create()
                    .uv(0, 0)
                    .cuboid(-5f, 10f, -5f, 10f, 10f, 10f),
                ModelTransform.pivot(0f, 0f, 0f)
            )
            return TexturedModelData.of(modelData,64,32)
        }
    }
}
