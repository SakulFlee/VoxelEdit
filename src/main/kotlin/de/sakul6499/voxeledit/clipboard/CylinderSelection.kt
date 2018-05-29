@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

class CylinderSelection(private val world: World, private val midPoint: Vector, radius: Int, up: Int, down: Int, hollow: Boolean = false) : Selection {
    override val blocks: Int

    // Height
    private var yBegin: Int = midPoint.blockY - down
    private var yEnd: Int = midPoint.blockY + up

    private val xBegin: Int = midPoint.blockX - radius
    private val xEnd: Int = midPoint.blockX + radius

    private val zBegin: Int = midPoint.blockZ - radius
    private val zEnd: Int = midPoint.blockZ + radius

    private val selectionQueue: MutableList<Vector> = mutableListOf()
    private var data: Array<Triple<Vector, Material, Byte>> = arrayOf()

    init {
        if (yBegin > 255) yBegin = 255
        if (yEnd < 0) yEnd = 0

        for (y in yEnd downTo yBegin) {
            for (x in xBegin..xEnd) {
                for (z in zBegin..zEnd) {
                    val currentPosition = Vector(x, y, z)
                    val distance = Vector(midPoint.blockX, y, midPoint.blockZ).distance(currentPosition).toInt()

                    if (hollow) {
                        if (distance == radius) selectionQueue += currentPosition
                    } else {
                        if (distance <= radius) selectionQueue += currentPosition
                    }
                }
            }
        }

        blocks = selectionQueue.size
    }

    override fun count(): Int = blocks - data.size

    override fun select(): Boolean {
        if (selectionQueue.size <= 0) return false
        val vector = selectionQueue[0]
        selectionQueue.removeAt(0)

        val block = vector.toLocation(world).block
        data += Triple(vector, block.type, block.data)

        return true
    }

    override fun data(): Bundle<World, Array<Triple<Vector, Material, Byte>>> = Bundle(world, data)

    override fun dataWithUpdate(position: Vector): Bundle<World, Array<Triple<Vector, Material, Byte>>> {
        var updated: Array<Triple<Vector, Material, Byte>> = arrayOf()

        val dx = position.blockX - midPoint.blockX
        val dz = position.blockZ - midPoint.blockZ
        val dy = position.blockY - midPoint.blockY

        data.forEach {
            val x = it.first.blockX + dx
            val z = it.first.blockZ + dz
            val y = it.first.blockY + dy

            updated += Triple(Vector(x, y, z), it.second, it.third)
        }

        return Bundle(world, updated)
    }

}