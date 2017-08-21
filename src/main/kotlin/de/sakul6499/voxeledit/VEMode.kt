package de.sakul6499.voxeledit

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object VEMode : Listener {

    private val inMode: MutableList<Player> = mutableListOf()

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        inMode.filter { it == event.player }.forEach {
            it.performCommand("/# ${event.message}")
            event.recipients.clear()

            event.isCancelled = true
        }

        event.recipients.removeIf { inMode.contains(it) }
    }

    fun handlePlayer(player: Player) {
        if (inMode.contains(player)) {
            // remove
            inMode.remove(player)

            player.sendMessage("${ChatColor.RED}You are now leaving VE-Mode.")
        } else {
            // add
            inMode.add(player)

            player.sendMessage("${ChatColor.AQUA}WELCOME!")
            player.sendMessage("You are now entering ${ChatColor.AQUA}VE-Mode.")
            player.sendMessage("The ${ChatColor.AQUA}VE-Mode${ChatColor.RESET} is a special mode, designed for ${ChatColor.AQUA}build(ing|er).")
            player.sendMessage("")
            player.sendMessage("In short:")
            player.sendMessage("${ChatColor.AQUA}1.${ChatColor.RESET} You will ${ChatColor.RED}NO LONGER${ChatColor.RESET} receive ${ChatColor.RED}ANY${ChatColor.RESET} chat message")
            player.sendMessage("${ChatColor.AQUA}2.${ChatColor.RESET} ${ChatColor.RED}EVERY${ChatColor.RESET} chat message you type will be translated to a VoxelEdit command")
            player.sendMessage("${ChatColor.AQUA}3.${ChatColor.RESET} If the command is not complete, I will help you [interactive-commands]")
            player.sendMessage("")
            player.sendMessage("The AI is currently ${ChatColor.RED}not implemented${ChatColor.RESET}! [Third mentioned point ${ChatColor.RED}WILL NOT${ChatColor.RESET} work (yet)]")
            player.sendMessage("")
            player.sendMessage("To exit use 'mode' again.")
        }
    }
}