package me.fzzyhmstrs.waygates.item

import me.fzzyhmstrs.waygates.entity.DimensionalAnchorBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class DimensionalAnchorItem(block: Block, settings: Settings): BlockItem(block, settings) {

    override fun onCraft(stack: ItemStack, world: World, player: PlayerEntity) {
        val nbt = stack.orCreateNbt
        val stateCompound = NbtCompound()
        val variant = world.random.nextInt(8)
        val partnerId = world.random.nextInt(Int.MAX_VALUE)
        stateCompound.putInt("age",variant)
        nbt.put("BlockStateTag", stateCompound)
        nbt.putInt("anchor_variant",variant)
        nbt.putInt("anchor_partner",partnerId)
    }

    override fun postPlacement(
        pos: BlockPos,
        world: World,
        player: PlayerEntity?,
        stack: ItemStack,
        state: BlockState
    ): Boolean {
        val entity = world.getBlockEntity(pos)?: return super.postPlacement(pos, world, player, stack, state)
        if (entity !is DimensionalAnchorBlockEntity) return super.postPlacement(pos, world, player, stack, state)
        val stackNbt = stack.nbt ?: return super.postPlacement(pos, world, player, stack, state)
        entity.readNbt(stackNbt)
        entity.markDirty()
        return true
    }

}