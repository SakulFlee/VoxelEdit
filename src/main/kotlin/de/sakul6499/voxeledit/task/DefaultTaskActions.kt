@file:Suppress("DEPRECATION")

package de.sakul6499.voxeledit.task

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

object DefaultTaskActions {
    fun defaultProcess(queue: MutableList<Triple<Vector, Material, Byte>>, undo: MutableList<Triple<Vector, Material, Byte>>, world: World): Boolean {
        if (queue.size <= 0) return false
        val triple = queue[0]
        queue.removeAt(0)

        val block = triple.first.toLocation(world).block
        if (block.type != triple.second || (triple.third == (-1).toByte() || block.data != triple.third)) {
            undo.add(Triple(triple.first, block.type, block.data))

            block.type = triple.second
            block.data = triple.third
        }

        return true
    }

    fun defaultUndo(queue: MutableList<Triple<Vector, Material, Byte>>, undo: MutableList<Triple<Vector, Material, Byte>>, world: World): Boolean {
        if (undo.size <= 0) return false
        val triple = undo[0]
        undo.removeAt(0)

        val block = triple.first.toLocation(world).block
        if (block.type != triple.second || (triple.third == (-1).toByte() || block.data != triple.third)) {
            queue.add(Triple(triple.first, block.type, block.data))

            block.type = triple.second
            block.data = triple.third
        }

        return true
    }
}