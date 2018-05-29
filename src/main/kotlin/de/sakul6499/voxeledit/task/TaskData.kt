package de.sakul6499.voxeledit.task

import org.bukkit.entity.Player

data class TaskData(val task: Task, val id: Int, var notifier: Player, var loops: Int = 0, var finished: Boolean = false, var undo: Boolean = false, var canceled: Boolean = false) {
    override fun toString(): String = "#$id notifier: ${notifier.name} loops: $loops ovbtp: ${task.overallBlocksToProcess} ${if (finished) "[FINISHED]" else ""}  ${if (undo) "[UNDO]" else ""}  ${if (canceled) "[CANCELED]" else ""}"
}