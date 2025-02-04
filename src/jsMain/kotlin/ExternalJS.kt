package org.manapart

import kotlin.js.Promise

object JsonObject {
    fun keys(obj: Any): List<String> {
        val raw = js("Object.keys(obj)") as Array<*>
        return raw.map { it as String }
    }

    fun entries(obj: Any): List<Pair<Any, Any>> {
        val raw = js("Object.entries(obj)") as Array<Array<Any>>
        return raw.map { it[0] to it[1] }
    }
}

@JsModule("localforage")
@JsNonModule
external object LocalForage {
    fun setItem(key: String, value: Any): Promise<*>
    fun getItem(key: String): Promise<Any?>
    fun config(config: LocalForageConfig)
}

data class LocalForageConfig(val name: String)
