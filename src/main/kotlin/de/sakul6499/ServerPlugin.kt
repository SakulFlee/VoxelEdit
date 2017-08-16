package de.sakul6499

import de.framework.logger.Logger
import de.framework.plugin.api.Init
import de.framework.plugin.api.Plugin
import de.framework.plugin.api.Shutdown
import de.framework.plugin.api.Startup
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin

@Plugin("FrameworkTestPlugin", "@Sakul6499", "Test")
class ServerPlugin {

    private lateinit var javaPlugin: JavaPlugin

    @Init
    fun initialize(javaPlugin: JavaPlugin) {
        this.javaPlugin = javaPlugin
    }

    @Startup
    fun startup() {
        Logger.info("Starting VoxelEdit!")

        // add command
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap: CommandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

            val commandHandler = CommandHandler()
            commandMap.register(commandHandler.name, "voxeledit:${commandHandler.name}", commandHandler)
        } catch(e: NoSuchFieldException) {
            Logger.error("Failed to register command!")
        } catch(e: NullPointerException) {
            Logger.error("Failed to register command!")
        } catch(e: SecurityException) {
            Logger.error("Failed to register command!")
        }

        Logger.info("Started VoxelEdit!")
    }

    @Shutdown
    fun shutdown() {
        Logger.info("Shutting down VoxelEdit!")

        Logger.info("Shut down VoxelEdit!")
    }
}