package me.fzzyhmstrs.waygates.block

import me.fzzyhmstrs.ai_odyssey.screen.WaygateScreenHandler
import me.fzzyhmstrs.waygates.entity.WaygateBlockEntity
import me.fzzyhmstrs.waygates.registry.RegisterEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WaygateBlock(settings: Settings): BlockWithEntity(settings) {

    @Deprecated("Deprecated in Java")
    override fun getRenderType(state: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
    }
    
    @Deprecated("Deprecated in Java")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (world.isClient || hand == Hand.OFF_HAND) {
            return ActionResult.SUCCESS
        }
        val entity = world.getBlockEntity(pos)
        return if (entity != null && entity is WaygateBlockEntity){
            setup(player, world, pos, entity)
            ActionResult.CONSUME
        } else {
            ActionResult.FAIL
        }
    }

    private fun setup(user: PlayerEntity, world: World, pos: BlockPos, entity: WaygateBlockEntity){
        user.openHandledScreen(
            SimpleNamedScreenHandlerFactory(
                { syncId: Int, inventory: PlayerInventory, _: PlayerEntity ->
                    WaygateScreenHandler(
                        syncId,
                        inventory,
                        ScreenHandlerContext.create(world, pos),
                        entity
                    )
                }, TranslatableText("screen.waygate.title")
            )
        )
    }
    
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return WaygateBlockEntity(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (world.isClient){
            null
        } else{
            checkType(type, RegisterEntity.WAYGATE_BLOCK_ENTITY)
            { world2: World, pos: BlockPos, state2: BlockState, blockEntity: WaygateBlockEntity ->
                WaygateBlockEntity.tick(world2, pos, state2, blockEntity)
            }
        }
    }
}
