package me.fzzyhmstrs.waygates.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.fzzyhmstrs.waygates.entity.WaygateHelper
import me.fzzyhmstrs.waygates.Waygates
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import java.awt.Color

class WaygateScreen(handler: WaygateScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<WaygateScreenHandler>(handler, playerInventory, title) {

    companion object{
        internal val texture = Identifier(Waygates.MOD_ID,"textures/gui/waygate_settings.png")
    }

    private val nameField: TextFieldWidget by lazy {
        createNameFieldWidget()
    }
    private val rainbowButton: RainbowSelectButtonWidget by lazy {
        createRainbowSelectWidget()
    }
    private val colorSlider: RainbowSliderWidget by lazy{
        createRainbowSliderWidget()
    }

    private var red: Int
    private var green: Int
    private var blue: Int
    private var color: Int
    private var priority: Boolean

    private var i: Int
    private var j: Int

    private val sliderListener = object: RainbowSliderWidget.RainbowListener{
        override fun onUpdate(color: Color) {
            onSliderChanged(color)
        }
    }

    private val buttonListener = object: RainbowSelectButtonWidget.OnToggleClickListener{
        override fun onToggleClick(toggled: Boolean) {
            priority = toggled
        }
    }

    init{
        red = handler.red.get()
        green = handler.green.get()
        blue = handler.blue.get()
        color = Color(red,green,blue).rgb
        priority = handler.rainbow.get() == 1
        i = (width - backgroundWidth) / 2
        j = (height - backgroundHeight) / 2
    }

    /////////////////////////////////////////

    override fun handledScreenTick() {
        super.handledScreenTick()
        nameField.tick()
    }

    private fun createNameFieldWidget(): TextFieldWidget{
        val widget = TextFieldWidget(this.textRenderer, i + 58, j + 20, 19, 12, LiteralText.EMPTY)
        widget.setFocusUnlocked(false)
        widget.setEditableColor(-1)
        widget.setUneditableColor(-1)
        widget.setDrawsBackground(false)
        widget.setMaxLength(50)
        widget.setChangedListener { string -> onNameChanged(string) }
        widget.text = ""
        widget.setEditable(false)
        return widget
    }

    private fun onNameChanged(customName: String){
        handler.name = customName
    }

    private fun createRainbowSliderWidget(): RainbowSliderWidget {
        val hsb = Color.RGBtoHSB(red, green, blue, null)
        return RainbowSliderWidget(98, 60, hsb[0].toDouble(), sliderListener)
    }

    private fun onSliderChanged(color: Color){
        red = color.red
        green = color.green
        blue = color.blue
        this.color = color.rgb
    }

    private fun createRainbowSelectWidget(): RainbowSelectButtonWidget{
        return RainbowSelectButtonWidget(139,39,priority,buttonListener)
    }

    ///////////////////////////////////////

    override fun init(){
        super.init()
        titleX = backgroundWidth/2 - textRenderer.getWidth(title)/2
        titleY = 5
        backgroundHeight = 108
        client?.keyboard?.setRepeatEvents(true)
        addSelectableChild(nameField)
        setInitialFocus(nameField)
        addDrawableChild(colorSlider)
        addDrawableChild(rainbowButton)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, texture)
        drawTexture(matrices,0,0,0,0,backgroundWidth,backgroundHeight)

        val nameText = TranslatableText("screen.waygate.name")
        textRenderer.draw(matrices,nameText,8.0F,17.0F,0xCCCCCC)

        val rainbowText = TranslatableText("screen.waygate.rainbow")
        textRenderer.draw(matrices,rainbowText,8.0F,39.0F,0xCCCCCC)

        val colorText = TranslatableText("screen.waygate.color")
        textRenderer.draw(matrices,colorText,8.0F,61.0F,0xCCCCCC)

        val customizeText = TranslatableText("screen.waygate.customize")
        textRenderer.draw(matrices,customizeText,8.0F,83.0F,0xCCCCCC)
    }

    override fun drawForeground(matrices: MatrixStack?, mouseX: Int, mouseY: Int) {
        RenderSystem.disableBlend()
        super.drawForeground(matrices, mouseX, mouseY)
        fill(matrices, 100, 62, 166, 76, color)
    }

    ///////////////////////////////////////////

    override fun render(matrices: MatrixStack?, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, delta)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            client?.player?.closeHandledScreen()
        }
        if (nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.isActive) {
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun close() {
        WaygateScreenHandler.sendInfoC2SPacket(WaygateHelper.WaygateSettings(handler.name, color, priority), handler.syncId)
        super.close()
    }

    override fun removed() {
        super.removed()
        client?.keyboard?.setRepeatEvents(false)
    }

    override fun resize(client: MinecraftClient, width: Int, height: Int) {
        i = (width - backgroundWidth) / 2
        j = (height - backgroundHeight) / 2
        super.resize(client, width, height)
    }
}
