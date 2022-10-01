package me.fzzyhmstrs.waygates.screen

import com.mojang.blaze3d.systems.RenderSystem
import me.emafire003.dev.coloredglowlib.util.Color
import me.fzzyhmstrs.ai_odyssey.entity.WaygateHelper
import me.fzzyhmstrs.ai_odyssey.screen.WaygateScreenHandler
import me.fzzyhmstrs.waygates.Waygates
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class WaygateScreen(handler: WaygateScreenHandler, playerInventory: PlayerInventory, title: Text):
    HandledScreen<WaygateScreenHandler>(handler, playerInventory, title), ScreenHandlerListener {

    private val colorCollisionBoxes: Map<Int,Bounds> = mapOf(
        2 to Bounds(9,105,27, 123),
        3 to Bounds(29,105,47,123),
        4 to Bounds(49,105,67,123),
        5 to Bounds(69,105,87,123),
        6 to Bounds(89,105,107,123),
        7 to Bounds(109,105,127,123),
        8 to Bounds(129,105,147,123),
        9 to Bounds(149,105,167,123),
        10 to Bounds(9,125,27, 143),
        11 to Bounds(29,125,47,143),
        12 to Bounds(49,125,67,143),
        13 to Bounds(69,125,87,143),
        14 to Bounds(89,125,107,143),
        15 to Bounds(109,125,127,143),
        16 to Bounds(129,125,147,143),
        17 to Bounds(149,125,167,143)
    )

    private val rainbowCollisionBox = Bounds(139,39,167,55)

    private val nameField: TextFieldWidget by lazy {
        createNameFieldWidget()
    }
    private val redField: TextFieldWidget by lazy {
        createRedFieldWidget()
    }
    private val greenField: TextFieldWidget by lazy {
        createGreenFieldWidget()
    }
    private val blueField: TextFieldWidget by lazy {
        createBlueFieldWidget()
    }

    private var color: Int
    private var red: Int
    private var green: Int
    private var blue: Int

    private var i: Int
    private var j: Int

    private val texture = Identifier(Waygates.MOD_ID,"textures/gui/waygate_settings.png")

    init{
        val clr = Color(handler.color.get())
        color = handler.color.get()
        red = clr.red
        green = clr.green
        blue = clr.blue
        i = (width - backgroundWidth) / 2
        j = (height - backgroundHeight) / 2
    }

    /////////////////////////////////////////

    override fun handledScreenTick() {
        super.handledScreenTick()
        nameField.tick()
        redField.tick()
        greenField.tick()
        blueField.tick()
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

    private fun createRedFieldWidget(): TextFieldWidget{
        val widget = NumFieldWidget(this.textRenderer, i + 73, j + 87, 19, 12, LiteralText.EMPTY)
        widget.setFocusUnlocked(false)
        widget.setEditableColor(-1)
        widget.setUneditableColor(-1)
        widget.setDrawsBackground(false)
        widget.setMaxLength(3)
        widget.setChangedListener { string -> onRedChanged(string) }
        widget.text = red.toString()
        widget.setEditable(false)
        return widget
    }

    private fun onRedChanged(customName: String){
        red = customName.toInt()
    }

    private fun createGreenFieldWidget(): TextFieldWidget{
        val widget = NumFieldWidget(this.textRenderer, i + 109, j + 87, 19, 12, LiteralText.EMPTY)
        widget.setFocusUnlocked(false)
        widget.setEditableColor(-1)
        widget.setUneditableColor(-1)
        widget.setDrawsBackground(false)
        widget.setMaxLength(3)
        widget.setChangedListener { string -> onGreenChanged(string) }
        widget.text = green.toString()
        widget.setEditable(false)
        return widget
    }

    private fun onGreenChanged(customName: String){
        green = customName.toInt()
    }

    private fun createBlueFieldWidget(): TextFieldWidget{
        val widget = NumFieldWidget(this.textRenderer, i + 145, j + 87, 19, 12, LiteralText.EMPTY)
        widget.setFocusUnlocked(false)
        widget.setEditableColor(-1)
        widget.setUneditableColor(-1)
        widget.setDrawsBackground(false)
        widget.setMaxLength(3)
        widget.setChangedListener { string -> onBlueChanged(string) }
        widget.text = blue.toString()
        widget.setEditable(false)
        return widget
    }

    private fun onBlueChanged(customName: String){
        blue = customName.toInt()
    }

    ///////////////////////////////////////

    override fun init(){
        super.init()
        titleX = backgroundWidth/2 - textRenderer.getWidth(title)/2
        titleY = 5
        backgroundHeight = 150
        client?.keyboard?.setRepeatEvents(true)
        addSelectableChild(nameField)
        addSelectableChild(redField)
        addSelectableChild(greenField)
        addSelectableChild(blueField)
        setInitialFocus(nameField)
        handler.addListener(this)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val mX = mouseX - i
        val mY = mouseY - j
        if (mY < 105.0){
            if (rainbowCollisionBox.check(mX,mY)){
                client?.interactionManager?.clickButton(handler.syncId, 1)
                return true
            }
        } else {
            for (boxEntry in colorCollisionBoxes){
                if (boxEntry.value.check(mX,mY)){
                    client?.interactionManager?.clickButton(handler.syncId, boxEntry.key)
                    return true
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        val mX = (mouseX - i).toDouble()
        val mY = (mouseY - j).toDouble()
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, this.texture)
        this.drawTexture(matrices, i, j, 0, 0, backgroundWidth, backgroundHeight)

        //need all that enchantment table garbo?

        val rainbowSelected = handler.rainbow.get() == 1
        val rainbowCollides = rainbowCollisionBox.check(mX, mY)
        val buttonU = if(rainbowSelected) 28 else 0
        val buttonV = if(rainbowCollides) 166 else 150
        this.drawTexture(matrices, i + 139, j + 39, buttonU, buttonV, 28, 16)

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
            client!!.player!!.closeHandledScreen()
        }
        if (nameField.keyPressed(keyCode, scanCode, modifiers) || nameField.isActive) {
            return true
        }
        if (redField.keyPressed(keyCode, scanCode, modifiers) || redField.isActive) {
            return true
        }
        if (greenField.keyPressed(keyCode, scanCode, modifiers) || greenField.isActive) {
            return true
        }
        if (blueField.keyPressed(keyCode, scanCode, modifiers) || blueField.isActive) {
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun close() {
        val clr = Color(red,green,blue).colorValue
        val priority = handler.rainbow.get() == 1
        WaygateScreenHandler.sendInfoC2SPacket(WaygateHelper.WaygateSettings(handler.name,clr,priority), handler.syncId)
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

    override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {}

    override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) {
        if (property == 0){
            val clr = Color(value)
            color = value
            red = clr.red
            green = clr.green
            blue = clr.blue
            redField.text = red.toString()
            greenField.text = green.toString()
            blueField.text = blue.toString()

        }
    }

    ////////////////////////////////////

    private class NumFieldWidget(textRenderer: TextRenderer, x: Int, y: Int, width: Int, height: Int, text: Text): TextFieldWidget(textRenderer, x, y, width, height, text){
        //numeral-only implementation of the text field widget for inputting the RGB
        override fun charTyped(chr: Char, modifiers: Int): Boolean {
            if (!this.isActive) {
                return false
            }
            if (!chr.isDigit()) return false
            return super.charTyped(chr, modifiers)
        }

    }

    private class Bounds(x1: Int, y1: Int, x2: Int, y2: Int){
        //quick class for checking a lot of buttons in a non-annoying way
        //designed to work off the upper corner of the screen itself, so mouseX and mouseY should be offset by the left and upper screen margin
        private val xa = x1.toDouble()
        private val ya = y1.toDouble()
        private val xb = x2.toDouble()
        private val yb = y2.toDouble()

        fun check(x: Double, y: Double): Boolean{
            return !(x < xa || y < ya || x >= xb || y >= yb)
        }
    }

}
