package de.sakul6499

class SphereTask(data: SphereData): Task<SphereData>(data) {
    override fun process(): Int = data.process()

    override fun count(): Int = data.count()
}