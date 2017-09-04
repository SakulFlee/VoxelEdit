package de.sakul6499.voxeledit.command

import org.bukkit.command.CommandSender

open class Command(label: Array<String>, private val syntax: String, private val help: String, val invoke: (args: Array<out String>, sender: CommandSender) -> Unit) {

    var label: Array<String>
        private set

    init {
        this.label = arrayOf()
        label.forEach {
            this.label += it.toLowerCase()
        }
    }

    override fun toString(): String = "$syntax: $help"
}