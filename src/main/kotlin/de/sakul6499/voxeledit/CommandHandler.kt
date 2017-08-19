package de.sakul6499.voxeledit

import de.framework.logger.Logger
import de.sakul6499.voxeledit.task.*
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class CommandHandler : BukkitCommand("voxeledit") {
    init {
        description = "Voxel Edit!"
        usageMessage = "/voxeledit | /ve | /# | //voxeledit | //ve | //# [use 'help' for more!]"
        permission = "voxeledit"
        aliases = listOf("ve", "/voxeledit", "/ve", "#", "/#")
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

                if (args == null || args.isEmpty() || args[0].equals("help", true) || args[0] == "?") {
                    sender.sendMessage("Voxel Edit Commands:")

                    sender.sendMessage("FORMS")
                    sender.sendMessage("sphere|s <radius> <blockName:blockDataIndex;...>")
                    sender.sendMessage("voxel|v <radius> <blockName:blockDataIndex;...>")
                    sender.sendMessage("cylinder|c <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                    sender.sendMessage("tower|t <radius> <up> (<down>) <blockName:blockDataIndex;...>")

                    sender.sendMessage("HOLLOW")
                    sender.sendMessage("hollow_sphere|hs <radius> <blockName:blockDataIndex;...>")
                    sender.sendMessage("hollow_voxel|hv <radius> <blockName:blockDataIndex;...>")
                    sender.sendMessage("hollow_cylinder|hc <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                    sender.sendMessage("hollow_tower|ht <radius> <up> (<down>) <blockName:blockDataIndex;...>")

                    sender.sendMessage("MANUAL")
                    sender.sendMessage("...")

                    sender.sendMessage("CLIPBOARD")
                    sender.sendMessage("...")

                    sender.sendMessage("SETTINGS")
                    sender.sendMessage("bps <BPS>")

                    return true
                }

                when (args[0].toLowerCase()) {
                /* Forms */
                    "sphere", "s" -> {
                        if (args.size <= 2) {
                            sender.sendMessage("sphere|s <radius> <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val materials = fetchMaterials(args[2], sender)

                        if (radius <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                        TaskHandler.addTask(SphereTask(world.uid, midPoint, radius, materials), sender)
                    }
                    "voxel", "v" -> {
                        if (args.size <= 2) {
                            sender.sendMessage("voxel|v <radius> <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val materials = fetchMaterials(args[2], sender)

                        if (radius <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                        TaskHandler.addTask(VoxelTask(world.uid, midPoint, radius, materials), sender)
                    }
                    "cylinder", "c" -> {
                        if (args.size <= 3) {
                            sender.sendMessage("cylinder|c <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val up = parse(args[2], sender)
                        val down = if (args.size > 4) parse(args[3], sender) else 0
                        val materials = if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)

                        if (radius <= 0 || up <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                        TaskHandler.addTask(CylinderTask(world.uid, midPoint, radius, up, down, materials), sender)
                    }
                    "tower", "t" -> {
                        if (args.size <= 3) {
                            sender.sendMessage("tower|t <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val up = parse(args[2], sender)
                        val down = if (args.size > 4) parse(args[3], sender) else 0
                        val materials = if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)

                        if (radius <= 0 || up <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                        TaskHandler.addTask(TowerTask(world.uid, midPoint, radius, up, down, materials), sender)
                    }
                /* Hollow */
                    "hollow_sphere", "hs" -> {
                        if (args.size <= 2) {
                            sender.sendMessage("hollow_sphere|hs <radius> <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val materials = fetchMaterials(args[2], sender)

                        if (radius <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                        TaskHandler.addTask(SphereTask(world.uid, midPoint, radius, materials, true), sender)
                    }
                    "hollow_voxel", "hv" -> {
                        if (args.size <= 2) {
                            sender.sendMessage("hollow_voxel|hv <radius> <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val materials = fetchMaterials(args[2], sender)

                        if (radius <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                        TaskHandler.addTask(VoxelTask(world.uid, midPoint, radius, materials, true), sender)
                    }
                    "hollow_cylinder", "hc" -> {
                        if (args.size <= 3) {
                            sender.sendMessage("hollow_cylinder|hc <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val up = parse(args[2], sender)
                        val down = if (args.size > 4) parse(args[3], sender) else 0
                        val materials = if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)

                        if (radius <= 0 || up <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                        TaskHandler.addTask(CylinderTask(world.uid, midPoint, radius, up, down, materials, true), sender)
                    }
                    "hollow_tower", "ht" -> {
                        if (args.size <= 3) {
                            sender.sendMessage("hollow_tower|ht <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                            return true
                        }

                        val loc = sender.location
                        val world = loc.world

                        val locX = loc.blockX
                        val locY = loc.blockY
                        val locZ = loc.blockZ

                        val midPoint = Vector(locX, locY, locZ)
                        val radius = parse(args[1], sender)
                        val up = parse(args[2], sender)
                        val down = if (args.size > 4) parse(args[3], sender) else 0
                        val materials = if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)

                        if (radius <= 0 || up <= 0 || materials.isEmpty()) return true

                        sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                        TaskHandler.addTask(TowerTask(world.uid, midPoint, radius, up, down, materials, true), sender)
                    }
                /* Manual */
                /* Clipboard */
                // general
                // todo set

                // clipboard
                // todo copy
                // todo cut
                // todo paste
                    "bps" -> {
                        if (args.size <= 1) {
                            sender.sendMessage("bps <BPS>")
                            sender.sendMessage("Current BPS: ${TaskHandler.BPS}")
                            return true
                        }

                        val bps = parse(args[1], sender)
                        TaskHandler.BPS = bps
                    }
                    else -> sender.sendMessage("Unknown!")
                }

            } else {
                Logger.debug("Command line command access for VoxelEdit is limited [Currently no commands implemented! :( ]")
            }

            return true
        } catch (e: Exception) {
            val msg = "Failed during command execution! [$e]"

            if (sender is Player) sender.sendMessage(msg) else Logger.warning(msg)
            Logger.exception(e)
            return false
        }
    }

    private fun _parse(input: String, assert: Boolean = true): Int {
        val output = Integer.parseInt(input)
        if (assert) if (output <= 0) throw AssertionError("$output is lower or equal zero!")

        return output
    }

    private fun parse(input: String, player: Player, assert: Boolean = false): Int {
        try {
            return _parse(input, assert)
        } catch (e: NumberFormatException) {
            player.sendMessage("'$input' is not a number!")
        } catch (e: AssertionError) {
            player.sendMessage(e.message)
        }

        return 0
    }

    private fun fetchMaterials(input: String, player: Player): Array<Triple<Material, Byte, Boolean>> {
        var output: Array<Triple<Material, Byte, Boolean>> = arrayOf()

        val literalMaterials = input.split(";")
        literalMaterials.forEach {
            var s = it

            val material: Material
            var mod: Byte = 0
            var physics = true

            if (s.endsWith('!')) {
                physics = false
                s = s.substring(0, s.length + 1)
            }

            val index = s.indexOf(':')
            if (index > 0) {
                mod = parse(s.substring(index + 1, s.length), player).toByte()
                s = s.substring(0, index)
            }

            material = Material.matchMaterial(s)
            output += Triple(material, mod, physics)
        }

        return output
    }
}