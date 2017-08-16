package de.sakul6499

import java.util.*

/**
 * # UserRegistry
 */
object UserRegistry {
    private var user: Array<UserObject> = arrayOf()

    fun getUserObject(uuid: UUID): UserObject {
        var u = user.firstOrNull { it.uuid == uuid }
        if(u == null) {
            u = UserObject(uuid)
            user += u
        }

        return u
    }

}