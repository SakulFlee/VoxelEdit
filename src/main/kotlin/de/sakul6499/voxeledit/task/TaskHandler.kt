package de.sakul6499.voxeledit.task

import de.sakul6499.voxeledit.VoxelEdit
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TaskHandler {

    /**
     * Blocks Per Second
     */
    var BPS: Int = 4096

    private var idCounter = 0
    private val tasks: MutableList<TaskData> = mutableListOf()

    init {
        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
            val taskCount = tasks.count { !it.finished && !it.canceled }
            if (taskCount > 0) {
                // Blocks Per Task
                val bpt = BPS / taskCount

                tasks.filterNot { it.finished || it.canceled }.forEach {
                    val timeLeft = it.task.count(it.undo) / bpt

                    for (i in 0..bpt) {
                        if (it.undo) {
                            if (!it.task.undo()) {
                                it.notifier.sendMessage("#${it.id} undo task finished!")
                                it.finished = true
                                return@forEach
                            }
                            it.loops++
                        } else {
                            if (!it.task.process()) {
                                it.notifier.sendMessage("#${it.id} task finished!")
                                it.finished = true
                                return@forEach
                            }
                            it.loops++
                        }
                    }

                    it.notifier.sendMessage("#${it.id} BPS: $BPS BPT: $bpt [${timeLeft}s - ${it.task.count(it.undo)}b]")
                }
            } else {
                Thread.sleep(1000)
            }
        }, 0, 20)
    }

    fun addTask(task: Task, notifier: Player): Int {
        val taskData = TaskData(task, ++idCounter, notifier)

        notifier.sendMessage("Added task #${taskData.id}!")
        notifier.sendMessage("Block to process: ${taskData.task.overallBlocksToProcess}")
        tasks.add(taskData)

        return taskData.id
    }

    fun cancelTask(id: Int) {
        val task = tasks.firstOrNull { it.id == id } ?: throw IllegalStateException("Task with id $id not found!")
        task.notifier.sendMessage("Cancelling task: $id")

        task.canceled = true
    }

    fun undoTask(id: Int) {
        val task = tasks.firstOrNull { it.id == id } ?: throw IllegalStateException("Task with id $id not found!")
        task.notifier.sendMessage("Undo task: $id")

        task.undo = !task.undo
        if (task.finished) task.finished = false
        if (task.canceled) task.canceled = false
    }

    fun getTasksForPlayer(player: Player): List<TaskData> = tasks.filter { it.notifier == player }
    fun getTasksForPlayerByName(name: String): List<TaskData> = tasks.filter { it.notifier.name == name }
    fun getTaskByID(id: Int): TaskData? = tasks.filter { it.id == id }.firstOrNull()
}