package de.sakul6499.voxeledit.task

import de.framework.logger.Logger
import de.sakul6499.voxeledit.VoxelEdit
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object TaskHandler {

    /**
     * Blocks Per Second
     */
    var BPS: Int = 2048

    private var tasks: MutableMap<Task, Array<out Player>> = mutableMapOf()
    private var bTask: BukkitTask? = null
    private var task: Runnable = Runnable {
        if (tasks.isEmpty()) {
            bTask!!.cancel()
            bTask = null
            return@Runnable
        }

        // Blocks Per Task
        val bpt = BPS / tasks.size

        try {
            val finished: MutableList<Task> = mutableListOf()

            tasks.forEach {
                if (finished.contains(it.key)) return@forEach
                val timeLeft = it.key.count() / bpt

                for (i in 0..bpt) {
                    if (!it.key.process()) {
                        Logger.debug("Finished!")
                        it.value.forEach { player -> player.sendMessage("#${it.key.id} FINISHED!") }
                        finished.add(it.key)
                        return@forEach
                    }
                    it.key.loops++
                }

                it.value.forEach { player ->
                    player.sendMessage("#${it.key.id} BPS: $BPS BPT: $bpt [$timeLeft s]")
                }
                Logger.debug("#${it.key.id} BPS: $BPS BPT: $bpt [$timeLeft]")
            }

            finished.forEach { tasks.remove(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun addTask(t: Task, vararg notifier: Player): Int {
        t.id = tasks.size + 1
        tasks.put(t, notifier)

        if (bTask == null) {
            bTask = Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, task, 0, 20)
        }

        return tasks.size
    }
}