package de.sakul6499.voxeledit.clipboard

import org.bukkit.entity.Player

class SelectionData(val selection: Selection, val id: Int, var notifier: Player, var loops: Int = 0, var shouldSelect: Boolean = true, var finished: Boolean = false, var canceled: Boolean = false) {
    override fun toString(): String = "#$id notifier: ${notifier.name} blocks: ${selection.blocks} loops: $loops ${if (finished) "[FINISHED]" else ""}  ${if (shouldSelect) "[SHOULD_SELECT]" else ""}  ${if (canceled) "[CANCELED]" else ""}"
}
