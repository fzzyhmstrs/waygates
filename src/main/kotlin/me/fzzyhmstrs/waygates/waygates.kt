package me.fzzyhmstrs.waygates

import me.fzzyhmstrs.ai_odyssey.screen.WaygateScreenHandler
import me.fzzyhmstrs.waygates.entity.WaygateBlockEntity
import me.fzzyhmstrs.waygates.registry.RegisterBlock
import me.fzzyhmstrs.waygates.registry.RegisterEntity
import me.fzzyhmstrs.waygates.registry.RegisterHandler
import me.fzzyhmstrs.waygates.registry.RegisterScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import kotlin.random.Random


object Waygates: ModInitializer {
    const val MOD_ID = "waygates"
    val waygatesRandom = Random(System.currentTimeMillis())

    override fun onInitialize() {
        RegisterBlock.registerAll()
        RegisterEntity.registerAll()
        RegisterHandler.registerAll()
        WaygateScreenHandler.registerServer()
    }
}

object WaygatesClient: ClientModInitializer{

    override fun onInitializeClient() {
        RegisterScreen.registerAll()
        WaygateBlockEntity.registerClient()
        WaygateScreenHandler.registerClient()
    }

}