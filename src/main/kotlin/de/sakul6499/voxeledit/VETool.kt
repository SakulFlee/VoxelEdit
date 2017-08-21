package de.sakul6499.voxeledit

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*

object VETool : Listener {
    private val tools: MutableMap<UUID, String> = mutableMapOf()

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!event.item.itemMeta.hasLore()) return

        when (event.action) {
            Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR -> {
                if (event.item.itemMeta.lore.size >= 1) {
                    val cmd = tools[UUID.fromString(event.item.itemMeta.lore[0])]
                    if (cmd != null) {
                        event.isCancelled = true

                        event.player.performCommand(cmd)
                    }
                }
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                if (event.item.itemMeta.lore.size >= 2) {
                    val cmd = tools[UUID.fromString(event.item.itemMeta.lore[1])]
                    if (cmd != null) {
                        event.isCancelled = true

                        event.player.performCommand(cmd)
                    }
                }
            }
            else -> {
            }
        }
    }

    fun addTool(player: Player, left: String? = null, right: String? = null) {
        if (player.inventory.itemInMainHand == null) {
            player.sendMessage("You do not hold any item in your main hand!")
            return
        }

        if (left != null) {
            player.sendMessage("Binding ${ChatColor.GREEN}'/# $left'${ChatColor.RESET} to the current item in hand on action ${ChatColor.AQUA}LEFT${ChatColor.RESET}!")
            val id = UUID.randomUUID()
            tools.put(id, "/# $left")
            if (player.inventory.itemInMainHand.itemMeta.lore == null) {
                player.inventory.itemInMainHand.itemMeta.lore = listOf(id.toString())
            } else player.inventory.itemInMainHand.itemMeta.lore[0] = id.toString()
        }

        if (right != null) {
            player.sendMessage("Binding ${ChatColor.GREEN}'/# $right'${ChatColor.RESET} to the current item in hand on action ${ChatColor.AQUA}RIGHT${ChatColor.RESET}!")
            val id = UUID.randomUUID()
            tools.put(id, "/# $right")
            if (player.inventory.itemInMainHand.itemMeta.lore == null) {
                player.inventory.itemInMainHand.itemMeta.lore = listOf("", id.toString())
            } else player.inventory.itemInMainHand.itemMeta.lore[1] = id.toString()
        }
    }
}