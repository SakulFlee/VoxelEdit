package de.sakul6499.voxeledit

/**
 * # Sphere
 */
class Sphere(val midPoint: Location, val radius: Int): Range {
    override fun count(): Int {
        return Math.floor((4.0 / 3.0) * Math.PI * radius).toInt()
    }
}