package me.fzzyhmstrs.waygates.entity

import me.fzzyhmstrs.waygates.registry.RegisterEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import kotlin.math.max

class WaygateBlockEntity(pos: BlockPos, state: BlockState): BlockEntity(RegisterEntity.WAYGATE_BLOCK_ENTITY,pos, state) {

    private var playerPresent: Int = 0
    private var settings: WaygateHelper.WaygateSettings = WaygateHelper.WaygateSettings.getDefault()
    private var lit = false
    private var waygateEntities: List<WaygateEntity> = listOf()

    companion object{
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: WaygateBlockEntity) {
            val player = world.getClosestPlayer(pos.x + 0.5,pos.y + 0.5, pos.z + 0.5,2.5, false)
            if (player != null){
                blockEntity.playerPresent++
                if (blockEntity.playerPresent <= 20) {
                    if (world is ServerWorld)
                    emitParticles(world,pos, blockEntity.playerPresent)
                } else if (!blockEntity.lit) {
                    if (world is ServerWorld) blockEntity.lightWayGate(pos,world,blockEntity.settings)
                } else {
                    blockEntity.waygateEntities.forEach {
                        it.life = 20
                    }
                }
            } else {
                blockEntity.playerPresent = max(0,blockEntity.playerPresent - 1)
            }
        }

        private fun emitParticles(world:ServerWorld, pos: BlockPos, count: Int){
            if (count < 5) return
            val particleCount = MathHelper.lerpFromProgress(count.toDouble(),5.0,20.0,10.0,25.0).toInt()
                world.spawnParticles (ParticleTypes.POOF,pos.z + 0.5,pos.y + 1.0,pos.x + 0.5,particleCount,1.0,0.5,1.0,0.0)

        }
    }

    fun lightWayGate(pos: BlockPos, world: ServerWorld, settings: WaygateHelper.WaygateSettings){
        if (!WaygateHelper.hasGate(world, pos)){
            WaygateHelper.addGate(world, pos, settings)
        } else {
            WaygateHelper.createGateEntities(world, pos, this)
        }

    }
    
    fun updateSettings(newSettings: WaygateHelper.WaygateSettings, world: World){
        settings = newSettings
        WaygateHelper.updateSettings(world,this.pos,newSettings)
        markDirty()
    }

    fun getSettings(): WaygateHelper.WaygateSettings{
        return settings
    }

    fun setEntities(list: List<WaygateEntity>){
        waygateEntities = list
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        settings.toNbt(nbt)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        settings = WaygateHelper.WaygateSettings.fromNbt(nbt)
    }
}
