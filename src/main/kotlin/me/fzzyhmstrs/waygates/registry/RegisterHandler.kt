package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.ai_odyssey.screen.WaygateScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType

object RegisterHandler {

    var WAYGATE_SCREEN_HANDLER: ScreenHandlerType<WaygateScreenHandler>? = null

    fun registerAll(){

        WAYGATE_SCREEN_HANDLER = ScreenHandlerType { syncID: Int, playerInventory: PlayerInventory ->
            WaygateScreenHandler(
                syncID,
                playerInventory
            )
        }
    }

}