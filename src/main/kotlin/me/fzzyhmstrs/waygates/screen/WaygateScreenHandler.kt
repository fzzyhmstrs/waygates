package me.fzzyhmstrs.waygates.screen

import me.fzzyhmstrs.waygates.entity.WaygateHelper
import me.fzzyhmstrs.waygates.Waygates
import me.fzzyhmstrs.waygates.entity.WaygateBlockEntity
import me.fzzyhmstrs.waygates.registry.RegisterBlock
import me.fzzyhmstrs.waygates.registry.RegisterHandler
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.Property
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import java.awt.Color

class WaygateScreenHandler(
    syncID: Int,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext,
    private val entity: WaygateBlockEntity
): ScreenHandler(RegisterHandler.WAYGATE_SCREEN_HANDLER, syncID) {

    constructor(syncID: Int, playerInventory: PlayerInventory): this(syncID,
        playerInventory,
        ScreenHandlerContext.EMPTY,
        WaygateBlockEntity(BlockPos.ORIGIN, RegisterBlock.WAYGATE.defaultState)
    )

    val red = Property.create()
    val green = Property.create()
    val blue = Property.create()
    val rainbow = Property.create()
    var name = entity.getSettings().customName

    init{
        val settings = entity.getSettings()
        val color = Color(settings.color)
        addProperty(red).set(color.red)
        addProperty(green).set(color.green)
        addProperty(blue).set(color.blue)
        val rb = if(settings.priority){ 1 } else { 0 }
        addProperty(rainbow).set(rb)
        val player = playerInventory.player
        if (player is ServerPlayerEntity) {
            sendNameS2CPacket(player,name,syncID)
        }
    }

    ////////////////////////////////////

    override fun canUse(player: PlayerEntity): Boolean {
        return true
    }

    companion object{

        val WAYGATE_INFO_C2S = Identifier(Waygates.MOD_ID,"info_c2s")
        val WAYGATE_NAME_S2C = Identifier(Waygates.MOD_ID,"name_s2c")

        fun sendInfoC2SPacket(settings: WaygateHelper.WaygateSettings, syncID: Int){
            val buf = PacketByteBufs.create()
            buf.writeInt(syncID)
            buf.writeString(settings.customName)
            buf.writeInt(settings.color)
            buf.writeBoolean(settings.priority)
            ClientPlayNetworking.send(WAYGATE_INFO_C2S, buf)
        }

        fun sendNameS2CPacket(player: ServerPlayerEntity, newName: String, syncID: Int){
            val buf = PacketByteBufs.create()
            buf.writeInt(syncID)
            buf.writeString(newName)
            ServerPlayNetworking.send(player, WAYGATE_NAME_S2C, buf)

        }

        fun registerClient(){
            ClientPlayNetworking.registerGlobalReceiver(WAYGATE_NAME_S2C) { client, _, buf, _ ->
                val player = client.player?:return@registerGlobalReceiver
                val syncId = buf.readInt()
                val handler = player.currentScreenHandler
                if (handler.syncId != syncId) return@registerGlobalReceiver
                if (handler !is WaygateScreenHandler) return@registerGlobalReceiver
                handler.name = buf.readString()
            }
        }

        fun registerServer(){
            ServerPlayNetworking.registerGlobalReceiver(WAYGATE_INFO_C2S) {_,player,_,buf,_ ->
                val handler = player.currentScreenHandler
                val syncId = buf.readInt()
                if (handler.syncId != syncId) return@registerGlobalReceiver
                if (handler is WaygateScreenHandler){

                    val customName = buf.readString()
                    val color = buf.readInt()
                    val priority = buf.readBoolean()
                    val clr = Color(color)

                    handler.name = customName
                    handler.red.set(clr.red)
                    handler.green.set(clr.green)
                    handler.blue.set(clr.blue)
                    val rb = if(priority){ 1 } else { 0 }
                    handler.rainbow.set(rb)

                    handler.entity.updateSettings(WaygateHelper.WaygateSettings(handler.name, color, priority), player.world)
                }

            }
        }

    }

}
