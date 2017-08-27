package de.sakul6499.voxeledit.clipboard

import de.framework.logger.Logger
import de.sakul6499.voxeledit.task.DefaultTaskActions
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Insertion(val world: World, val data: MutableList<Triple<Vector, Material, Byte>>, val id: Int, var notifier: Player, var loops: Int = 0, var finished: Boolean = false, var canceled: Boolean = false, var undo: Boolean = false) {
    val blocks: Int = data.size

    private val _undo = mutableListOf<Triple<Vector, Material, Byte>>()

    companion object {
        fun fromSelection(selection: Selection, position: Vector, id: Int, notifier: Player, loops: Int = 0, finished: Boolean = false, canceled: Boolean = false): Insertion {
            val data = selection.dataWithUpdate(position)
            return Insertion(data.first!!, data.second!!.toMutableList(), id, notifier, loops, finished, canceled)
        }
    }

    init {
        Logger.debug("Data Size: ${data.size}")
        Logger.debug("Blocks: $blocks")
    }

    fun task() = DefaultTaskActions.defaultProcess(data, _undo, world)

    fun undo() = DefaultTaskActions.defaultUndo(data, _undo, world)

    override fun toString(): String = "#$id notifier: ${notifier.name} blocks: $blocks loops: $loops ${if (finished) "[FINISHED]" else ""}  ${if (canceled) "[CANCELED]" else ""} ${if (undo) "[UNDO]" else ""}"
}