package de.sakul6499

import org.bukkit.util.Vector
import java.util.*

/**
 * # Location
 */
data class Location(val x: Float = 0F, val y: Float = 0F, val z: Float = 0F, val uuid: UUID? = null) {
    fun toVector(): Vector = Vector(x, y, z)
}