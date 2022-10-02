package me.fzzyhmstrs.waygates.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.waygates.Waygates
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.widget.SliderWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.awt.Color
import kotlin.math.abs


class RainbowSliderWidget(x: Int, y: Int, value: Double, private val listener: RainbowListener?): SliderWidget(x,y,106,18, LiteralText.EMPTY,value) {

    private var color: Color = Color.RED

    override fun renderBackground(matrices: MatrixStack?, client: MinecraftClient?, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, WaygateScreen.texture)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        this.drawTexture(matrices, x + (value * (width - 5).toDouble()).toInt(), y, 56, 108, 5, 18)
    }

    override fun renderButton(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        val minecraftClient = MinecraftClient.getInstance()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()
        renderBackground(matrices, minecraftClient, mouseX, mouseY)
    }

    fun getColor(): Color{
        return color
    }

    override fun updateMessage() {
    }

    override fun applyValue() {
        color = colorFromFloat(value.toFloat())
        listener?.onUpdate(color)
    }

    private fun colorFromFloat(slider: Float): Color {
        val clampedSlider = MathHelper.clampedLerp(0.0f, 1.0f, slider)
        val h = clampedSlider * 360.0f
        val x = 1 - abs(h / 60.0f % 2.0f - 1)
        val r: Float
        val g: Float
        val b: Float
        if (h >= 0 && h < 60) {
            r = 1.0f
            g = x
            b = 0.0f
        } else if (h >= 60 && h < 120) {
            r = x
            g = 1.0f
            b = 0.0f
        } else if (h >= 120 && h < 180) {
            r = 0.0f
            g = 1.0f
            b = x
        } else if (h >= 180 && h < 240) {
            r = 0.0f
            g = x
            b = 1.0f
        } else if (h >= 240 && h < 300) {
            r = x
            g = 0.0f
            b = 1.0f
        } else {
            r = 1.0f
            g = 0.0f
            b = x
        }
        return Color(r, g, b)
    }

    interface RainbowListener{
        fun onUpdate(color: Color)
    }

}