package de.sakul6499.voxeledit.clipboard

import de.framework.api.Bundle
import de.framework.api.Quad
import de.sakul6499.voxeledit.task.PosTask
import de.sakul6499.voxeledit.task.ReplacePosTask
import de.sakul6499.voxeledit.task.Tasks
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector

object Clipboard {

    /**
     * Selection Blocks per Second (= 20 Ticks)
     */
    //    var SBPS: Int = 8192

    /**
     * Insert Blocks per Second (= 20 Ticks)
     */
    //    var IBPS: Int = 2048

//    private var idCounter = 0

    private val pos: MutableList<Quad<Player, Vector, Vector, World>> = mutableListOf()
//    private val insertions: MutableList<Insertion> = mutableListOf()

    init {
//        Bukkit.getServer().scheduler.runTaskTimer(VoxelEdit.javaPlugin, {
//            val insertion = insertions.filter { !it.finished && !it.canceled }
//            if (insertion.isNotEmpty()) {
//                val ibpt = IBPS / insertion.size
//
//                insertion.forEach {
//                    val blocksLeft = it.blocks - it.loops
//                    val timeLeft = blocksLeft / ibpt
//
//                    it.notifier.sendMessage("#${it.id} IBPS: ${Tasks.BlocksPerSecond} IBPT: $ibpt [${timeLeft}s - ${blocksLeft}b]")
//
//                    for (i in 0..ibpt) {
//                        if (it.undo) {
//                            if (!it.undo()) {
//                                it.notifier.sendMessage("#${it.id} undo insertion task finished!")
//                                it.finished = true
//                                return@forEach
//                            }
//                        } else {
//                            if (!it.task()) {
//                                it.notifier.sendMessage("#${it.id} insertion task finished!")
//                                it.finished = true
//                                return@forEach
//                            }
//                        }
//
//                        it.loops++
//                    }
//                }
//            } else {
//                Thread.sleep(1000)
//            }
//        }, 0, 20)
    }


//    fun addInsertion(selectionID: Int, notifier: SQLPlayer): Int {
//        val selection = selections.firstOrNull { it.id == selectionID } ?: throw IllegalStateException("Selection with id $selectionID not found!")
//        val insertion = Insertion.fromSelection(selection.selection, notifier.location.toVector(), ++idCounter, notifier)
//
//        notifier.sendMessage("Added Insertion task #${insertion.id}!")
//        notifier.sendMessage("Blocks to process: ${insertion.blocks}")
//        insertions.add(insertion)
//
//        return insertion.id
//    }

    fun createPosSelection(notifier: Player, hollow: Boolean = false): Int {
        val entry = Clipboard.getEntryPos(notifier) ?: return -1
        return Tasks.addSelection(PosSelection(entry.fourth!!, entry.second!!, entry.second!!, hollow), notifier)
    }


//    fun cancelInsertion(id: Int) {
//        val insertion = insertions.firstOrNull { it.id == id } ?: throw IllegalStateException("Insertion with id $id not found!")
//        insertion.notifier.sendMessage("Cancelling insertion: $id")
//
//        insertion.canceled = true
//    }

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

    private fun getEntryPos(player: Player): Quad<Player, Vector, Vector, World>? {
        val entry = pos.firstOrNull { it.first == player }
        if (entry?.second == null || entry.third == null) {
            player.sendMessage("You do not have clipboard yet (try selecting something) OR injectCommandWrapper position mismatch occurred!")
            return null
        }

        return entry
    }

    fun actionPlace(player: Player, materials: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        val entry = getEntryPos(player) ?: return

        player.sendMessage("Preparing place action ...")
        Tasks.addTask(PosTask(entry.fourth!!, entry.second!!, entry.third!!, materials, hollow), player)
        player.sendMessage("Place task submitted!")
    }

    fun actionReplace(player: Player, filter: Array<Bundle<Material, Byte>>, replace: Array<Bundle<Material, Byte>>, hollow: Boolean = false) {
        val entry = getEntryPos(player) ?: return

        player.sendMessage("Preparing replace action ...")
        Tasks.addTask(ReplacePosTask(entry.fourth!!, entry.second!!, entry.third!!, filter, replace, hollow), player)
        player.sendMessage("Replace task submitted!")
    }


}