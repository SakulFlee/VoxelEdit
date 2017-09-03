@file:Suppress("UNUSED")

package de.sakul6499.voxeledit.command

import kotlin.reflect.full.memberProperties

fun main(args: Array<String>) {
    println("Commands:")
    GetAllCommands().forEach { println(it) }
}

fun GetCommand(label: String): CommandData = GetCommandOrNull(label) ?: throw NullPointerException("Command '$label' not found!")
fun GetCommandOrNull(label: String): CommandData? = GetAllCommands().find { it.label.any { it == label.toLowerCase() } }

fun GetAllCommands(): Array<CommandData> {
    var output: Array<CommandData> = arrayOf()
    Commands::class.memberProperties.forEach {
        val prop = it.get(Commands)
        if (prop is CommandData) {
            output += prop
        }
    }
    return output
}

object Commands {
    val test = CommandData(arrayOf("test", "t", "1")) { args, sender ->
        sender.sendMessage("Test!")
        if (args.isNotEmpty()) {
            args.forEach {
                sender.sendMessage(" -> $it")
            }
        }
    }

    val sphere = CommandData(arrayOf("sphere", "s")) { args, sender ->
        // command stuff here
    }

    val voxel = CommandData(arrayOf("voxel", "v")) { args, sender ->
        // command stuff here
    }
}