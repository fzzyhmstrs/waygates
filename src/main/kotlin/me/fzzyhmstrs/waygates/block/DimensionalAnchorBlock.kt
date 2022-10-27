package me.fzzyhmstrs.waygates.block

import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos

class DimensionalAnchorBlock(settings: Settings): BlockWithEntity(settings) {
    override fun createBlockEntity(pos: BlockPos?, state: BlockState?): BlockEntity? {
        TODO("Not yet implemented")
    }
}