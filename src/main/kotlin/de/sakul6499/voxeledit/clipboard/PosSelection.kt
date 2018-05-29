@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

class PosSelection(private val world: World, pos1: Vector, pos2: Vector, hollow: Boolean = false) : Selection {
    override val blocks: Int

    private val midPoint: Vector = pos1.midpoint(pos2)

    private val selectionQueue: MutableList<Vector> = mutableListOf()
    private var data: Array<Triple<Vector, Material, Byte>> = arrayOf()

    init {
        // Height
        var yBegin: Int
        var yEnd: Int
        if (pos1.blockY > pos2.blockY) {
            yBegin = pos2.blockY
            yEnd = pos1.blockY
        } else {
            yBegin = pos1.blockY
            yEnd = pos2.blockY
        }

        if (yEnd > 255) yEnd = 255
        if (yBegin < 0) yBegin = 0

        val xBegin: Int
        val xEnd: Int
        if (pos1.blockX > pos2.blockX) {
            xBegin = pos2.blockX
            xEnd = pos1.blockX
        } else {
            xBegin = pos1.blockX
            xEnd = pos2.blockX
        }

        val zBegin: Int
        val zEnd: Int
        if (pos1.blockZ > pos2.blockZ) {
            zBegin = pos2.blockZ
            zEnd = pos1.blockZ
        } else {
            zBegin = pos1.blockZ
            zEnd = pos2.blockZ
        }

        for (y in yEnd downTo yBegin) {
            if (hollow) {
                for (x in xBegin..xEnd) {
                    selectionQueue += Vector(x, y, zBegin)
                    selectionQueue += Vector(x, y, zEnd)
                }

                for (z in zBegin..zEnd) {
                    selectionQueue += Vector(xBegin, y, z)
                    selectionQueue += Vector(xEnd, y, z)
                }

                if (y == yBegin || y == yEnd) {
                    for (x in xBegin..xEnd) {
                        for (z in zBegin..zEnd) {
                            selectionQueue += Vector(x, y, z)
                        }
                    }
                }
            } else {
                for (x in xBegin..xEnd) {
                    for (z in zBegin..zEnd) {
                        selectionQueue += Vector(x, y, z)
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