package me.fzzyhmstrs.waygates.registry

import me.fzzyhmstrs.waygates.MOD_ID
import me.fzzyhmstrs.waygates.block.DimensionalAnchorBlock
import me.fzzyhmstrs.waygates.block.WaygateBlock
import me.fzzyhmstrs.waygates.item.DimensionalAnchorItem
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object RegisterBlock {

    val DIMENSIONAL_ANCHOR = DimensionalAnchorBlock(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(22.5f, 600.0f))
    val WAYGATE = WaygateBlock(FabricBlockSettings.of(Material.METAL, MapColor.IRON_GRAY).requiresTool().strength(22.5f, 600.0f))

    fun registerAll(){
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "dimensional_anchor"), DIMENSIONAL_ANCHOR)
        Registry.register(
            Registry.ITEM, Identifier(MOD_ID,"dimensional_anchor"), DimensionalAnchorItem(
                DIMENSIONAL_ANCHOR,
                FabricItemSettings().group(ItemGroup.TRANSPORTATION).maxCount(2))
        )

        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "waygate"), WAYGATE)
        Registry.register(
            Registry.ITEM, Identifier(MOD_ID,"waygate"), BlockItem(
                WAYGATE,
                FabricItemSettings().group(ItemGroup.TRANSPORTATION))
        )
    }

}