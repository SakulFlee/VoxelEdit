@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.task

import de.framework.api.Bundle
import de.framework.logger.Logger
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

class PosSelectTask(private val world: World, pos1: Vector, pos2: Vector, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) : Task {
    override val overallBlocksToProcess: Int

    private val queue = mutableListOf<Triple<Vector, Material, Byte>>()
    private val undo = mutableListOf<Triple<Vector, Material, Byte>>()

    init {
        val random = Random()

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

        Logger.debug("Y $yBegin -> $yEnd")
        Logger.debug("X $xBegin -> $xEnd")
        Logger.debug("Z $zBegin -> $zEnd")

        materials.forEach {
            if (it.first == null) throw IllegalStateException("Vector is null!")
            if (it.second == null) throw IllegalStateException("Material is null!")
        }

        for (y in yEnd downTo yBegin) {
            if (hollow) {
                for (x in xBegin..xEnd) {
                    var bundle = materials[random.nextInt(materials.size)]
                    queue.add(Triple(Vector(x, y, zBegin), bundle.first!!, bundle.second!!))

                    bundle = materials[random.nextInt(materials.size)]
                    queue.add(Triple(Vector(x, y, zEnd), bundle.first!!, bundle.second!!))
                }

                for (z in zBegin..zEnd) {
                    var bundle = materials[random.nextInt(materials.size)]
                    queue.add(Triple(Vector(xBegin, y, z), bundle.first!!, bundle.second!!))

                    bundle = materials[random.nextInt(materials.size)]
                    queue.add(Triple(Vector(xEnd, y, z), bundle.first!!, bundle.second!!))
                }

                if (y == yBegin || y == yEnd) {
                    for (x in xBegin..xEnd) {
                        for (z in zBegin..zEnd) {
                            val bundle = materials[random.nextInt(materials.size)]
                            queue.add(Triple(Vector(x, y, z), bundle.first!!, bundle.second!!))
                        }
                    }
                }
            } else {
                for (x in xBegin..xEnd) {
                    for (z in zBegin..zEnd) {
                        val currentPosition = Vector(x, y, z)
                        val bundle = materials[random.nextInt(materials.size)]

                        queue.add(Triple(currentPosition, bundle.first!!, bundle.second!!))
                    }
                }
            }
        }

        overallBlocksToProcess = count()
    }

    override fun count(undo: Boolean): Int = if (undo) this.undo.size else queue.size

    override fun process(): Boolean = DefaultTaskActions.defaultProcess(queue, undo, world)

    override fun undo(): Boolean = DefaultTaskActions.defaultUndo(queue, undo, world)
}