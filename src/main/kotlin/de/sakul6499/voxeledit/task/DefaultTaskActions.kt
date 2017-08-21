@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.task

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

object DefaultTaskActions {
    private fun exec(from: MutableList<Triple<Vector, Material, Byte>>, to: MutableList<Triple<Vector, Material, Byte>>, world: World): Boolean {
        if (from.size <= 0) return false
        val triple = from[0]
        from.removeAt(0)

        val block = triple.first.toLocation(world).block
        if (block.type != triple.second || (triple.third == (-1).toByte() || block.data != triple.third)) {
            to.add(Triple(triple.first, block.type, block.data))

            block.type = triple.second
            block.data = if (triple.third < (0).toByte()) 0 else triple.third
        }

        return true
    }

    fun defaultProcess(queue: MutableList<Triple<Vector, Material, Byte>>, undo: MutableList<Triple<Vector, Material, Byte>>, world: World): Boolean = exec(queue, undo, world)

    fun defaultUndo(queue: MutableList<Triple<Vector, Material, Byte>>, undo: MutableList<Triple<Vector, Material, Byte>>, world: World): Boolean = exec(undo, queue, world)
}