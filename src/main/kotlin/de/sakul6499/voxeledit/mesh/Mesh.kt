package de.sakul6499.voxeledit.mesh

import de.sakul6499.voxeledit.VoxelEdit
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class Mesh {
    private val id: UUID = UUID.randomUUID()

    private var vecs: Array<ArmorStand> = arrayOf()

    fun update(world: World, new_vecs: Array<Vector>) {
        Bukkit.getServer().scheduler.runTask(VoxelEdit.javaPlugin, {
            var futureAS: Array<Vector> = arrayOf()
            var existingAS: Array<ArmorStand> = arrayOf()

            new_vecs.forEach {
                val prevAS = vecs.find { prev -> prev.location.toVector().blockX == it.blockX && prev.location.toVector().blockY == it.blockY && prev.location.toVector().blockZ == it.blockZ }
                if (prevAS == null) {
                    futureAS += it
                } else {
                    existingAS += prevAS
                }
            }

            vecs.filterNot { existingAS.contains(it) }.forEach { it.remove() }
            vecs = existingAS

            futureAS.forEach { vecs += spawnMeshObject(world, it) }
        })
    }

    fun remove() {
        vecs.forEach { it.remove() }
    }

    private fun spawnMeshObject(world: World, vector: Vector): ArmorStand {
        val armorStand = world.spawnEntity(vector.toLocation(world), EntityType.ARMOR_STAND) as ArmorStand

        armorStand.isVisible = false
        armorStand.setGravity(false)
        armorStand.isSmall = true
        armorStand.isCollidable = false
        armorStand.isCustomNameVisible = false
        armorStand.isSilent = true
        armorStand.isMarker = true

        armorStand.helmet = ItemStack(Material.STONE)
        armorStand.isGlowing = true

        return armorStand
    }

    override fun equals(other: Any?): Boolean = if (other !is Mesh) false else other.id == id
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + Arrays.hashCode(vecs)
        return result
    }
}