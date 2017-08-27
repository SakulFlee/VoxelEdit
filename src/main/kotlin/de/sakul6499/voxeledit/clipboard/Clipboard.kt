package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import de.framework.api.Quad
import de.sakul6499.voxeledit.VoxelEdit
import de.sakul6499.voxeledit.task.PlaceTask
import de.sakul6499.voxeledit.task.ReplacePosTask
import de.sakul6499.voxeledit.task.TaskHandler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object Clipboard {

    /**
     * Selection Blocks per Second (= 20 Ticks)
     */
    var SBPS: Int = 8192

    /**
     * Insert Blocks per Second (= 20 Ticks)
     */
    var IBPS: Int = 2048

    private var idCounter = 0

    private val pos: MutableList<Quad<Player, Vector, Vector, World>> = mutableListOf()
    private val selections: MutableList<SelectionData> = mutableListOf()
    private val insertions: MutableList<Insertion> = mutableListOf()

    init {
        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
            val selection = selections.filter { it.shouldSelect && (!it.finished && !it.canceled) }
            if (selection.isNotEmpty()) {
                val sbpt = SBPS / selection.size

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

                    it.notifier.sendMessage("#${it.id} SBPS: ${TaskHandler.BPS} SBPT: $sbpt [${timeLeft}s - ${it.selection.count()}b]")
                }
            } else {
                Thread.sleep(1000)
            }
        }, 0, 20)

        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
            val insertion = insertions.filter { !it.finished && !it.canceled }
            if (insertion.isNotEmpty()) {
                val ibpt = IBPS / insertion.size

                insertion.forEach {
                    val blocksLeft = it.blocks - it.loops
                    val timeLeft = blocksLeft / ibpt

                    it.notifier.sendMessage("#${it.id} IBPS: ${TaskHandler.BPS} IBPT: $ibpt [${timeLeft}s - ${blocksLeft}b]")

                    for (i in 0..ibpt) {
                        if (it.undo) {
                            if (!it.undo()) {
                                it.notifier.sendMessage("#${it.id} undo insertion task finished!")
                                it.finished = true
                                return@forEach
                            }
                        } else {
                            if (!it.task()) {
                                it.notifier.sendMessage("#${it.id} insertion task finished!")
                                it.finished = true
                                return@forEach
                            }
                        }

                        it.loops++
                    }
                }
            } else {
                Thread.sleep(1000)
            }
        }, 0, 20)
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

    // TODO undo insertion

    fun addInsertion(selectionID: Int, notifier: Player): Int {
        val selection = selections.firstOrNull { it.id == selectionID } ?: throw IllegalStateException("Selection with id $selectionID not found!")
        val insertion = Insertion.fromSelection(selection.selection, notifier.location.toVector(), ++idCounter, notifier)

        notifier.sendMessage("Added Insertion task #${insertion.id}!")
        notifier.sendMessage("Blocks to process: ${insertion.blocks}")
        insertions.add(insertion)

        return insertion.id
    }

    fun cancelInsertion(id: Int) {
        val insertion = insertions.firstOrNull { it.id == id } ?: throw IllegalStateException("Insertion with id $id not found!")
        insertion.notifier.sendMessage("Cancelling insertion: $id")

        insertion.canceled = true
    }

    fun pushPosition(player: Player, pos1: Vector? = null, pos2: Vector? = null, world: World? = null) {
        var entry = pos.firstOrNull { it.first == player }
        if (entry == null) {
            entry = Quad(player, pos1, pos2, world)
            pos.add(entry)
        } else {
            if (pos1 != null) entry.second = pos1
            if (pos2 != null) entry.third = pos2

            if (entry.fourth != null && world != null && entry.fourth!!.uid != world.uid) player.sendMessage("Warning: world changed by selection!")
            if (world != null) entry.fourth = world
        }
    }

    fun actionPlace(player: Player, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        // check for selectors
        val entry = pos.firstOrNull { it.first == player }
        if (entry?.second == null || entry.third == null) {
            player.sendMessage("You do not have clipboard yet (try selecting something) OR a position mismatch occurred!")
            return
        }

        player.sendMessage("Preparing place action ...")
        TaskHandler.addTask(PlaceTask(entry.fourth!!, entry.second!!, entry.third!!, materials, hollow), player)
        player.sendMessage("Place task submitted!")
    }

    fun actionReplace(player: Player, filter: Array<Bundle<Material, Byte>>, replace: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        // check for selectors
        val entry = pos.firstOrNull { it.first == player }
        if (entry?.second == null || entry.third == null) {
            player.sendMessage("You do not have clipboard yet (try selecting something) OR a position mismatch occurred!")
            return
        }

        player.sendMessage("Preparing replace action ...")
        TaskHandler.addTask(ReplacePosTask(entry.fourth!!, entry.second!!, entry.third!!, filter, replace, hollow), player)
        player.sendMessage("Replace task submitted!")
    }

    fun getSelectionsForPlayer(player: Player): List<SelectionData> = selections.filter { it.notifier == player }
    fun getSelectionsForPlayerByName(name: String): List<SelectionData> = selections.filter { it.notifier.name == name }
    fun getTasksByID(id: Int): SelectionData? = selections.filter { it.id == id }.firstOrNull()
}