package de.sakul6499

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.util.Vector
import java.util.*

data class SphereData(val worldUID: UUID, val midPoint: Vector, val radius: Int, val materials: List<Material>) {
    val xBegin: Int = midPoint.blockX - radius
    val xEnd: Int = midPoint.blockX + radius
    val yBegin: Int = midPoint.blockY - radius
    val yEnd: Int = midPoint.blockY + radius
    val zBegin: Int = midPoint.blockZ - radius
    val zEnd: Int = midPoint.blockZ + radius

    inline fun run(consumer: (Vector) -> Unit) {
        for (x in xBegin..xEnd) {
            for (y in yBegin..yEnd) {
                for (z in zBegin..zEnd) {
                    val currentPosition = Vector(x, y, z)

                    val distance = midPoint.distance(currentPosition)
                    if (distance <= radius) {
                        consumer.invoke(currentPosition)
                    }
                }
            }
        }
    }

    fun count(): Int {
        var blocksProcessed = 0

        run { blocksProcessed++ }

        return blocksProcessed
    }

    fun process(): Int {
        var blocksProcessed = 0

        val world = Bukkit.getWorld(worldUID)
        run {
            val typeIndex = Random().nextInt(materials.size - 1)
            world.getBlockAt(it.toLocation(world)).type = materials[typeIndex]

            blocksProcessed++
        }

        return blocksProcessed
    }
}