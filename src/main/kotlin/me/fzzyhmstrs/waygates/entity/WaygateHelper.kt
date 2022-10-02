package me.fzzyhmstrs.waygates.entity

import me.fzzyhmstrs.waygates.entity.WaygateEntity
import me.fzzyhmstrs.waygates.Waygates
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.awt.Color

object WaygateHelper {

    private val waygates: MutableMap<Identifier,WaygatePersistentState> = mutableMapOf()

    fun registerServer(){
        ServerWorldEvents.LOAD.register{_, world ->
            val worldIdentifier = world.registryKey.value;
            world.persistentStateManager.getOrCreate({nbt -> stateFromNbt(nbt)},{ createState(worldIdentifier)},"waygates")
        }
    }

    private fun createState(worldIdentifier: Identifier): WaygatePersistentState{
        val state = WaygatePersistentState()
        waygates[worldIdentifier] = state
        return state
    }

    private fun stateFromNbt(nbt: NbtCompound): WaygatePersistentState{
        val worldIdentifier = Identifier(nbt.getString("world_id"))
        return createState(worldIdentifier).fromNbt(nbt)
    }
    
    private fun getOrInitializeState(world: ServerWorld): WaygatePersistentState{
        val worldIdentifier = world.registryKey.value
        return if (!waygates.containsKey(worldIdentifier)){
            val function = { nbt: NbtCompound -> stateFromNbt(nbt) }
            val function2 = { createState(worldIdentifier) }
            world.persistentStateManager.getOrCreate(function,function2,"waygates")
        } else {
            waygates[worldIdentifier]?: WaygatePersistentState()
        }
    }

    ///////////////////////////////////////

    fun hasGate(world: World, pos: BlockPos): Boolean{
        val worldIdentifier = world.registryKey.value
        return waygates[worldIdentifier]?.gates?.containsKey(pos) == true
    }

    fun getGate(world: World, pos: BlockPos): Pair<BlockPos, WaygateSettings>?{
        val worldIdentifier = world.registryKey.value
        return if (waygates[worldIdentifier]?.gates?.containsKey(pos) == true){
            val settings = waygates[worldIdentifier]?.gates?.get(pos) ?: return null
            Pair(pos, settings)
        } else {
            null
        }
    }

    fun addGate(world: ServerWorld, pos: BlockPos, settings: WaygateSettings){
        val state = getOrInitializeState(world)
        state.gates[pos] = settings
    }

    fun createGateEntities(world: World, pos: BlockPos, parent: WaygateBlockEntity){
        val worldIdentifier = world.registryKey.value
        val gates = waygates[worldIdentifier]?.gates ?: return
        val list: MutableList<WaygateEntity> = mutableListOf()
        gates.forEach { (gatePos, settings) ->
            if (pos != gatePos){
                val spriteParent = world.getBlockEntity(gatePos)
                if (spriteParent != null && spriteParent is WaygateBlockEntity){
                    val wge = WaygateEntity.createWaygateEntity(world, pos, gatePos, settings)
                    list.add(wge)
                    world.spawnEntity(wge)
                }
            }
        }
        parent.setEntities(list)
    }
    
    fun updateSettings(world: World, pos: BlockPos, newSettings: WaygateSettings){
        val worldIdentifier = world.registryKey.value
        waygates[worldIdentifier]?.gates?.put(pos,newSettings)
    }
    
    ////////////////////////////////////////////////

    class WaygatePersistentState: PersistentState(){

        val gates: MutableMap<BlockPos,WaygateSettings> = mutableMapOf()

        fun fromNbt(nbt: NbtCompound): WaygatePersistentState{
            val list = nbt.getList("gate_list",10)
            list.forEach {
                val compound = it as NbtCompound
                val pos = Waygates.readBlockPos("gate_pos", compound)
                val settings = WaygateSettings.fromNbt(compound)
                gates[pos] = settings
            }
            return this
        }

        override fun writeNbt(nbt: NbtCompound): NbtCompound {
            val list = NbtList()
            gates.forEach {
                val compound = NbtCompound()
                Waygates.writeBlockPos("gate_pos",it.key,compound)
                it.value.toNbt(compound)
                list.add(compound)
            }
            nbt.put("gate_list",list)
            return nbt
        }
    }

    
    class WaygateSettings(val customName: String, val color: Int, val priority: Boolean){

        val colorArray = Color(color).getColorComponents(null)

        fun toNbt(nbt: NbtCompound){
            nbt.putString("custom_name",customName)
            nbt.putInt("color",color)
            nbt.putBoolean("priority",priority)
        }
        companion object {
            
            val defaultColors: Map<Int, Int> = mapOf(
                2 to 0x000000,
                3 to 0xAAAAAA,
                4 to 0xAA0000,
                5 to 0xFFAA00,
                6 to 0x00AA00,
                7 to 0x00AAAA,
                8 to 0x0000AA,
                9 to 0xAA00AA,
                10 to 0x555555,
                11 to 0xFFFFFF,
                12 to 0xFF5555,
                13 to 0xFFFF55,
                14 to 0x55FF55,
                15 to 0x55FFFF,
                16 to 0x5555FF,
                17 to 0xFF55FF
            )
            
            fun getDefault(): WaygateSettings{
                val index = Waygates.waygatesRandom.nextInt(defaultColors.size) + 2
                return WaygateSettings("",defaultColors[index]?:0xFFFFFF,false)
            }
            
            fun fromNbt(nbt: NbtCompound): WaygateSettings {
                var name = ""
                var color = 0xFFFFFF
                var priority = false
                if (nbt.contains("custom_name")) {
                    name = nbt.getString("custom_name")
                }
                if (nbt.contains("color")) {
                    color = nbt.getInt("color")
                }
                if (nbt.contains("priority")) {
                    priority = nbt.getBoolean("priority")
                }
                return WaygateSettings(name, color, priority)
            }
        }
    }

}
