package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.screen.WaygateScreen
import me.fzzyhmstrs.waygates.screen.WaygateScreenHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text


object RegisterScreen {

    fun registerAll(){
        HandledScreens.register(RegisterHandler.WAYGATE_SCREEN_HANDLER){
            handler: WaygateScreenHandler, playerInventory: PlayerInventory, title: Text ->
            WaygateScreen(handler, playerInventory, title)
        }
    }

}