package de.sakul6499.voxeledit

import de.framework.logger.Logger
import de.framework.map.Generator
import de.framework.plugin.api.Init
import de.framework.plugin.api.Plugin
import de.framework.plugin.api.Shutdown
import de.framework.plugin.api.Startup
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

@Plugin("FrameworkTestPlugin", "@Sakul6499", "Test")
class VoxelEdit : Listener {

    companion object {
        lateinit var javaPlugin: JavaPlugin
    }

    @Init
    fun initialize(javaPlugin: JavaPlugin) {
        Companion.javaPlugin = javaPlugin
    }

    lateinit var g: Generator

    @Startup
    fun startup() {
        Logger.info("Starting VoxelEdit!")

        // validate command
//        Logger.warning("Validating command ...")
//        ValidateCommands()

        // add command
//        try {
//            val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
//            commandMapField.isAccessible = true
//            val commandMap: CommandMap = commandMapField.get(Bukkit.getServer()) as CommandMap
//
//            val commandHandler = CommandHandler()
//            commandMap.register(commandHandler.name, "voxeledit:${commandHandler.name}", commandHandler)
//        } catch (e: NoSuchFieldException) {
//            Logger.error("Failed to register command!")
//        } catch (e: NullPointerException) {
//            Logger.error("Failed to register command!")
//        } catch (e: SecurityException) {
//            Logger.error("Failed to register command!")
//        }

        // init commands
        Logger.warning("Initializing commands ...")
        VECommands.initCommands()

        // register VE-Mode event handler
        Bukkit.getPluginManager().registerEvents(VEMode, javaPlugin)

        // register VE-Tool event handler
        Bukkit.getPluginManager().registerEvents(VETool, javaPlugin)

        Bukkit.getPluginManager().registerEvents(this, javaPlugin)

        Logger.info("Started VoxelEdit!")

        Logger.debug("Generator Test # START")
        Bukkit.getScheduler().runTask(VoxelEdit.javaPlugin, {
            while (Bukkit.getWorlds().size == 0) {
            }

            Bukkit.setDefaultGameMode(GameMode.CREATIVE)
            g = Generator()
            Logger.debug("Generator Test # END")
        })
    }

    @Shutdown
    fun shutdown() {
        Logger.info("Shutting down VoxelEdit!")

        Logger.info("Shut down VoxelEdit!")
    }

    @EventHandler
    fun a(event: PlayerJoinEvent) {
        event.player.teleport(g.w.spawnLocation)
    }
}
