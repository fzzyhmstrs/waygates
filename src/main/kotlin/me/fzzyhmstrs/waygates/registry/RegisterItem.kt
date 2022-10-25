package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.Waygates
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterItem {

    val ETERNITY_SHARD = Item(FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(11))
    val ETERNITY_STONE = Item(FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(11))

    fun registerAll(){
        Registry.register(Registry.ITEM, Identifier(Waygates.MOD_ID,"eternity_shard"), ETERNITY_SHARD)
        Registry.register(Registry.ITEM, Identifier(Waygates.MOD_ID,"eternity_stone"), ETERNITY_STONE)
    }

}