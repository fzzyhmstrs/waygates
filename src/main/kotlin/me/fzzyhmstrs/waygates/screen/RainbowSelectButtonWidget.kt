package me.fzzyhmstrs.waygates.screen

import net.minecraft.client.gui.widget.ToggleButtonWidget

class RainbowSelectButtonWidget(x: Int, y: Int,toggled: Boolean, private val listener: OnToggleClickListener): ToggleButtonWidget(x,y,28,16,toggled) {

    init{
        setTextureUV(0,109,28,16,WaygateScreen.texture)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        super.onClick(mouseX, mouseY)
        toggled = !toggled
        listener.onToggleClick(toggled)
    }

    interface OnToggleClickListener{
        fun onToggleClick(toggled: Boolean)
    }

}