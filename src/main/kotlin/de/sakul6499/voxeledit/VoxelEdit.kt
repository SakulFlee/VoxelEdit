package de.sakul6499.voxeledit

import de.framework.logger.Logger
import de.framework.plugin.api.Init
import de.framework.plugin.api.Plugin
import de.framework.plugin.api.Shutdown
import de.framework.plugin.api.Startup
import de.sakul6499.voxeledit.command.CommandHandler
import de.sakul6499.voxeledit.command.ValidateCommands
import org.bukkit.Bukkit
import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin

@Plugin("FrameworkTestPlugin", "@Sakul6499", "Test")
class VoxelEdit {

    companion object {
        lateinit var javaPlugin: JavaPlugin
    }

    @Init
    fun initialize(javaPlugin: JavaPlugin) {
        Companion.javaPlugin = javaPlugin
    }

    @Startup
    fun startup() {
        Logger.info("Starting VoxelEdit!")

        // validate commands
        Logger.warning("Validating commands ...")
        ValidateCommands()

        // add command
        try {
            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            commandMapField.isAccessible = true
            val commandMap: CommandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

            val commandHandler = CommandHandler()
            commandMap.register(commandHandler.name, "voxeledit:${commandHandler.name}", commandHandler)
        } catch (e: NoSuchFieldException) {
            Logger.error("Failed to register command!")
        } catch (e: NullPointerException) {
            Logger.error("Failed to register command!")
        } catch (e: SecurityException) {
            Logger.error("Failed to register command!")
        }

        // register VE-Mode event handler
        Bukkit.getPluginManager().registerEvents(VEMode, javaPlugin)

        // register VE-Tool event handler
        Bukkit.getPluginManager().registerEvents(VETool, javaPlugin)

        Logger.info("Started VoxelEdit!")
    }

    @Shutdown
    fun shutdown() {
        Logger.info("Shutting down VoxelEdit!")

        Logger.info("Shut down VoxelEdit!")
    }
}
