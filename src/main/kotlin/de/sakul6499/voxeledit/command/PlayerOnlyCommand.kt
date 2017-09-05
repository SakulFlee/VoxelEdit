package de.sakul6499.voxeledit.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
class PlayerOnlyCommand(label: Array<String>, syntax: String, help: String, _invoke: (Array<out String>, Player) -> CommandReturn) : Command(label, syntax, help, _invoke as (args: Array<out String>, sender: CommandSender) -> CommandReturn) {
    override fun invoke(args: Array<out String>, sender: CommandSender) {
        if(sender !is Player) sender.sendMessage("This command is for players only!")
        else super.invoke(args, sender)
    }
}