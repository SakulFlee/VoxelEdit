package de.sakul6499.voxeledit

import de.framework.api.Bundle
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.material.Wood
import org.bukkit.util.Vector
import java.util.*

data class SphereData(private val worldUID: UUID, private val midPoint: Vector, private val radius: Int, private val materials: List<Material>, private val applyPhysics: Boolean = true) {
    private val xBegin: Int = midPoint.blockX - radius
    private val xEnd: Int = midPoint.blockX + radius
    private val yBegin: Int = midPoint.blockY - radius
    private val yEnd: Int = midPoint.blockY + radius
    private val zBegin: Int = midPoint.blockZ - radius
    private val zEnd: Int = midPoint.blockZ + radius

    private val queue = mutableListOf<Bundle<Vector, Material>>()

    init {
        if (materials.isEmpty()) throw IllegalStateException("Materials empty!")

        val random = Random()

        for (y in yBegin..yEnd) {
            for (x in xBegin..xEnd) {
//            for (y in yBegin..yEnd) {
                for (z in zBegin..zEnd) {
                    val currentPosition = Vector(x, y, z)

                    val distance = midPoint.distance(currentPosition)
                    if (distance <= radius) {
                        queue.add(Bundle(currentPosition, materials[random.nextInt(materials.size)]))
                    }
                }
            }
        }
    }

//    @Deprecated(message = "unused")
//    inline private fun run(consumer: (Vector) -> Unit) {
//        for (x in xBegin..xEnd) {
//            for (y in yBegin..yEnd) {
//                for (z in zBegin..zEnd) {
//                    val currentPosition = Vector(x, y, z)
//
//                    val distance = midPoint.distance(currentPosition)
//                    if (distance <= radius) {
//                        consumer.invoke(currentPosition)
//                    }
//                }
//            }
//        }
//    }

    fun count(): Int = queue.size

    fun process() {
        val world = Bukkit.getWorld(worldUID)
        val bundle = queue[0]
        queue.removeAt(0)

        if (bundle.first == null) throw IllegalStateException("Vector is null!")
        if (bundle.second == null) throw IllegalStateException("Material is null!")

        // TODO: physics >:D
        val block = bundle.first!!.toLocation(world).block
        if (block.type != bundle.second!!) {
            block.setType(bundle.second, applyPhysics)
            if (block.state.data is Wood) {
                block.data = 1
            }
        }
    }
}