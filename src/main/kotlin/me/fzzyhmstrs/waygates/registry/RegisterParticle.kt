package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.Waygates
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterParticle {

    val WAYGATE_BLOCK = FabricParticleTypes.simple()

    fun registerParticleTypes(){
        Registry.register(Registry.PARTICLE_TYPE, Identifier(Waygates.MOD_ID,"waygate_block"), WAYGATE_BLOCK)
    }

    fun registerParticleFactories(){

    }
}
