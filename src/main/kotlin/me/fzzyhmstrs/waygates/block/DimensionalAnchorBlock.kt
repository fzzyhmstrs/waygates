package me.fzzyhmstrs.waygates.block

import me.fzzyhmstrs.waygates.entity.DimensionalAnchorBlockEntity
import me.fzzyhmstrs.waygates.registry.RegisterBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.GameRules
import net.minecraft.world.World

class DimensionalAnchorBlock(settings: Settings): BlockWithEntity(settings) {

    companion object{
        private val VARIANT = Properties.AGE_7
        private val partnerMap: MutableMap<Int, Partner> = mutableMapOf()

        private fun getOrCreatePartner(anchor: DimensionalAnchorBlockEntity, world: World): Partner{
            val id = anchor.partnerId
            if (!partnerMap.containsKey(id)){ //no existing partner, this is the first of two BEs to be placed
                return partnerMap.put(id,Partner(anchor.pos,world.registryKey.value.toString(),false))?: Partner.EMPTY
            } else { //there is a partner pre-existing in the world
                val existingPartner = partnerMap[id] ?: throw IllegalStateException("Couldn't find partner with id: $id")
                val dim = existingPartner.dim
                if (dim == world.registryKey.value.toString()){ //partner is in the same dimension as the BE (why?)
                    val partner = world.getBlockEntity(existingPartner.pos)
                    if (partner != null && partner is DimensionalAnchorBlockEntity){
                        partner.setPartner(anchor.pos,dim) //sets the partner's data to the BEs location
                        return existingPartner.withFound() //return partners data with found flag
                    } else {
                        throw IllegalStateException("Couldn't find block entity at partner position: ${existingPartner.pos}")
                    }
                } else if (world is ServerWorld) { //otherwise we need to use the BE world to find the partners dimension world
                    val registryKey = RegistryKey.of(Registry.WORLD_KEY, Identifier(dim))
                    val partnerWorld = world.server.getWorld(registryKey)?: throw IllegalArgumentException("Couldn't find the world for dim: $dim")
                    val partnerEntity = partnerWorld.getBlockEntity(existingPartner.pos)
                    if (partnerEntity != null && partnerEntity is DimensionalAnchorBlockEntity){
                        partnerEntity.setPartner(anchor.pos,world.registryKey.value.toString()) //sets the partner's data to the BEs location
                        return existingPartner.withFound() //return partners data with found flag
                    } else {
                        throw IllegalStateException("Couldn't find block entity at partner position: ${existingPartner.pos}")
                    }
                } else {
                    throw IllegalStateException("Somehow passed a client world to this server only method")
                }
            }
        }

        private class Partner(val pos: BlockPos, val dim: String, var found: Boolean){
            companion object{
                val EMPTY = Partner(BlockPos.ORIGIN,World.OVERWORLD.value.toString(),false)
            }
            fun withFound(): Partner{
                found = true
                return this
            }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(VARIANT)
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        itemStack: ItemStack
    ) {
        super.onPlaced(world, pos, state, placer, itemStack)
        if (world.isClient) return
        val be = world.getBlockEntity(pos)?:return
        if (be is DimensionalAnchorBlockEntity){
            val partner = getOrCreatePartner(be, world)
            if (partner.found){
                be.setPartner(partner.pos,partner.dim)
            }
        }
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        val entity = world.getBlockEntity(pos)?:return super.onBreak(world, pos, state, player)
        if (!world.isClient && player.isCreative && world.gameRules.getBoolean(GameRules.DO_TILE_DROPS) && entity is DimensionalAnchorBlockEntity){
            val itemStack = ItemStack(this)
            entity.writeToStack(itemStack)
            val itemEntity = ItemEntity(
                world,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                itemStack
            )
            itemEntity.setToDefaultPickupDelay()
            world.spawnEntity(itemEntity)
        }
        super.onBreak(world, pos, state, player)
    }

    @Deprecated("Deprecated in Java")
    override fun getDroppedStacks(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val stacks = super.getDroppedStacks(state, builder)
        val list: MutableList<ItemStack> = mutableListOf()
        val entity = builder.getNullable(LootContextParameters.BLOCK_ENTITY)?:return stacks
        if (entity !is DimensionalAnchorBlockEntity) return stacks
        stacks.forEach {
            if (it.isOf(RegisterBlock.DIMENSIONAL_ANCHOR.asItem())){
                entity.writeToStack(it)
                list.add(it)
            }
        }
        if (list.isEmpty())return stacks
        return list
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return DimensionalAnchorBlockEntity(pos, state)
    }
}