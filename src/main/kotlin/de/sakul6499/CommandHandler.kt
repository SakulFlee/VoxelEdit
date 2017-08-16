package de.sakul6499

import de.framework.logger.Logger
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class CommandHandler : BukkitCommand("voxeledit") {
    init {
        description = "Voxel Edit!"
        usageMessage = "/voxeledit | /ve [use 'help' for more!]"
        permission = "voxeledit"
        aliases = listOf("ve", "/voxeledit", "/ve")
    }

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        if (sender is Player) {
            sender.sendMessage("VoxelEdit!")
            sender.sendMessage("#$commandLabel")
            args?.forEach {
                println(" -> $it")
            }

            if(args == null || args.isEmpty()) {
                // TODO "help"
                return true
            }

            when(args[0].toLowerCase()) {
                "sphere" -> {
                    val loc = sender.location
                    val dia = 10

                    val lx = loc.blockX
                    val ly = loc.blockY
                    val lz = loc.blockZ

                    val rad = dia / 2
                    val lrad = rad + Math.sqrt((lx.toDouble()*lx.toDouble()) + (ly.toDouble()*ly.toDouble()) + (lz.toDouble()*lz.toDouble()))

                    for(x in lx-rad..lx+rad) {
                        for(y in ly-rad..ly+rad) {
                            for(z in lz-rad..lz+rad) {
                                val X = x.toDouble()
                                val Y = y.toDouble()
                                val Z = z.toDouble()

                                val s = Math.sqrt((X*X) + (Y*Y) + (Z*Z))
                                sender.sendMessage("X $X Y $Y Z $Z -> $s [${if(s <= rad) "TRUE" else "FALSE"}] [${if(s <= lrad) "TRUE" else "FALSE"}]")

                                if(s <= lrad) {
                                    Location(loc.world, X, Y, Z).block.type = Material.STONE
                                }
                            }
                        }
                    }
                }
                else -> sender.sendMessage("Unknown!")
            }

        } else {
            Logger.debug("Command line command access for VoxelEdit is limited [Currently no commands implemented! :( ]")
        }

        return true
    }
}