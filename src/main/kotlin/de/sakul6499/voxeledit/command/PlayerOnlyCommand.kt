package de.sakul6499.voxeledit.command

import org.bukkit.entity.Player

class PlayerOnlyCommand(label: Array<String>, full_command: String = "", invoke: (args: Array<out String>, player: Player) -> Unit) : Command(label, full_command, { args, sender ->
    if (sender is Player) {
        invoke(args, sender)
    } else sender.sendMessage("This command is for players only!")
})