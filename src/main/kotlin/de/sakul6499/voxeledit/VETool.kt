package de.sakul6499.voxeledit

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object VETool : Listener {
    private val leftClickTools: MutableList<Triple<Player, Material, String>> = mutableListOf()
    private val rightClickTools: MutableList<Triple<Player, Material, String>> = mutableListOf()

    @Suppress("UNUSED")
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        when (event.action) {
            Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR -> {
                val triple = leftClickTools.firstOrNull { it.first == event.player && it.second == event.material }
                if (triple != null) event.player.performCommand(triple.third)
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                val triple = rightClickTools.firstOrNull { it.first == event.player && it.second == event.material }
                if (triple != null) event.player.performCommand(triple.third)
            }
            else -> {
            }
        }
    }

    fun unbindToolFromPlayer(player: Player, left: Boolean = false, right: Boolean = false) {
        if (player.inventory.itemInMainHand == null || player.inventory.itemInMainHand.type == Material.AIR) {
            player.sendMessage("You do not hold an item in your main hand OR it can not be used for tooling / binding!")
            return
        }

        val item = player.inventory.itemInMainHand.type

        if (left) {
            leftClickTools.removeIf {
                if (it.first == player && it.second == item) {
                    player.sendMessage("Unbinding command ${ChatColor.GREEN}'${it.third}'${ChatColor.RESET} from '$item' on action ${ChatColor.AQUA}LEFT${ChatColor.RESET}!")

                    true
                } else false
            }
        }
        if (right)
            rightClickTools.removeIf {
                if (it.first == player && it.second == item) {
                    player.sendMessage("Unbinding command ${ChatColor.GREEN}'${it.third}'${ChatColor.RESET} from '$item' on action ${ChatColor.AQUA}RIGHT${ChatColor.RESET}!")

                    true
                } else false
            }
    }

    fun bindToolToPlayer(player: Player, left: String? = null, right: String? = null) {
        if (player.inventory.itemInMainHand == null || player.inventory.itemInMainHand.type == Material.AIR) {
            player.sendMessage("You do not hold an item in your main hand OR it can not be used for tooling / binding!")
            return
        }

        if (left != null) {
            val cmd = if (left.startsWith("/")) left.replace("/", "") else "/# $left"
            player.sendMessage("Binding command ${ChatColor.GREEN}'$cmd'${ChatColor.RESET} to the current item in hand on action ${ChatColor.AQUA}LEFT${ChatColor.RESET}!")

            leftClickTools.removeIf { it.first == player }
            leftClickTools.add(Triple(player, player.inventory.itemInMainHand.type, cmd))
        }

        if (right != null) {
            val cmd = if (right.startsWith("/")) right.replace("/", "") else "/# $right"
            player.sendMessage("Binding command ${ChatColor.GREEN}'$cmd'${ChatColor.RESET} to the current item in hand on action ${ChatColor.AQUA}RIGHT${ChatColor.RESET}!")

            rightClickTools.removeIf { it.first == player }
            rightClickTools.add(Triple(player, player.inventory.itemInMainHand.type, cmd))
        }
    }
}