package me.fzzyhmstrs.waygates.entity

import me.fzzyhmstrs.ai_odyssey.entity.WaygateEntity
import me.fzzyhmstrs.ai_odyssey.entity.WaygateHelper
import me.fzzyhmstrs.waygates.Waygates
import me.fzzyhmstrs.waygates.registry.RegisterEntity
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
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
        private val PARTICLE_PACKET = Identifier(Waygates.MOD_ID,"waygate_particles")

        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: WaygateBlockEntity) {
            val player = world.getClosestPlayer(pos.x + 0.5,pos.y + 0.5, pos.z + 0.5,2.0, false)
            if (player != null){
                blockEntity.playerPresent++
                if (blockEntity.playerPresent <= 20) {
                    sendParticlePacket(pos, blockEntity.playerPresent)
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


        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(PARTICLE_PACKET) {client,_,buf,_ ->
                val world = client.world?: return@registerGlobalReceiver
                val pos = buf.readBlockPos()
                val count = buf.readInt()
                emitParticles(world,pos, count)
            }
            ServerWorldEvents.LOAD.register {_,world -> world.persistentStateManager}
        }


        private fun sendParticlePacket(pos: BlockPos, count: Int) {
            val buf = PacketByteBufs.create()
            buf.writeBlockPos(pos)
            buf.writeInt(count)
        }

        private fun emitParticles(world:World, pos: BlockPos, count: Int){
            if (count < 5) return
            val particleCount = MathHelper.lerpFromProgress(count.toDouble(),5.0,20.0,10.0,25.0).toInt()
            for (i in 0..particleCount){
                val rndX = pos.x + world.random.nextDouble()
                val rndZ = pos.z + world.random.nextDouble()
                world.addParticle(ParticleTypes.POOF,rndX,pos.y + 1.0,rndZ,0.0,0.0,0.0)
            }
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
