package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import de.framework.api.Quad
import de.sakul6499.voxeledit.task.PosSelectTask
import de.sakul6499.voxeledit.task.ReplacePosTask
import de.sakul6499.voxeledit.task.TaskHandler
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object Clipboard {
    private val pos: MutableList<Quad<Player, Vector, Vector, World>> = mutableListOf()
    // selector map? -> as tasks

    fun pushPosition(player: Player, pos1: Vector? = null, pos2: Vector? = null, world: World? = null) {
        var entry = pos.firstOrNull { it.first == player }
        if (entry == null) {
            entry = Quad(player, pos1, pos2, world)
            pos.add(entry)
        } else {
            if (pos1 != null) entry.second = pos1
            if (pos2 != null) entry.third = pos2

            if (entry.fourth != null && world != null && entry.fourth!!.uid != world.uid) player.sendMessage("Warning: world changed by selection!")
            if (world != null) entry.fourth = world
        }
    }

    fun actionPlace(player: Player, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        // check for selectors
        val entry = pos.firstOrNull { it.first == player }
        if (entry?.second == null || entry.third == null) {
            player.sendMessage("You do not have clipboard yet (try selecting something) OR a position mismatch occurred!")
            return
        }

        player.sendMessage("Preparing place action ...")
        TaskHandler.addTask(PosSelectTask(entry.fourth!!, entry.second!!, entry.third!!, materials, hollow), player)
        player.sendMessage("Place task submitted!")
    }

    fun actionReplace(player: Player, filter: Array<Bundle<Material, Byte>>, replace: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        // check for selectors
        val entry = pos.firstOrNull { it.first == player }
        if (entry?.second == null || entry.third == null) {
            player.sendMessage("You do not have clipboard yet (try selecting something) OR a position mismatch occurred!")
            return
        }

        player.sendMessage("Preparing replace action ...")
        TaskHandler.addTask(ReplacePosTask(entry.fourth!!, entry.second!!, entry.third!!, filter, replace, hollow), player)
        player.sendMessage("Replace task submitted!")
    }
}