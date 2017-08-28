package de.sakul6499.voxeledit

import de.framework.logger.Logger
import de.sakul6499.voxeledit.mesh.MeshHandler
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.util.Vector

object VEMode : Listener {

    private val inMode: MutableList<Player> = mutableListOf()
    private val inAssist: MutableList<Player> = mutableListOf()
    private val assistMap: MutableList<Triple<Player, Vector, String>> = mutableListOf()

    @Suppress("UNUSED")
    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        inMode.filter { it == event.player }.forEach {
            val split = event.message.split(" ")
            Logger.debug("###")
            split.forEach {
                Logger.debug(it)
            }
            Logger.debug("###")

            if (inAssist.any { event.player == it }) {
                when (split[0].toLowerCase()) {
                    "assist", "a" -> {
                        event.player.sendMessage("LEAVING ASSIST MODE")
                        inAssist.remove(event.player)
                    }
                    "confirm", "c" -> {
                        val a = assistMap.find { (first) -> first == it }
                        if (a == null) {
                            it.sendMessage("Nothing committed!")
                        } else {
                            MeshHandler.removeMesh(it.uniqueId)
                            it.teleport(a.second.toLocation(it.world))
                            it.performCommand("/# ${a.third}")
                        }
                    }
                // sphere|s <radius> <blockName:blockDataIndex;...>
                    "sphere", "s" -> {
                        if (split.size <= 2) {
                            println("Invalid syntax ...")
                            return
                        }

                        assistMap.removeIf { (first) -> first == it }
                        assistMap.add(Triple(it, it.location.toVector(), event.message))

                        val radius: Int = try {
                            val i = Integer.parseInt(split[1]); if (i <= 0) {
                                it.sendMessage("Radius must be greater than zero!"); return
                            } else i
                        } catch (e: NumberFormatException) {
                            it.sendMessage("Radius must be a number!"); return
                        }

                        Logger.debug("Assisting: $it -> ${event.message}")
                        val mesh = MeshHandler.createMesh(it.uniqueId)

                        var vecs = arrayOf<Vector>()
                        // ABSTRACT MATH
                        val midPoint = event.player.location.toVector()
                        val w = event.player.world

                        var yBegin: Int = midPoint.blockY - radius
                        var yEnd: Int = midPoint.blockY + radius

                        val xBegin: Int = midPoint.blockX - radius
                        val xEnd: Int = midPoint.blockX + radius

                        val zBegin: Int = midPoint.blockZ - radius
                        val zEnd: Int = midPoint.blockZ + radius

                        if (yBegin > 255) yBegin = 255
                        if (yEnd < 0) yEnd = 0

                        for (y in yEnd downTo yBegin) {
                            for (x in xBegin..xEnd) {
                                for (z in zBegin..zEnd) {
                                    val currentPosition = Vector(x, y, z)
                                    val distance = midPoint.distance(currentPosition).toInt()
                                    if (distance == radius) vecs += currentPosition
                                }
                            }
                        }
                        // ABSTRACT MATH

                        mesh.update(w, vecs)
                    }
                    else -> {
                        it.sendMessage("No assist mapping for entry / input found!")
                        it.performCommand("/# ${event.message}")
                    }
                }
            } else {
                when (split[0].toLowerCase()) {
                    "assist", "a" -> {
                        event.player.sendMessage("ENTERING ASSIST MODE")
                        inAssist.add(event.player)
                    }
                    else -> it.performCommand("/# ${event.message}")
                }
            }

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