package de.sakul6499.voxeledit.command

import org.bukkit.command.CommandSender
import java.util.*

class CommandData(label: Array<String>, val invoke: (args: Array<out String>, sender: CommandSender) -> Unit) {

    var label: Array<String>
        private set

    init {
        this.label = arrayOf()
        label.forEach {
            this.label += it.toLowerCase()
        }
    }

    override fun toString(): String {
        return "CommandData(invoke=$invoke, label=${Arrays.toString(label)})"
    }
}