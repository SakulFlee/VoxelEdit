package de.sakul6499

import de.framework.logger.Logger
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class CommandHandler : BukkitCommand("voxeledit") {
    init {
        description = "Voxel Edit!"
        usageMessage = "/voxeledit | /ve [use 'help' for more!]"
        permission = "voxeledit"
        aliases = listOf("ve", "/voxeledit", "/ve")
    }

    var world: World? = null

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        try {
            if (sender is Player) {
                sender.sendMessage("VoxelEdit!")
                sender.sendMessage("#$commandLabel")
                args?.forEach {
                    println(" -> $it")
                }

                if (args == null || args.isEmpty()) {
                    // TODO "help"
                    return true
                }

                when (args[0].toLowerCase()) {
                    "sphere" -> {
                        if (args.size <= 2) {
                            sender.sendMessage("[!] /ve sphere <radius> <blockIDs;|blockName;>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = Integer.parseInt(args[1])

                        val literalMaterials = args[2].split(";")
                        val materials = mutableListOf<Material>()
                        literalMaterials.forEach {
                            materials += Material.matchMaterial(it)
                        }

                        val data = SphereData(world.uid, midPoint, radius, materials)
                        val task = SphereTask(data)

                        Bukkit.getServer().scheduler.runTaskAsynchronously(ServerPlugin.javaPlugin, {
                            val startMS = System.currentTimeMillis()

                            val toProcess =  task.count()
                            sender.sendMessage("to Process: $toProcess")
                            Logger.info("to Process: $toProcess")
                            val endFirstMS = System.currentTimeMillis()

                            val blocksProcessed = task.process()
                            sender.sendMessage("Processed: $blocksProcessed")
                            Logger.info("Processed: $blocksProcessed")
                            val endSecondMS = System.currentTimeMillis()

                            Logger.debug("Started: $startMS ms")
                            Logger.debug("First: $endFirstMS ms")
                            Logger.debug("Second: $endSecondMS ms")

                            Logger.debug("Duration First: ${endFirstMS - startMS} ms")
                            Logger.debug("Duration Second: ${endSecondMS - endFirstMS} ms")
                        })

//                        Logger.debug("# locX $locX locY $locY locZ $locZ")
//
//                        /*
//                      X
//                     /_\ _ Z
//                      |  /|
//                      | /
//                      \- - -> Y
//                     */
//
//                        sender.teleport(loc.add(0.0, radius.toDouble(), 0.0))
//
//                        val xBegin = locX - radius
//                        val xEnd = locX + radius
//                        val yBegin = locY - radius
//                        val yEnd = locY + radius
//                        val zBegin = locZ - radius
//                        val zEnd = locZ + radius
//
//                        val blocksTotal = (xEnd - xBegin) * (yEnd - yBegin) * (zEnd - zBegin)
//                        println("Total blocks: $blocksTotal")
//
//                        var blocksSet = 0
//
//                        // head plugin "registry"
//                        // -> allow logger to access for class name search
//
//                        for (x in xBegin..xEnd) {
//                            for (y in yBegin..yEnd) {
//                                for (z in zBegin..zEnd) {
//                                    val currentPosition = Vector(x, y, z)
//                                    Logger.debug("X $locX Y $locY Z $locZ")
//
//                                    val distance = midPoint.distance(currentPosition)
//                                    Logger.debug("Distance: $distance")
//                                    if (distance <= radius) {
//                                        world.getBlockAt(x, y, z).type = Material.STONE
//                                        Logger.debug("Set block! #${++blocksSet}")
//                                    }
//
//                                    Logger.debug("")
//                                }
//                            }
//                        }
//
//                        println("Placed blocks: $blocksSet")
                    }
                    else -> sender.sendMessage("Unknown!")
                }

            } else {
                Logger.debug("Command line command access for VoxelEdit is limited [Currently no commands implemented! :( ]")
            }

            return true
        } catch(e: Exception) {
            val msg = "Failed during command execution! [$e]"

            if(sender is Player) sender.sendMessage(msg) else Logger.warning(msg)
            return false
        }
    }

    fun setBlock(x: Double, y: Double, z: Double) {
        println("X $x Y $y Z $z")
        world!!.getBlockAt(x.toInt(), y.toInt(), z.toInt()).type = Material.STONE
    }

    fun DrawSphere(r: Double, lats: Int, longs: Int) {
        var i: Int = 0
        while (i <= lats) {
            val lat0 = Math.PI * (-0.5 + (i - 1).toDouble() / lats)
            val z0 = Math.sin(lat0)
            val zr0 = Math.cos(lat0)

            val lat1 = Math.PI * (-0.5 + i.toDouble() / lats)
            val z1 = Math.sin(lat1)
            val zr1 = Math.cos(lat1)

            var j: Int = 0
            while (j <= longs) {
                val lng = 2.0 * Math.PI * (j - 1).toDouble() / longs
                val x = Math.cos(lng)
                val y = Math.sin(lng)

                setBlock(x * zr0, y * zr0, z0)
                setBlock(x * zr1, y * zr1, z1)
                j++
            }
            i++
        }
    }
}