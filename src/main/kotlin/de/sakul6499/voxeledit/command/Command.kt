package de.sakul6499.voxeledit.command

import org.bukkit.command.CommandSender

open class Command(label: Array<String>, private val syntax: String, private val help: String, private val _invoke: (args: Array<out String>, sender: CommandSender) -> CommandReturn) {

    var label: Array<String>
        private set

    init {
        this.label = arrayOf()
        label.forEach {
            this.label += it.toLowerCase()
        }
    }

    open fun invoke(args: Array<out String>, sender: CommandSender) {
        if(_invoke(args, sender) == CommandReturn.FAILED) {
            sender.sendMessage("Invalid syntax!")
            sender.sendMessage(" -> $syntax")
        }
    }

    override fun toString(): String = "$syntax: $help"
}