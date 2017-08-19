package de.sakul6499.voxeledit.task

interface Task {
    var id: Int
    var loops: Int
    val overallBlocksToProcess: Int

    fun count(): Int
    fun process(): Boolean
}