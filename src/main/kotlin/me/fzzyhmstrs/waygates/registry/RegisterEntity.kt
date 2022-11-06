package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.MOD_ID
import me.fzzyhmstrs.waygates.entity.DimensionalAnchorBlockEntity
import me.fzzyhmstrs.waygates.entity.WaygateBlockEntity
import me.fzzyhmstrs.waygates.entity.WaygateEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object RegisterEntity {

    val WAYGATE_BLOCK_ENTITY: BlockEntityType<WaygateBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        "$MOD_ID:waygate_block_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            WaygateBlockEntity(
                pos,
                state
            )
        }, RegisterBlock.WAYGATE).build(null))

    val DIMENSIONAL_ANCHOR_BLOCK_ENTITY: BlockEntityType<DimensionalAnchorBlockEntity> = Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        "$MOD_ID:dimensional_anchor_block_entity",
        FabricBlockEntityTypeBuilder.create({ pos: BlockPos, state: BlockState ->
            DimensionalAnchorBlockEntity(
                pos,
                state
            )
        }, RegisterBlock.DIMENSIONAL_ANCHOR).build(null))

    val WAYGATE_ENTITY: EntityType<WaygateEntity> = Registry.register(
        Registry.ENTITY_TYPE,
        Identifier(MOD_ID, "waygate_entity"),
        FabricEntityTypeBuilder.create(
            SpawnGroup.MISC
        ) { entityType: EntityType<WaygateEntity>, world: World ->
            WaygateEntity(
                entityType,
                world
            )
        }.dimensions(EntityDimensions.fixed(0.4f, 0.4f)).build()
    )

    fun registerAll(){}

    fun <T: BlockEntity> getBlockEntity(world: World, pos: BlockPos, entityType: BlockEntityType<T>): T?{
        val chk = world.getBlockEntity(pos, entityType)
        return if (chk.isPresent){
            chk.get()
        } else {
            null
        }
    }

}