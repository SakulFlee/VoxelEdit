@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.task

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector
import java.util.*

class CylinderTask(private val world: World, midPoint: Vector, radius: Int, up: Int, down: Int, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) : Task {
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

        for (y in yEnd downTo yBegin) {
            for (x in xBegin..xEnd) {
                for (z in zBegin..zEnd) {
                    val currentPosition = Vector(x, y, z)
                    val distance = Vector(midPoint.blockX, y, midPoint.blockZ).distance(currentPosition).toInt()

                    val bundle = materials[random.nextInt(materials.size)]
                    if (bundle.first == null) throw IllegalStateException("Vector is null!")
                    if (bundle.second == null) throw IllegalStateException("Material is null!")

                    if (hollow) {
                        if (distance == radius) queue.add(Triple(currentPosition, bundle.first!!, bundle.second!!))
                    } else {
                        if (distance <= radius) queue.add(Triple(currentPosition, bundle.first!!, bundle.second!!))
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