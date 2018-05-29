package de.sakul6499.voxeledit.task

import de.sakul6499.voxeledit.VoxelEdit
import de.sakul6499.voxeledit.clipboard.Selection
import de.sakul6499.voxeledit.clipboard.SelectionData
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object Tasks {

    var BlocksPerSecond: Int = 4096
    var SelectionBlocksPerSecond: Int = 8192

    private var idCounter = 0
    private val tasks: MutableList<TaskData> = mutableListOf()
    private val selections: MutableList<SelectionData> = mutableListOf()

    init {
        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
            val taskCount = tasks.count { !it.finished && !it.canceled }
            if (taskCount > 0) {
                // Blocks Per Task
                val bpt = BlocksPerSecond / taskCount

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

                    it.notifier.sendMessage("#${it.id} BlocksPerSecond: ${BlocksPerSecond} BPT: $bpt [${timeLeft}s - ${it.task.count(it.undo)}b]")
                }
            } else {
                Thread.sleep(1000)
            }
        }, 0, 20)

        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
            val selection = selections.filter { it.shouldSelect && (!it.finished && !it.canceled) }
            if (selection.isNotEmpty()) {
                val sbpt = SelectionBlocksPerSecond / selection.size

                selection.forEach {
                    val timeLeft = (it.selection.blocks - it.loops) / sbpt

                    for (i in 0..sbpt) {
                        if (!it.selection.select()) {
                            it.notifier.sendMessage("#${it.id} selection task finished!")
                            it.finished = true
                            return@forEach
                        }

                        it.loops++
                    }

                    it.notifier.sendMessage("#${it.id} SBPS: ${Tasks.BlocksPerSecond} SBPT: $sbpt [${timeLeft}s - ${it.selection.count()}b]")
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

    fun addSelection(selection: Selection, notifier: Player): Int {
        val selectionData = SelectionData(selection, ++idCounter, notifier)

        notifier.sendMessage("Added Selection task #${selectionData.id}!")
        notifier.sendMessage("Blocks to process: ${selectionData.selection.blocks}")
        selections.add(selectionData)

        return selectionData.id
    }

    fun cancelSelection(id: Int) {
        val selection = selections.firstOrNull { it.id == id } ?: throw IllegalStateException("Selection with id $id not found!")
        selection.notifier.sendMessage("Cancelling selection: $id")

        selection.canceled = true
    }

    fun getTasksForPlayer(player: Player): List<TaskData> = tasks.filter { it.notifier == player }
    fun getTasksForPlayerByName(name: String): List<TaskData> = tasks.filter { it.notifier.name == name }
    fun getTaskByID(id: Int): TaskData? = tasks.filter { it.id == id }.firstOrNull()

    fun getSelectionsForPlayer(player: Player): List<SelectionData> = selections.filter { it.notifier == player }
    fun getSelectionsForPlayerByName(name: String): List<SelectionData> = selections.filter { it.notifier.name == name }
    fun getTasksByID(id: Int): SelectionData? = selections.filter { it.id == id }.firstOrNull()
}