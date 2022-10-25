package me.fzzyhmstrs.waygates

import me.fzzyhmstrs.waygates.entity.WaygateBlockEntity
import me.fzzyhmstrs.waygates.entity.WaygateHelper
import me.fzzyhmstrs.waygates.registry.*
import me.fzzyhmstrs.waygates.screen.WaygateScreenHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import kotlin.random.Random


object Waygates: ModInitializer {
    const val MOD_ID = "waygates"
    val waygatesRandom = Random(System.currentTimeMillis())

    fun writeBlockPos(key: String, pos: BlockPos, nbt: NbtCompound){
        nbt.putLong(key,pos.asLong())
    }
    fun readBlockPos(key: String, nbt: NbtCompound): BlockPos {
        return if (nbt.contains(key)){
            BlockPos.fromLong(nbt.getLong(key))
        } else {
            BlockPos.ORIGIN
        }
    }

    override fun onInitialize() {
        RegisterBlock.registerAll()
        RegisterItem.registerAll()
        RegisterEntity.registerAll()
        RegisterHandler.registerAll()
        RegisterParticle.registerParticleTypes()
        WaygateScreenHandler.registerServer()
        WaygateHelper.registerServer()
    }
}

object WaygatesClient: ClientModInitializer{

    override fun onInitializeClient() {
        RegisterScreen.registerAll()
        RegisterRenderer.registerAll()
        RegisterParticle.registerParticleTex()
        WaygateScreenHandler.registerClient()
    }

}