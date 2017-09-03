package de.sakul6499.voxeledit.command

import de.framework.api.Bundle
import org.bukkit.Material
import org.bukkit.entity.Player

fun main(args: Array<String>) {
    val a = CommandV1(arrayOf("A"), "A", arrayOf(
            CommandV1(arrayOf("B1", "b1"), "B1", arrayOf(CommandV1.GetIntHolder().appendChild(CommandV1.GetMaterialHolder()))),
            CommandV1(arrayOf("B2", "b2"), "B2", arrayOf(CommandV1.GetIntHolder().optional().appendChild(CommandV1.GetMaterialHolder().optional()))),
            CommandV1(arrayOf("B3", "b3"), "B3", arrayOf(CommandV1.GetIntHolder().optional().appendChild(CommandV1.GetMaterialHolder().optional()))))
    )
    println(a)

    println(a.a("A B2".split(" ").toTypedArray(), 0, null))
}

class CommandV1(val label: Array<String>, val help: String = "", private var args: Array<CommandV1> = arrayOf(), val invoke: () -> Boolean = { true }) {

    private var optional: Boolean = false

    init {
        if (label.isEmpty()) throw IllegalStateException("CommandV1 label must not be null!")
    }

    companion object {
        private val INT_INDICATOR = "__INT__"
        private val MATERIAL_INDICATOR = "__MATERIAL__"

        fun GetIntHolder(): CommandV1 = CommandV1(arrayOf(INT_INDICATOR), "Int placeholder")
        fun GetMaterialHolder(): CommandV1 = CommandV1(arrayOf(MATERIAL_INDICATOR), "Material placeholder")
    }

    fun isIntPlaceholder(): Boolean = label.size == 1 && label[0] == INT_INDICATOR
    fun isInt(): Boolean = isIntPlaceholder()
    fun isMaterialPlaceholder(): Boolean = label.size == 1 && label[0] == MATERIAL_INDICATOR
    fun isMaterial(): Boolean = isMaterialPlaceholder()

    var match_value: Any? = null
        private set
        get() {
            val tmp = field
            field = null
            return tmp
        }

    fun match_value_as_int(): Int? {
        if (!isInt()) throw IllegalStateException("CommandV1 type must be int to access match value as int!")

        if (match_value == null) return match_value

        return match_value as Int
    }

    fun match_value_as_material(): Array<Bundle<Material, Byte>>? {
        if (!isMaterial()) throw IllegalStateException("CommandV1 type must be material to access match value as material!")

        if (match_value == null) return match_value

        @Suppress("UNCHECKED_CAST")
        return match_value as Array<Bundle<Material, Byte>>
    }

    fun match(l: String, player: Player?): Boolean = when {
        isInt() -> {
            try {
                match_value = Integer.parseInt(l)
                true
            } catch (e: NumberFormatException) {
                false
            }
        }
        isMaterial() -> {
            var output: Array<Bundle<Material, Byte>> = arrayOf()

            try {
                val literalMaterials = l.split(";")
                literalMaterials.forEach {
                    var s = it

                    val material: Material
                    var mod: Byte = -1

                    val index = s.indexOf(':')
                    if (index > 0) {
                        mod = try {
                            var i = Integer.parseInt(s.substring(index + 1, s.length))
                            if (i < 0) {
                                player?.sendMessage("The block data index must not be lower than zero! [Setting to zero for '$it']")
                                i = 0
                            }

                            i.toByte()
                        } catch (e: NumberFormatException) {
                            player?.sendMessage("The block data index must be a number! [Setting to zero for '$it']")

                            0.toByte()
                        }
                        s = s.substring(0, index)
                    }

                    try {
                        material = Material.matchMaterial(s)
                        output += Bundle(material, mod)
                    } catch (e: IllegalStateException) {
                        player?.sendMessage("Malformed material name!")

                        return false
                    }
                }
            } catch (e: NullPointerException) {
                player?.sendMessage("Materials malformed!")
            }

            if (output.isEmpty()) {
                player?.sendMessage("Materials must not be empty!")
                false
            } else {
                match_value = output
                true
            }
        }
        else -> label.any { it.toLowerCase() == l.toLowerCase() }
    }

    /*
    Match: A B2 M

    A
        // B1 I M
        B1  I M

        // B2 (I) (M)
        B2
        B2  I M
        B2  I
        B2  M

        // B3 (I) (M)
        B3
        B3  I M
        B3  I
        B3  M
     */

    fun a(full: Array<String>, index: Int = 0, player: Player? = null): Boolean = if (index >= full.size) false else {
        println("#$index [${full.size}]; ${full[index]}")
        if (match(full[index], player)) {
            args.any { it.a(full, index + 1, player) }
        } else false
    }


//    fun full_match(l: String, player: Player?): Bundle<Boolean, Array<Bundle<Int, Any?>>> {
//        val output = Bundle<Boolean, Array<Bundle<Int, Any?>>>()
//        var array = arrayOf<Bundle<Int, Any?>>()
//
//        var match = false
//
//        var counter = 0
//        l.split(" ").forEach {
//            counter++
//
//            // TODO <--
//        }
//
//        return output
//    }

    fun optional(): CommandV1 {
        optional = true
        return this
    }

    fun appendChild(child: CommandV1): CommandV1 {
        args += child

        return this
    }

    fun childs(): Int {
        var childs: Int = 0

        if (args.isNotEmpty()) args.forEach { childs += it.childs() }

        return childs
    }

    fun toLabel(): String = when {
        isIntPlaceholder() -> if (optional) "(<Int>)" else "<Int>"
        isMaterialPlaceholder() -> if (optional) "(<Material:ID;...>)" else "<Material:ID;...>"
        else -> {
            var output = if (optional) "(" else ""
            label.forEach { output += "$it|" }
            output = output.substring(0, output.length - 1)
            if (optional) output.plus(")") else output
        }
    }

    override fun toString(): String {
        val label = toLabel()

        return if (args.isNotEmpty()) {
            var output = ""
            args.forEach {
                output += "$label $it"
            }
            output
        } else "$label\n"
    }
}