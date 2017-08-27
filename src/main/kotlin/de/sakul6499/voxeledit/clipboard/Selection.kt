package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

interface Selection {
    val blocks: Int

    fun count(): Int

    fun select(): Boolean
    fun data(): Bundle<World, Array<Triple<Vector, Material, Byte>>>
}