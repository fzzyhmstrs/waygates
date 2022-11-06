package me.fzzyhmstrs.waygates.entity

import me.fzzyhmstrs.waygates.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

class DimensionalAnchorBlockEntity(pos:BlockPos, state: BlockState): BlockEntity(RegisterEntity.DIMENSIONAL_ANCHOR_BLOCK_ENTITY,pos,state) {

    var variant: Int = 0
    var partnerId: Int = 0
    var partnerDim: String = ""
    var partnerPos: BlockPos = BlockPos.ORIGIN

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        if (nbt.contains("anchor_variant")){
            variant = nbt.getInt("anchor_variant")
        }
        if (nbt.contains("anchor_partner")){
            partnerId = nbt.getInt("anchor_partner")
        }
        if (nbt.contains("anchor_dim")){
            partnerDim = nbt.getString("partner_dim")
        }
        if (nbt.contains("anchor_pos")){
            partnerPos = BlockPos.fromLong(nbt.getLong("anchor_pos"))
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putInt("anchor_variant", variant)
        nbt.putInt("anchor_partner",partnerId)
        nbt.putString("anchor_dim",partnerDim)
        nbt.putLong("anchor_pos",partnerPos.asLong())
    }

    fun setPartner(pos: BlockPos, dim: String){
        partnerDim = dim
        partnerPos = pos
        markDirty()
    }

    fun writeToStack(stack: ItemStack){
        val nbt = stack.orCreateNbt
        val stateCompound = NbtCompound()
        stateCompound.putInt("age",variant)
        nbt.put("BlockStateTag", stateCompound)
        nbt.putInt("anchor_variant",variant)
        nbt.putInt("anchor_partner",partnerId)
        nbt.putString("anchor_dim",partnerDim)
        nbt.putLong("anchor_pos",partnerPos.asLong())
    }

}