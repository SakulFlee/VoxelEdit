@file:Suppress("UNUSED")

package de.sakul6499.voxeledit.command

import de.framework.api.Bundle
import de.sakul6499.voxeledit.VEMode
import de.sakul6499.voxeledit.VETool
import de.sakul6499.voxeledit.clipboard.*
import de.sakul6499.voxeledit.task.*
import org.bukkit.Material
import org.bukkit.command.CommandSender
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

fun GetFullHelp(): String {
    var output = ""
    GetAllCommands().forEach { output += "$it\n" }
    return output
}

fun ValidateCommands(): Boolean {
    var success = true
    var labels = arrayOf<String>()
    GetAllCommands().forEach { cmd ->
        if (labels.any { label -> cmd.label.any { it == label } }) success = false

        labels += cmd.label
    }
    return success
}

object Commands {
    // Shapes
    val sphere = PlayerOnlyCommand(arrayOf("sphere", "s"), "sphere|s <radius> <blockName:blockDataIndex;...>", "Places blocks in a sphere by given") { args, player ->
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
        val radius = fetchInt(args[0], player)
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.isFlying = true
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials), player)
        }
    }

    val voxel = PlayerOnlyCommand(arrayOf("voxel", "v"), "voxel|v <radius> <blockName:blockDataIndex;...>", "Places blocks in a voxel (cube) by given radius") { args, player ->
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
        val radius = fetchInt(args[0], player)
        val materials = fetchMaterials(args[1], player)

        if (radius != null && materials != null) {
            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials), player)
        }
    }

    val cylinder = PlayerOnlyCommand(arrayOf("cylinder", "c"), "cylinder|c <radius> <up> (<down>) <blockName:blockDataIndex;...>", "Places blocks in cylinder by given up and down value") { args, player ->
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
        val radius = fetchInt(args[0], player)
        val up = fetchInt(args[1], player)
        var down = if (args.size > 3) fetchInt(args[2], player) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials), player)
        }
    }

    val tower = PlayerOnlyCommand(arrayOf("tower", "t"), "tower|t <radius> <up> (<down>) <blockName:blockDataIndex;...>", "Places a tower (voxel / cubic cylinder) by given up and down values") { args, player ->
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
        val radius = fetchInt(args[0], player)
        val up = fetchInt(args[1], player)
        var down = if (args.size > 3) fetchInt(args[2], player) else 0
        if (down == null) down = 0

        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)

        if (radius != null && up != null && materials != null) {
            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials), player)
        }
    }

//    // hollow shapes
//    val hollow_sphere = PlayerOnlyCommand(arrayOf("hollow_sphere", "hs")) { args, player ->
//        if (args.size <= 1) {
//            player.sendMessage("hollow_sphere|hs <radius> <blockName:blockDataIndex;...>")
//            return@PlayerOnlyCommand
//        }
//
//        val loc = player.location
//        val world = loc.world
//
//        val locX = loc.blockX
//        val locY = loc.blockY
//        val locZ = loc.blockZ
//
//        val midPoint = Vector(locX, locY, locZ)
//        val radius = fetchInt(args[0], player)
//        val materials = fetchMaterials(args[1], player)
//
//        if (radius != null && materials != null) {
//            player.isFlying = true
//            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
//            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials, true), player)
//        }
//    }
//    val hollow_voxel = PlayerOnlyCommand(arrayOf("hollow_voxel", "hv")) { args, player ->
//        if (args.size <= 1) {
//            player.sendMessage("hollow_voxel|hv <radius> <blockName:blockDataIndex;...>")
//            return@PlayerOnlyCommand
//        }
//
//        val loc = player.location
//        val world = loc.world
//
//        val locX = loc.blockX
//        val locY = loc.blockY
//        val locZ = loc.blockZ
//
//        val midPoint = Vector(locX, locY, locZ)
//        val radius = fetchInt(args[0], player)
//        val materials = fetchMaterials(args[1], player)
//
//        if (radius != null && materials != null) {
//            player.isFlying = true
//            player.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
//            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials, true), player)
//        }
//    }
//    val hollow_cylinder = PlayerOnlyCommand(arrayOf("hollow_cylinder", "hc")) { args, player ->
//        if (args.size <= 2) {
//            player.sendMessage("hollow_cylinder|hc <radius> <up> (<down>) <blockName:blockDataIndex;...>")
//            return@PlayerOnlyCommand
//        }
//
//        val loc = player.location
//        val world = loc.world
//
//        val locX = loc.blockX
//        val locY = loc.blockY
//        val locZ = loc.blockZ
//
//        val midPoint = Vector(locX, locY, locZ)
//        val radius = fetchInt(args[0], player)
//        val up = fetchInt(args[1], player)
//        var down = if (args.size > 3) fetchInt(args[2], player) else 0
//        if (down == null) down = 0
//
//        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)
//
//        if (radius != null && up != null && materials != null) {
//            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
//            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials, true), player)
//        }
//    }
//    val hollow_tower = PlayerOnlyCommand(arrayOf("hollow_tower", "ht")) { args, player ->
//        if (args.size <= 2) {
//            player.sendMessage("hollow_tower|ht <radius> <up> (<down>) <blockName:blockDataIndex;...>")
//            return@PlayerOnlyCommand
//        }
//
//        val loc = player.location
//        val world = loc.world
//
//        val locX = loc.blockX
//        val locY = loc.blockY
//        val locZ = loc.blockZ
//
//        val midPoint = Vector(locX, locY, locZ)
//        val radius = fetchInt(args[0], player)
//        val up = fetchInt(args[1], player)
//        var down = if (args.size > 3) fetchInt(args[2], player) else 0
//        if (down == null) down = 0
//
//        val materials = if (args.size > 4) fetchMaterials(args[3], player) else fetchMaterials(args[2], player)
//
//        if (radius != null && up != null && materials != null) {
//            player.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
//            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials, true), player)
//        }
//    }

    // Place
    val place = PlayerOnlyCommand(arrayOf("place", "p"), "place|p <blockName:blockDataIndex;...> ('hollow')", "Places blocks in the selected areal") { args, player ->
        if (args.isEmpty()) {
            player.sendMessage("place|p <blockName:blockDataIndex;...> ('hollow')")
            return@PlayerOnlyCommand
        }

        val materials = fetchMaterials(args[0], player)

        var hollow = false
        if (args.size > 1) {
            if (args[1].toLowerCase() == "hollow") {
                hollow = true
            } else {
                player.sendMessage("Didn't get '${args[2]}'!")
                player.sendMessage("place|p <blockName:blockDataIndex;...> ('hollow')")
                return@PlayerOnlyCommand
            }
        }

        if (materials != null) Clipboard.actionPlace(player, materials, hollow)
    }

    val replace = PlayerOnlyCommand(arrayOf("replace", "r"), "replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')", "Replaces all blocks in 'filter' to 'replace' in the selected areal") { args, player ->
        if (args.size <= 2) {
            player.sendMessage("replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')")
            return@PlayerOnlyCommand
        }

        val filter = fetchMaterials(args[0], player)
        val replace = fetchMaterials(args[1], player)

        var hollow = false
        if (args.size >= 3) {
            if (args[2] == "hollow") {
                hollow = true
            } else {
                player.sendMessage("Didn't get '${args[2]}'!")
                player.sendMessage("replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')")
                return@PlayerOnlyCommand
            }
        }

        if (filter != null && replace != null) Clipboard.actionReplace(player, filter, replace, hollow)
    }

    // Select - Basics
    val pos1 = PlayerOnlyCommand(arrayOf("pos1", "p1"), "pos1|p1", "Push's first position to Clipboard-Matrix [used for selection -> place, replace, ...]") { args, player ->
        player.sendMessage("Pushing position #1!")
        Clipboard.pushPosition(player, player.location.toVector(), null, player.location.world)
    }

    val pos2 = PlayerOnlyCommand(arrayOf("pos2", "p2"), "pos2|p2", "Push's second position to Clipboard-Matrix [used for selection -> place, replace, ...]") { args, player ->
        player.sendMessage("Pushing position #2!")
        Clipboard.pushPosition(player, null, player.location.toVector(), player.location.world)
    }

    // Select - Advanced
    val select = PlayerOnlyCommand(arrayOf("select", "se"),
            "select|se sphere|s <radius>\n" +
                    "select|se voxel|v <radius>\n" +
                    "select|se cylinder|c <radius> <up> (<down>)\n" +
                    "select|se tower|t <radius> <up> (<down>)\n" +
                    "select|se pos|p",
            "Select blocks by given shape and pushes them into the Clipboard-Matrix") { args, player ->
        if (args.isEmpty()) {
            player.sendMessage("No args!")
            player.sendMessage("select|se sphere|s <radius>")
            player.sendMessage("select|se voxel|v <radius>")
            player.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
            player.sendMessage("select|se tower|t <radius> <up> (<down>)")
            player.sendMessage("select|se pos|p")
            return@PlayerOnlyCommand
        }

        when (args[0]) {
            "sphere", "s" -> {
                if (args.size <= 1) {
                    player.sendMessage("select|se sphere|s <radius>")
                    return@PlayerOnlyCommand
                }

                val loc = player.location
                val world = loc.world

                val locX = loc.blockX
                val locY = loc.blockY
                val locZ = loc.blockZ

                val midPoint = Vector(locX, locY, locZ)
                val radius = fetchInt(args[1], player)

                if (radius != null) {
                    val selection = SphereSelection(world, midPoint, radius, false)
                    Clipboard.addSelection(selection, player)
                }
            }
            "voxel", "v" -> {
                if (args.size <= 1) {
                    player.sendMessage("select|se voxel|v <radius>")
                    return@PlayerOnlyCommand
                }

                val loc = player.location
                val world = loc.world

                val locX = loc.blockX
                val locY = loc.blockY
                val locZ = loc.blockZ

                val midPoint = Vector(locX, locY, locZ)
                val radius = fetchInt(args[1], player)

                if (radius != null) {
                    val selection = VoxelSelection(world, midPoint, radius, false)
                    Clipboard.addSelection(selection, player)
                }
            }
            "cylinder", "c" -> {
                if (args.size <= 2) {
                    player.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
                    return@PlayerOnlyCommand
                }

                val loc = player.location
                val world = loc.world

                val locX = loc.blockX
                val locY = loc.blockY
                val locZ = loc.blockZ

                val midPoint = Vector(locX, locY, locZ)
                val radius = fetchInt(args[1], player)
                val up = fetchInt(args[2], player)
                val down = if (args.size > 3) fetchInt(args[3], player) else 0

                if (radius != null && up != null && down != null) {
                    val selection = CylinderSelection(world, midPoint, radius, up, down, false)
                    Clipboard.addSelection(selection, player)
                }
            }
            "tower", "t" -> {
                if (args.size <= 2) {
                    player.sendMessage("select|se tower|t <radius> <up> (<down>)")
                    return@PlayerOnlyCommand
                }

                val loc = player.location
                val world = loc.world

                val locX = loc.blockX
                val locY = loc.blockY
                val locZ = loc.blockZ

                val midPoint = Vector(locX, locY, locZ)
                val radius = fetchInt(args[1], player)
                val up = fetchInt(args[2], player)
                val down = if (args.size > 3) fetchInt(args[3], player) else 0

                if (radius != null && up != null && down != null) {
                    val selection = TowerSelection(world, midPoint, radius, up, down, false)
                    Clipboard.addSelection(selection, player)
                }
            }
            "pos", "p" -> {
                val id = Clipboard.addPosSelection(player, false)
                if (id < 0) {
                    player.sendMessage("Could not start pos selective processing!")
                }
            }
            else -> {
                player.sendMessage("No args!")
                player.sendMessage("select|se sphere|s <radius>")
                player.sendMessage("select|se voxel|v <radius>")
                player.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
                player.sendMessage("select|se tower|t <radius> <up> (<down>)")
                player.sendMessage("select|se pos|p")
                return@PlayerOnlyCommand
            }
        }
    }

    // Clipboard III - Insert
    val insert = PlayerOnlyCommand(arrayOf("insert", "i"), "insert|i <selection ID>", "") { args, player ->
        if (args.isEmpty()) {
            player.sendMessage("insert|i <selection ID>")
            return@PlayerOnlyCommand
        }

        val selectionID = fetchInt(args[0], player)

        if (selectionID != null) Clipboard.addInsertion(selectionID, player)
    }

    // Tasks
    val tasks = Command(arrayOf("tasks", "ta"), "task|ta (<player>)", "List tasks for a specific player (or yourself)") { args, sender ->
        if (sender is Player) {
            if (args.isEmpty()) {
                sender.sendMessage("Tasks for @${sender.name}:")
                TaskHandler.getTasksForPlayer(sender).forEach {
                    sender.sendMessage(" -> $it")
                }
            } else {
                val name = args[0]
                try {
                    sender.sendMessage("Tasks for @$name:")
                    TaskHandler.getTasksForPlayerByName(name).forEach {
                        sender.sendMessage(" -> $it")
                    }
                } catch (e: Exception) {
                    sender.sendMessage("Unable to get tasks for player '$name'!")
                }
            }
        } else {
            // TODO: Implement task view via console (tasks <player>)
            sender.sendMessage("Not yet implemented! :(")
        }
    }

    val cancel = Command(arrayOf("cancel", "c"), "cancel|c (<player>) <task id>", "Cancels a task with a specific ID (for a specific player)") { args, sender ->
        if (sender is Player) {
            try {
                val id = if (args.isEmpty()) {
                    val tasks = TaskHandler.getTasksForPlayer(sender)
                    tasks[tasks.size - 1].id
                } else fetchInt(args[0], sender)

                // TODO: add player arg

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

    val undo = Command(arrayOf("undo", "u"), "undo|u (<player>) <ID>", "Undo a task with a specific ID (for a specific Player)") { args, sender ->
        if (sender is Player) {
            try {
                val id = if (args.isEmpty()) {
                    val tasks = TaskHandler.getTasksForPlayer(sender)
                    tasks[tasks.size - 1].id
                } else fetchInt(args[0], sender)

                if (id == null || id < 0) {
                    sender.sendMessage("The task id must be zero or greater!")
                    sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                    return@Command
                }

                TaskHandler.undoTask(id)
            } catch (e: NullPointerException) {
                sender.sendMessage("Either something critical just happened or you simply do not have any tasks queued yet, that we could undo!")
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
            // TODO: Implement task undo via console (undo (<player>) <id>)
            sender.sendMessage("Not yet implemented! :(")
        }
    }

    // Settings & Other
    val bps = Command(arrayOf("bps"), "bps (<bps>)", "Sets the BPS [ONLY USE IF YOU KNOW WHAT THIS DOES! MISUSE IS NOT A BUG!]") { args, sender ->
        if (args.isEmpty()) {
            sender.sendMessage("Current BPS: ${TaskHandler.BPS}")
            return@Command
        }

        try {
            val bps = fetchInt(args[0], sender)
            if (bps == null || bps <= 0) {
                sender.sendMessage("The BPS value must be a integer and not be lower or equal zero!")
                return@Command
            }

            TaskHandler.BPS = bps
        } catch (e: NumberFormatException) {
            sender.sendMessage("The BPS value must be a number!")
            return@Command
        }
    }

    val mode = PlayerOnlyCommand(arrayOf("mode", "m"), "mode", "Enters the VE-Mode [Is a try out totally worth it ;)]") { args, player ->
        VEMode.handlePlayer(player)
    }

    val tool = PlayerOnlyCommand(arrayOf("tool", "to"), "tool|to (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)", "Binds a command to an item, which then will be executed each time you click right or left. [For VoxelEdit commands the /# (or any other) can be left out!]") { args, player ->
        if (args.size <= 2) {
            player.sendMessage("tool|to (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)")
            return@PlayerOnlyCommand
        }

        val bind: Boolean = when {
            args[0] == "bind" || args[0] == "b" -> true
            args[0] == "unbind" || args[0] == "u" -> false
            else -> {
                player.sendMessage("tool|to (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)")
                return@PlayerOnlyCommand
            }
        }

        when (args[1].toLowerCase()) {
            "left", "l" -> {
                if (bind) {
                    if (args.size < 2) {
                        player.sendMessage("You need to provide a command!")
                        player.sendMessage("tool|to (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)")
                        return@PlayerOnlyCommand
                    }

                    var s = ""
                    for (a in 2 until args.size) s += "${args[a]} "

                    VETool.bindToolToPlayer(player, s, null)
                } else {
                    VETool.unbindToolFromPlayer(player, true, false)
                }
            }
            "right", "r" -> {
                if (bind) {
                    if (args.size < 2) {
                        player.sendMessage("You need to provide a command!")
                        player.sendMessage("tool|to (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)")
                        return@PlayerOnlyCommand
                    }

                    var s = ""
                    for (a in 2 until args.size) s += "${args[a]} "

                    VETool.bindToolToPlayer(player, null, s)
                } else {
                    VETool.unbindToolFromPlayer(player, false, true)
                }
            }
            else -> {
                player.sendMessage("tool (bind|b)|(unbind|u) (left|l)|(right|r) (<command>)")
                return@PlayerOnlyCommand
            }
        }
    }
}

private fun fetchInt(l: String, sender: CommandSender? = null, checkBoundary: Boolean = true): Int? = try {
    val i = Integer.parseInt(l)

    if (checkBoundary && i < 0) {
        sender?.sendMessage("Number must be greater or equal zero!")
        null
    } else i
} catch (e: NumberFormatException) {
    sender?.sendMessage("Invalid number!")
    null
}


private fun fetchMaterials(l: String, sender: CommandSender? = null): Array<Bundle<Material, Byte>>? {
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
                        sender?.sendMessage("The block data index must not be lower than zero! [Setting to zero for '$it']")
                        i = 0
                    }

                    i.toByte()
                } catch (e: NumberFormatException) {
                    sender?.sendMessage("The block data index must be a number! [Setting to zero for '$it']")

                    0.toByte()
                }
                s = s.substring(0, index)
            }

            try {
                material = Material.matchMaterial(s)
                output += Bundle(material, mod)
            } catch (e: IllegalStateException) {
                sender?.sendMessage("Malformed material name!")

                return null
            }
        }
    } catch (e: NullPointerException) {
        sender?.sendMessage("Materials malformed!")
        return null
    }

    return if (output.isEmpty()) {
        sender?.sendMessage("Materials must not be empty!")
        null
    } else output
}