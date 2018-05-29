package de.sakul6499.voxeledit.task

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.util.Vector

class InsertionTask(private val world: World, private val queue: MutableList<Triple<Vector, Material, Byte>>) : Task {
    override val overallBlocksToProcess: Int = count()

    private val undo = mutableListOf<Triple<Vector, Material, Byte>>()

    override fun count(undo: Boolean): Int = if (undo) this.undo.size else queue.size

    override fun process(): Boolean = DefaultTaskActions.defaultProcess(queue, undo, world)

    override fun undo(): Boolean = DefaultTaskActions.defaultUndo(queue, undo, world)
}