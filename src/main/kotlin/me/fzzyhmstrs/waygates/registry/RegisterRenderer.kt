package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.MOD_ID
import me.fzzyhmstrs.waygates.Waygates
import me.fzzyhmstrs.waygates.model.WaygateEntityModel
import me.fzzyhmstrs.waygates.model.WaygateEntityRenderer
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.Identifier

object RegisterRenderer {

    val WAYGATE_SPRITE: EntityModelLayer = EntityModelLayer(Identifier(MOD_ID,"waygate_sprite"),"waygate_sprite_layer")

    fun registerAll(){

        EntityRendererRegistry.register(
            RegisterEntity.WAYGATE_ENTITY
        ){context: EntityRendererFactory.Context ->
            WaygateEntityRenderer(
                context
            )
        }

        EntityModelLayerRegistry.registerModelLayer(WAYGATE_SPRITE, WaygateEntityModel::getTexturedModelData)
    }

}