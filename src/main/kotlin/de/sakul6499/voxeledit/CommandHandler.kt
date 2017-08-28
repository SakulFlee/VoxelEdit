package de.sakul6499.voxeledit

import de.framework.api.Bundle
import de.framework.logger.Logger
import de.sakul6499.voxeledit.clipboard.*
import de.sakul6499.voxeledit.task.*
import org.bukkit.Bukkit
import org.bukkit.Material
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

    override fun execute(sender: CommandSender?, commandLabel: String?, args: Array<out String>?): Boolean {
        Bukkit.getServer().scheduler.runTaskAsynchronously(VoxelEdit.javaPlugin, {
            try {
                if (sender is Player) {
                    if (args == null || args.isEmpty() || args[0].equals("help", true) || args[0] == "?") {
                        sender.sendMessage("VoxelEdit by @Sakul6499 | Lukas!")
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

                        sender.sendMessage("CLIPBOARD - MANUAL")
                        sender.sendMessage("undo (<id>)")
                        sender.sendMessage("cancel (<id>)")
                        sender.sendMessage("pos1|p1")
                        sender.sendMessage("pos2|p2")
                        sender.sendMessage("place|p <blockName:blockDataIndex;...> ('hollow')")
                        sender.sendMessage("replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')")

                        sender.sendMessage("CLIPBOARD - SELECTOR")
                        sender.sendMessage("...")

                        sender.sendMessage("CLIPBOARD - OPERATIONS")
                        sender.sendMessage("...")

                        sender.sendMessage("VE-Tool")
                        sender.sendMessage("tool (bind|b)|(unbind|u) (left|l)|(right|r) <command>")

                        sender.sendMessage("SETTINGS")
                        sender.sendMessage("bps <BPS>")
                        sender.sendMessage("mode [enter VE-Mode]")

                        return@runTaskAsynchronously
                    }

                    when (args[0].toLowerCase()) {
                    /* Forms */
                        "sphere", "s" -> {
                            if (args.size <= 2) {
                                sender.sendMessage("sphere|s <radius> <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val materials = try {
                                fetchMaterials(args[2], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials), sender)
                        }
                        "voxel", "v" -> {
                            if (args.size <= 2) {
                                sender.sendMessage("voxel|v <radius> <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val materials = try {
                                fetchMaterials(args[2], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials), sender)
                        }
                        "cylinder", "c" -> {
                            if (args.size <= 3) {
                                sender.sendMessage("cylinder|c <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val up: Int = try {
                                Integer.parseInt(args[2])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            }
                            val down: Int = if (args.size > 4) try {
                                Integer.parseInt(args[1])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            } else 0
                            val materials = try {
                                if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials), sender)
                        }
                        "tower", "t" -> {
                            if (args.size <= 3) {
                                sender.sendMessage("tower|t <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val up: Int = try {
                                Integer.parseInt(args[2])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            }
                            val down: Int = if (args.size > 4) try {
                                Integer.parseInt(args[1])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            } else 0
                            val materials = try {
                                if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials), sender)
                        }
                    /* Hollow */
                        "hollow_sphere", "hs" -> {
                            if (args.size <= 2) {
                                sender.sendMessage("hollow_sphere|hs <radius> <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val materials = try {
                                fetchMaterials(args[2], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                            TaskHandler.addTask(SphereTask(world, midPoint, radius, materials, true), sender)
                        }
                        "hollow_voxel", "hv" -> {
                            if (args.size <= 2) {
                                sender.sendMessage("hollow_voxel|hv <radius> <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val materials = try {
                                fetchMaterials(args[2], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, radius.toDouble() + 1, 0.0))
                            TaskHandler.addTask(VoxelTask(world, midPoint, radius, materials, true), sender)
                        }
                        "hollow_cylinder", "hc" -> {
                            if (args.size <= 3) {
                                sender.sendMessage("hollow_cylinder|hc <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val up: Int = try {
                                Integer.parseInt(args[2])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            }
                            val down: Int = if (args.size > 4) try {
                                Integer.parseInt(args[1])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            } else 0
                            val materials = try {
                                if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                            TaskHandler.addTask(CylinderTask(world, midPoint, radius, up, down, materials, true), sender)
                        }
                        "hollow_tower", "ht" -> {
                            if (args.size <= 3) {
                                sender.sendMessage("hollow_tower|ht <radius> <up> (<down>) <blockName:blockDataIndex;...>")
                                return@runTaskAsynchronously
                            }

                            val loc = sender.location
                            val world = loc.world

                            val locX = loc.blockX
                            val locY = loc.blockY
                            val locZ = loc.blockZ

                            val midPoint = Vector(locX, locY, locZ)
                            val radius: Int = try {
                                val i = Integer.parseInt(args[1]); if (i <= 0) {
                                    sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                            }
                            val up: Int = try {
                                Integer.parseInt(args[2])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            }
                            val down: Int = if (args.size > 4) try {
                                Integer.parseInt(args[1])
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                            } else 0
                            val materials = try {
                                if (args.size > 4) fetchMaterials(args[4], sender) else fetchMaterials(args[3], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            sender.teleport(loc.add(0.0, up.toDouble() + 1, 0.0))
                            TaskHandler.addTask(TowerTask(world, midPoint, radius, up, down, materials, true), sender)
                        }
                    /* Clipboard - Manual */
                        "undo" -> {
                            try {
                                val id: Int = if (args.size <= 1) {
                                    val tasks = TaskHandler.getTasksForPlayer(sender)
                                    tasks[tasks.size - 1].id
                                } else Integer.parseInt(args[1])
                                if (id < 0) {
                                    sender.sendMessage("The task id must be zero or greater!")
                                    sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                    return@runTaskAsynchronously
                                }

                                TaskHandler.undoTask(id)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Either something critical just happened or you simply do not have any tasks queued yet, that we could undo!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            } catch (e: IndexOutOfBoundsException) {
                                sender.sendMessage("A task with the id '${args[1]}' couldn't be found!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("The task id must be a number!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            }
                        }
                        "cancel" -> {
                            try {
                                val id: Int = if (args.size <= 1) {
                                    val tasks = TaskHandler.getTasksForPlayer(sender)
                                    tasks[tasks.size - 1].id
                                } else Integer.parseInt(args[1])
                                if (id < 0) {
                                    sender.sendMessage("The task id must be zero or greater!")
                                    sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                    return@runTaskAsynchronously
                                }

                                TaskHandler.cancelTask(id)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Either something critical just happened or you simply do not have any tasks queued yet, that we could cancel!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            } catch (e: IndexOutOfBoundsException) {
                                sender.sendMessage("A task with the id '${args[1]}' couldn't be found!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("The task id must be a number!")
                                sender.sendMessage("Tip: use 'tasks' to view your tasks!")
                                return@runTaskAsynchronously
                            }
                        }
                        "pos1", "p1" -> {
                            sender.sendMessage("Pushing position!")
                            Clipboard.pushPosition(sender, sender.location.toVector(), null, sender.location.world)
                        }
                        "pos2", "p2" -> {
                            sender.sendMessage("Pushing position!")
                            Clipboard.pushPosition(sender, null, sender.location.toVector(), sender.location.world)
                        }
                        "place", "p" -> {
                            if (args.size <= 1) {
                                sender.sendMessage("place|p <blockName:blockDataIndex;...> ('hollow')")
                                return@runTaskAsynchronously
                            }

                            val materials = try {
                                fetchMaterials(args[1], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Materials malformed!"); return@runTaskAsynchronously
                            }
                            if (materials.isEmpty()) {
                                sender.sendMessage("Materials must not be empty!"); return@runTaskAsynchronously
                            }

                            var hollow = false
                            if (args.size >= 3) {
                                if (args[2] == "hollow") {
                                    hollow = true
                                } else {
                                    sender.sendMessage("Didn't get '${args[2]}'!")
                                    sender.sendMessage("place|p <blockName:blockDataIndex;...> ('hollow')")
                                    return@runTaskAsynchronously
                                }
                            }

                            Clipboard.actionPlace(sender, materials, hollow)
                        }
                        "replace", "r" -> {
                            if (args.size <= 2) {
                                sender.sendMessage("replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')")
                                return@runTaskAsynchronously
                            }

                            val filter = try {
                                fetchMaterials(args[1], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Filter materials malformed!"); return@runTaskAsynchronously
                            }
                            if (filter.isEmpty()) {
                                sender.sendMessage("Filter materials must not be empty!"); return@runTaskAsynchronously
                            }

                            val replace = try {
                                fetchMaterials(args[2], sender)
                            } catch (e: NullPointerException) {
                                sender.sendMessage("Replace materials malformed!"); return@runTaskAsynchronously
                            }
                            if (replace.isEmpty()) {
                                sender.sendMessage("Replace materials must not be empty!"); return@runTaskAsynchronously
                            }

                            var hollow = false
                            if (args.size >= 4) {
                                if (args[3] == "hollow") {
                                    hollow = true
                                } else {
                                    sender.sendMessage("Didn't get '${args[3]}'!")
                                    sender.sendMessage("replace|r <filter -> blockName:blockDataIndex;...> <replace -> blockName:blockDataIndex;...> ('hollow')")
                                    return@runTaskAsynchronously
                                }
                            }

                            Clipboard.actionReplace(sender, filter, replace, hollow)
                        }
                    /* Clipboard - Select */
                        "select", "se" -> {
                            if (args.size <= 1) {
                                sender.sendMessage("[select|se options]:")
                                sender.sendMessage("select|se sphere|s radius")
                                sender.sendMessage("select|se voxel|v radius")
                                sender.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
                                sender.sendMessage("select|se tower|t <radius> <up> (<down>)")
                                sender.sendMessage("select|se pos|p")
                                return@runTaskAsynchronously
                            }

                            when (args[1]) {
                                "sphere", "s" -> {
                                    if (args.size <= 2) {
                                        sender.sendMessage("select|se sphere|s radius")
                                        return@runTaskAsynchronously
                                    }

                                    val loc = sender.location
                                    val world = loc.world

                                    val locX = loc.blockX
                                    val locY = loc.blockY
                                    val locZ = loc.blockZ

                                    val midPoint = Vector(locX, locY, locZ)
                                    val radius: Int = try {
                                        val i = Integer.parseInt(args[2]); if (i <= 0) {
                                            sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                        } else i
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                                    }

                                    val selection = SphereSelection(world, midPoint, radius, false)
                                    Clipboard.addSelection(selection, sender)
                                }
                                "voxel", "v" -> {
                                    if (args.size <= 2) {
                                        sender.sendMessage("select|se voxel|v radius")
                                        return@runTaskAsynchronously
                                    }

                                    val loc = sender.location
                                    val world = loc.world

                                    val locX = loc.blockX
                                    val locY = loc.blockY
                                    val locZ = loc.blockZ

                                    val midPoint = Vector(locX, locY, locZ)
                                    val radius: Int = try {
                                        val i = Integer.parseInt(args[2]); if (i <= 0) {
                                            sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                        } else i
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                                    }

                                    val selection = VoxelSelection(world, midPoint, radius, false)
                                    Clipboard.addSelection(selection, sender)
                                }
                                "cylinder", "c" -> {
                                    if (args.size <= 3) {
                                        sender.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
                                        return@runTaskAsynchronously
                                    }

                                    val loc = sender.location
                                    val world = loc.world

                                    val locX = loc.blockX
                                    val locY = loc.blockY
                                    val locZ = loc.blockZ

                                    val midPoint = Vector(locX, locY, locZ)
                                    val radius: Int = try {
                                        val i = Integer.parseInt(args[2]); if (i <= 0) {
                                            sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                        } else i
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                                    }
                                    val up: Int = try {
                                        Integer.parseInt(args[3])
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                                    }
                                    val down: Int = if (args.size > 5) try {
                                        Integer.parseInt(args[4])
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                                    } else 0

                                    val selection = CylinderSelection(world, midPoint, radius, up, down, false)
                                    Clipboard.addSelection(selection, sender)
                                }
                                "tower", "t" -> {
                                    if (args.size <= 3) {
                                        sender.sendMessage("select|se tower|t <radius> <up> (<down>)")
                                        return@runTaskAsynchronously
                                    }

                                    val loc = sender.location
                                    val world = loc.world

                                    val locX = loc.blockX
                                    val locY = loc.blockY
                                    val locZ = loc.blockZ

                                    val midPoint = Vector(locX, locY, locZ)
                                    val radius: Int = try {
                                        val i = Integer.parseInt(args[2]); if (i <= 0) {
                                            sender.sendMessage("Radius must be greater than zero!"); return@runTaskAsynchronously
                                        } else i
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Radius must be a number!"); return@runTaskAsynchronously
                                    }
                                    val up: Int = try {
                                        Integer.parseInt(args[3])
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                                    }
                                    val down: Int = if (args.size > 5) try {
                                        Integer.parseInt(args[4])
                                    } catch (e: NumberFormatException) {
                                        sender.sendMessage("Up must be a number!"); return@runTaskAsynchronously
                                    } else 0

                                    val selection = TowerSelection(world, midPoint, radius, up, down, false)
                                    Clipboard.addSelection(selection, sender)
                                }
                                "pos", "p" -> {

                                }
                                else -> {
                                    sender.sendMessage("[select|se options]:")
                                    sender.sendMessage("select|se sphere|s radius")
                                    sender.sendMessage("select|se voxel|v radius")
                                    sender.sendMessage("select|se cylinder|c <radius> <up> (<down>)")
                                    sender.sendMessage("select|se tower|t <radius> <up> (<down>)")
                                    sender.sendMessage("select|se pos|p")
                                    return@runTaskAsynchronously
                                }
                            }
                        }
                        "insert", "i" -> {
                            if (args.size <= 1) {
                                sender.sendMessage("insert|i <selection ID>")
                                return@runTaskAsynchronously
                            }

                            val selectionID: Int = try {
                                val i = Integer.parseInt(args[1])
                                if (i <= 0) {
                                    sender.sendMessage("Selection ID must be greater than zero!")
                                    return@runTaskAsynchronously
                                } else i
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("Selection ID must be a number!")
                                return@runTaskAsynchronously
                            }

                            Clipboard.addInsertion(selectionID, sender)
                        }
                    /* VE-Tool */
                        "tool" -> {
                            if (args.size <= 3) {
                                sender.sendMessage("tool (bind|b)|(unbind|u) (left|l)|(right|r) <command>")
                                return@runTaskAsynchronously
                            }

                            val bind: Boolean = when {
                                args[1] == "bind" || args[1] == "b" -> true
                                args[1] == "unbind" || args[1] == "u" -> false
                                else -> {
                                    sender.sendMessage("tool (bind|b)|(unbind|u) (left|l)|(right|r) <command>")
                                    return@runTaskAsynchronously
                                }
                            }

                            when (args[2].toLowerCase()) {
                                "left", "l" -> {
                                    var s = ""
                                    for (a in 3 until args.size) s += "${args[a]} "

                                    if (bind) {
                                        VETool.bindToolToPlayer(sender, s, null)
                                    } else {
                                        VETool.unbindToolFromPlayer(sender, true, false)
                                    }
                                }
                                "right", "r" -> {
                                    var s = ""
                                    for (a in 3 until args.size) s += "${args[a]} "

                                    if (bind) {
                                        VETool.bindToolToPlayer(sender, null, s)
                                    } else {
                                        VETool.unbindToolFromPlayer(sender, false, true)
                                    }
                                }
                                else -> {
                                    sender.sendMessage("tool (bind|b)|(unbind|u) (left|l)|(right|r) <command>")
                                    return@runTaskAsynchronously
                                }
                            }
                        }
                    /* Settings */
                        "bps" -> {
                            if (args.size <= 1) {
                                sender.sendMessage("Current BPS: ${TaskHandler.BPS}")
                                return@runTaskAsynchronously
                            }

                            try {
                                val bps = parse(args[1], sender)
                                if (bps <= 0) {
                                    sender.sendMessage("The BPS value must not be lower or equal zero!")
                                    return@runTaskAsynchronously
                                }

                                TaskHandler.BPS = bps
                            } catch (e: NumberFormatException) {
                                sender.sendMessage("The BPS value must be a number!")
                                return@runTaskAsynchronously
                            }
                        }
                        "tasks" -> {
                            if (args.size <= 1) {
                                sender.sendMessage("Tasks for @${sender.name}:")
                                TaskHandler.getTasksForPlayer(sender).forEach {
                                    sender.sendMessage(" -> $it")
                                }

                                return@runTaskAsynchronously
                            }

                            for (i in 1 until args.size) {
                                try {
                                    sender.sendMessage("Tasks for @${args[i]}:")
                                    TaskHandler.getTasksForPlayerByName(args[i]).forEach {
                                        sender.sendMessage(" -> $it")
                                    }
                                } catch (e: Exception) {
                                    sender.sendMessage("Unable to get tasks for player '${args[i]}'!")
                                }
                            }
                        }
                        "mode" -> {
                            VEMode.handlePlayer(sender)
                        }
                        else -> sender.sendMessage("Unknown command!")
                    }

                } else {
                    Logger.debug("Command line command access for VoxelEdit is limited [Currently no commands implemented! :( ]")
                }

                return@runTaskAsynchronously
            } catch (e: Exception) {
                val msg = "Failed during command execution! [$e]"

                if (sender is Player) sender.sendMessage(msg) else Logger.warning(msg)
                Logger.exception(e)
                return@runTaskAsynchronously
            }
        })

        return true
    }

    private fun _parse(input: String, assert: Boolean = true): Int {
        val output = Integer.parseInt(input)
        if (assert) if (output <= 0) throw AssertionError("$output is lower or equal zero!")

        return output
    }

    private fun parse(input: String, player: Player, assert: Boolean = true): Int {
        try {
            return _parse(input, assert)
        } catch (e: NumberFormatException) {
            player.sendMessage("'$input' is not a number!")
        } catch (e: AssertionError) {
            player.sendMessage(e.message)
        }

        return 0
    }

    private fun fetchMaterials(input: String, player: Player): Array<Bundle<Material, Byte>> {
        var output: Array<Bundle<Material, Byte>> = arrayOf()

        val literalMaterials = input.split(";")
        literalMaterials.forEach {
            var s = it

            val material: Material
            var mod: Byte = -1

            val index = s.indexOf(':')
            if (index > 0) {
                mod = try {
                    var i = Integer.parseInt(s.substring(index + 1, s.length))
                    if (i < 0) {
                        player.sendMessage("The block data index must not be lower than zero! [Setting to zero for '$it']")
                        i = 0
                    }

                    i.toByte()
                } catch (e: NumberFormatException) {
                    player.sendMessage("The block data index must be a number! [Setting to zero for '$it']")

                    0.toByte()
                }
                s = s.substring(0, index)
            }

            try {
                material = Material.matchMaterial(s)
                output += Bundle(material, mod)
            } catch (e: IllegalStateException) {
                player.sendMessage("Malformed material name!")

                return arrayOf()
            }
        }

        return output
    }
}