@file:Suppress("UNUSED")

package de.sakul6499.voxeledit.command

import de.framework.api.Bundle
import de.sakul6499.voxeledit.VEMode
import de.sakul6499.voxeledit.task.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.reflect.full.memberProperties

fun GetCommand(label: String): Command = GetCommandOrNull(label) ?: throw NullPointerException("Command '$label' not found!")
fun GetCommandOrNull(label: String): Command? = GetAllCommands().find { it.label.any { it == label.toLowerCase() } }

fun GetAllCommands(): Array<Command> {
    var output: Array<Command> = arrayOf()
    Commands::class.memberProperties.forEach {
        val prop = it.get(Commands)
        if (prop is Command) {
            output += prop
        }
    }
    return output
}

object Commands {
    // Shapes
    val sphere = PlayerOnlyCommand(arrayOf("sphere", "s")) { args, player ->
        if (args.size <= 1) {
            player.sendMessage("sphere|s <radius> <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.isFlying = true
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials), player)
        }
    }

    val voxel = PlayerOnlyCommand(arrayOf("voxel", "v")) { args, player ->
        if (args.size <= 1) {
            player.sendMessage("voxel|v <radius> <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials), player)
        }
    }

    val cylinder = PlayerOnlyCommand(arrayOf("cylinder", "c")) { args, player ->
        if (args.size <= 2) {
            player.sendMessage("cylinder|c <radius> <up> (<down>) <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val up = fetchInt(args[1])
        var down = if (args.size > 3) fetchInt(args[2]) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials), player)
        }
    }

    val tower = PlayerOnlyCommand(arrayOf("tower", "t")) { args, player ->
        if (args.size <= 2) {
            player.sendMessage("tower|t <radius> <up> (<down>) <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val up = fetchInt(args[1])
        var down = if (args.size > 3) fetchInt(args[2]) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials), player)
        }
    }

    // hollow shapes
    val hollow_sphere = PlayerOnlyCommand(arrayOf("hollow_sphere", "hs")) { args, player ->
        if (args.size <= 1) {
            player.sendMessage("hollow_sphere|hs <radius> <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.isFlying = true
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials, true), player)
        }
    }
    val hollow_voxel = PlayerOnlyCommand(arrayOf("hollow_voxel", "hv")) { args, player ->
        if (args.size <= 1) {
            player.sendMessage("hollow_voxel|hv <radius> <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.isFlying = true
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials, true), player)
        }
    }
    val hollow_cylinder = PlayerOnlyCommand(arrayOf("hollow_cylinder", "hc")) { args, player ->
        if (args.size <= 2) {
            player.sendMessage("hollow_cylinder|hc <radius> <up> (<down>) <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val up = fetchInt(args[1])
        var down = if (args.size > 3) fetchInt(args[2]) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials, true), player)
        }
    }
    val hollow_tower = PlayerOnlyCommand(arrayOf("hollow_tower", "ht")) { args, player ->
        if (args.size <= 2) {
            player.sendMessage("hollow_tower|ht <radius> <up> (<down>) <blockName:blockDataIndex;...>")
            return@PlayerOnlyCommand
        }

        val loc = player.location
        val world = loc.world

        val locX = loc.blockX
        val locY = loc.blockY
        val locZ = loc.blockZ

        val midPoint = Vector(locX, locY, locZ)
        val radius = fetchInt(args[0])
        val up = fetchInt(args[1])
        var down = if (args.size > 3) fetchInt(args[2]) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials, true), player)
        }
    }

    // Clipboard I - Basics


    // Clipboard II
    // Clipboard III

    // TODO: Copy rest

    // Settings & Other
    val cancel = Command(arrayOf("cancel")) { args, sender ->
        if (sender is Player) {
            try {
                val id = if (args.isEmpty()) {
                    val tasks = TaskHandler.getTasksForPlayer(sender)
                    tasks[tasks.size - 1].id
                } else fetchInt(args[0])

                if (id == null || id < 0) {
                    sender.sendMessage("The task id must be zero or greater!")
                    sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                    return@Command
                }

                TaskHandler.cancelTask(id)
            } catch (e: NullPointerException) {
                sender.sendMessage("Either something critical just happened or you simply do not have any tasks queued yet, that we could cancel!")
                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                return@Command
            } catch (e: IndexOutOfBoundsException) {
                sender.sendMessage("A task with the id '${args[1]}' couldn't be found!")
                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                return@Command
            } catch (e: NumberFormatException) {
                sender.sendMessage("The task id must be a number!")
                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                return@Command
            }
        } else {
            // TODO: Implement task cancellation via console (tasks (<player>) <id>)
            sender.sendMessage("Not yet implemented! :(")
        }
    }

    val mode = PlayerOnlyCommand(arrayOf("mode", "m")) { args, player ->
        VEMode.handlePlayer(player)
    }
}

private fun fetchInt(l: String): Int? = try {
    Integer.parseInt(l)
} catch (e: NumberFormatException) {
    null
}


private fun fetchMaterials(l: String, player: Player?): Array<Bundle<Material, Byte>>? {
    var output: Array<Bundle<Material, Byte>> = arrayOf()

    try {
        val literalMaterials = l.split(";")
        literalMaterials.forEach {
            var s = it

            val material: Material
            var mod: Byte = -1

            val index = s.indexOf(':')
            if (index > 0) {
                mod = try {
                    var i = Integer.parseInt(s.substring(index + 1, s.length))
                    if (i < 0) {
                        player?.sendMessage("The block data index must not be lower than zero! [Setting to zero for '$it']")
                        i = 0
                    }

                    i.toByte()
                } catch (e: NumberFormatException) {
                    player?.sendMessage("The block data index must be a number! [Setting to zero for '$it']")

                    0.toByte()
                }
                s = s.substring(0, index)
            }

            try {
                material = Material.matchMaterial(s)
                output += Bundle(material, mod)
            } catch (e: IllegalStateException) {
                player?.sendMessage("Malformed material name!")

                return null
            }
        }
    } catch (e: NullPointerException) {
        player?.sendMessage("Materials malformed!")
        return null
    }

    return if (output.isEmpty()) {
        player?.sendMessage("Materials must not be empty!")
        null
    } else output
}