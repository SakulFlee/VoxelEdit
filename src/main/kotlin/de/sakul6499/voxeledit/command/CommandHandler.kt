package de.sakul6499.voxeledit.command

import de.framework.api.Bundle
import de.sakul6499.voxeledit.VoxelEdit
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player

class CommandHandler : BukkitCommand("voxeledit") {
    init {
        description = "Voxel Edit!"
        usageMessage = "/voxeledit | /ve | /# | //voxeledit | //ve | //# [use 'help' for more!]"
        permission = "voxeledit"
        aliases = listOf("ve", "/voxeledit", "/ve", "#", "/#")
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
        Bukkit.getServer().scheduler.runTaskAsynchronously(VoxelEdit.javaPlugin, {
            if (args.isNotEmpty()) {
                val command = GetCommandOrNull(args[0])
                if (command == null) {
                    sender.sendMessage("Command not found!")
                    return@runTaskAsynchronously
                }

                command.invoke(args.copyOfRange(1, args.size), sender)
            } else sender.sendMessage("VoxelEdit by @Sakul6499 | Lukas")
        })

        return true
    }

    /*
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
                        sender.sendMessage("bps <BlocksPerSecond>")
                        sender.sendMessage("mode [enter VE-Mode]")

                        return@runTaskAsynchronously
                    }
     */

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