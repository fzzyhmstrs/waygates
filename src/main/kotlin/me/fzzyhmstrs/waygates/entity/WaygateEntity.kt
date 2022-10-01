package me.fzzyhmstrs.ai_odyssey.entity

import me.emafire003.dev.coloredglowlib.ColoredGlowLib
import me.emafire003.dev.coloredglowlib.util.Color
import me.fzzyhmstrs.ai_odyssey.registry.RegisterEntity
import me.fzzyhmstrs.amethyst_core.nbt_util.Nbt
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos

class WaygateEntity(entityType: EntityType<out WaygateEntity>, world: World): Entity(entityType, world) {

    constructor(entityType: EntityType<out WaygateEntity>,
                world: World,
                settings: WaygateHelper.WaygateSettings,
                destPos: BlockPos,
                parentPos: BlockPos):
            this( entityType, world){
        this.settings = settings
        destination = destPos
        parent = parentPos
    }

    var life = 20
    private var initAnimation: Iterator<Vec3d>? = null
    private var parent: BlockPos = BlockPos.ORIGIN
    private var destination = BlockPos.ORIGIN
    var settings: WaygateHelper.WaygateSettings = WaygateHelper.WaygateSettings.getDefault()

    init{
        this.isInvulnerable  = true
        if (settings.priority){
            ColoredGlowLib.setRainbowColorToEntity(this,true)
        } else {
            val color = Color(settings.color)
            ColoredGlowLib.setColorToEntity(this, color)
        }
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        life = Nbt.readIntNbt("life",nbt)
        destination = Nbt.readBlockPos("dest_pos",nbt)
        parent = Nbt.readBlockPos("parent_pos", nbt)
        settings = WaygateHelper.WaygateSettings.fromNbt(nbt)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        Nbt.writeIntNbt("life",life, nbt)
        Nbt.writeBlockPos("dest_pos",destination,nbt)
        Nbt.writeBlockPos("parent_pos",parent,nbt)
        settings.toNbt(nbt)
    }

    override fun createSpawnPacket(): Packet<*> {
        TODO("Not yet implemented")
    }

    override fun tick() {
        super.tick()
        if (initAnimation != null){
            if (initAnimation?.hasNext() == true){
                val nextPos = initAnimation?.next() ?: this.pos
                this.setPosition(nextPos)
            }
        }
        life--
        if (life <= 0){
            douse()
        }
        addParticles(1)
    }

    override fun initDataTracker() {}

    override fun hasNoGravity(): Boolean { return true }

    override fun collides(): Boolean { return false }

    override fun isPushable(): Boolean { return false }

    override fun collidesWith(other: Entity?): Boolean { return false }

    override fun collidesWithStateAtPos(pos: BlockPos, state: BlockState): Boolean { return false }

    override fun checkBlockCollision() { return }

    override fun isInsideWall(): Boolean { return false }

    override fun shouldRenderName(): Boolean {
        return settings.customName != ""
    }

    private fun addParticles(count: Int){
        for (i in 1..count){
            val rndRange = if (world.random.nextFloat() > 0.7){
                0.5
            } else {
                0.25
            }
            val rndX = (world.random.nextDouble() - 0.5) * rndRange
            val rndY = (world.random.nextDouble() - 0.5) * rndRange
            val rndZ = (world.random.nextDouble() - 0.5) * rndRange
            world.addParticle(ParticleTypes.SMOKE,rndX, rndY, rndZ, 0.0, 0.0, 0.0)
        }
    }

    private fun douse(){
        for (i in 1..20){
            val rndX = (world.random.nextDouble() - 0.5) * 0.5
            val rndY = (world.random.nextDouble() - 0.5) * 0.5
            val rndZ = (world.random.nextDouble() - 0.5) * 0.5
            val velX = (world.random.nextDouble() - 0.5) * 0.5
            val velY = (world.random.nextDouble() - 0.5) * 0.5
            val velZ = (world.random.nextDouble() - 0.5) * 0.5
            world.addParticle(ParticleTypes.SMOKE,rndX, rndY, rndZ, velX, velY, velZ)
        }
        //world.playSound()
        discard()
    }

    override fun interact(player: PlayerEntity, hand: Hand): ActionResult {
        warp(player, destination)
        return super.interact(player, hand)
    }

    fun warp(player: PlayerEntity, destination: BlockPos){
        player.teleport(destination.x + 0.5, destination.y + 1.0, destination.z + 0.5)
    }

    companion object{

        private val animationFractions: List<Double> = listOf(
            0.0,
            0.05,
            0.15,
            0.3,
            0.5,
            0.75,
            1.0
        )

        fun createWaygateEntity(world: World,startGatePos: BlockPos, endGatePos: BlockPos, settings: WaygateHelper.WaygateSettings): WaygateEntity{
            val wge = WaygateEntity(RegisterEntity.WAYGATE_ENTITY, world, settings, endGatePos, startGatePos)
            val gateDistance = startGatePos.getManhattanDistance(endGatePos)
            val spriteDistance = MathHelper.lerpFromProgress(gateDistance.toDouble(),10.0,10000.0,1.5,3.5)
            val centeredPos = Vec3d(startGatePos.x + 0.5, startGatePos.y + 1.0, startGatePos.z + 0.5)
            val angleVecBP = endGatePos.subtract(startGatePos)
            val angleVec = Vec3d(angleVecBP.x.toDouble(), angleVecBP.y.toDouble(), angleVecBP.z.toDouble()).normalize().multiply(spriteDistance)
            val finalPos = centeredPos.add(0.0,1.62,0.0).add(angleVec)
            val startPos = Vec3d(finalPos.x,centeredPos.y,finalPos.z)
            val finalHeight = finalPos.y - startPos.y
            val animationList: MutableList<Vec3d> = mutableListOf()
            animationFractions.forEach{
                val framePos = Vec3d(finalPos.x,centeredPos.y + finalHeight*it, finalPos.z)
                animationList.add(framePos)
            }
            wge.initAnimation = animationList.iterator()
            wge.setPosition(startPos)
            val pitch = asin(angleVec.y * -1.0)
            val j = cos(pitch)
            val yaw = asin(angleVec.x / j)
            wge.setRotation((yaw * 180 / PI).toFloat(), (pitch * 180 / PI).toFloat())
            return wge
        }

    }
}
