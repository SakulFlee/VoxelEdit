package de.sakul6499

abstract class Task<out T>(val data: T) {
    abstract fun process(): Int

    abstract fun count(): Int
}