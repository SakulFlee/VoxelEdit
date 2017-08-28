package de.sakul6499.voxeledit.mesh

import java.util.*

object MeshHandler {
    private val meshMap: MutableMap<UUID, Mesh> = mutableMapOf()

    /**
     * Note: Must NOT be a valid player unique id (can be any UUID)
     */
    fun createMesh(uuid: UUID): Mesh {
        val mesh = meshMap.getOrPut(uuid, { Mesh() })
        return mesh
    }

    fun getMesh(uuid: UUID): Mesh? = meshMap[uuid]

    fun removeMesh(uuid: UUID) {
        if (meshMap.containsKey(uuid)) {
            meshMap[uuid]!!.remove()
            meshMap.remove(uuid)
        }
    }
}