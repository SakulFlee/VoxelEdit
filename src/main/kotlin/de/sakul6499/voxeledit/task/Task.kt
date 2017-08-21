package de.sakul6499.voxeledit.task

interface Task {
    //    var id: Int
//    var loops: Int
    val overallBlocksToProcess: Int

    /**
     * @return the currently left over blocks to process
     */
    fun count(undo: Boolean = false): Int

    fun process(): Boolean
    fun undo(): Boolean
}