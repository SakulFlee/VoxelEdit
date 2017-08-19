package de.sakul6499.voxeledit.task

import de.framework.api.Bundle
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector
import java.util.*

class TowerTask(private val worldUID: UUID, midPoint: Vector, radius: Int, up: Int, down: Int, materials: Array<Triple<Material, Byte, Boolean>>, hollow: Boolean = false) : Task {
    override var id: Int = 0
    override var loops: Int = 0
    override val overallBlocksToProcess: Int

    // Height
    private val yBegin: Int = midPoint.blockY - down
    private val yEnd: Int = midPoint.blockY + up

    private val xBegin: Int = midPoint.blockX - radius
    private val xEnd: Int = midPoint.blockX + radius

    private val zBegin: Int = midPoint.blockZ - radius
    private val zEnd: Int = midPoint.blockZ + radius

    private val queue = mutableListOf<Bundle<Vector, Triple<Material, Byte, Boolean>>>()

    init {
//        if (materials.isEmpty()) throw IllegalStateException("Materials empty!")

        val random = Random()

        for (y in yBegin..yEnd) {
            if (hollow) {
                for (x in xBegin..xEnd) {
                    queue.add(Bundle(Vector(x, y, zBegin), materials[random.nextInt(materials.size)]))
                    queue.add(Bundle(Vector(x, y, zEnd), materials[random.nextInt(materials.size)]))
                }

                for (z in zBegin..zEnd) {
                    queue.add(Bundle(Vector(xBegin, y, z), materials[random.nextInt(materials.size)]))
                    queue.add(Bundle(Vector(xEnd, y, z), materials[random.nextInt(materials.size)]))
                }
            } else {
                for (x in xBegin..xEnd) {
                    for (z in zBegin..zEnd) {
                        val currentPosition = Vector(x, y, z)

                        queue.add(Bundle(currentPosition, materials[random.nextInt(materials.size)]))
                    }
                }
            }
        }

        overallBlocksToProcess = count()
    }

    override fun count(): Int = queue.size

    override fun process(): Boolean {
        if (queue.size <= 0) return false
        val bundle = queue[0]
        queue.removeAt(0)

        val world = Bukkit.getWorld(worldUID)

        if (bundle.first == null) throw IllegalStateException("Vector is null!")
        if (bundle.second == null) throw IllegalStateException("Material is null!")

        val block = bundle.first!!.toLocation(world).block
        if (block.type != bundle.second!!) {
            block.setType(bundle.second!!.first, bundle.second!!.third)
            block.data = bundle.second!!.second
        }

        return true
    }
}