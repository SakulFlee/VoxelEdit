@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.task

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

class TowerTask(private val world: World, midPoint: Vector, radius: Int, up: Int, down: Int, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) : Task {
    override val overallBlocksToProcess: Int

    // Height
    private var yBegin: Int = midPoint.blockY - down
    private var yEnd: Int = midPoint.blockY + up

    private val xBegin: Int = midPoint.blockX - radius
    private val xEnd: Int = midPoint.blockX + radius

    private val zBegin: Int = midPoint.blockZ - radius
    private val zEnd: Int = midPoint.blockZ + radius

    private val queue = mutableListOf<Triple<Vector, Material, Byte>>()
    private val undo = mutableListOf<Triple<Vector, Material, Byte>>()

    init {
        val random = Random()

        if (yBegin > 255) yBegin = 255
        if (yEnd < 0) yEnd = 0

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