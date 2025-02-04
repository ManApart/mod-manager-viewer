package org.manapart

import kotlinx.serialization.Serializable
import org.manapart.LocalForage.config
import org.w3c.dom.HTMLElement
import kotlin.js.Promise

@Serializable
data class InMemoryStorage(
    var mods: List<Mod> = listOf(),
    var profiles: List<Profile> = listOf(),
)

@Serializable
data class Profile(val name: String, var ids: List<Int>, var filePaths: List<String>) {
    fun modCount() = ids.size + filePaths.size
}

@Serializable
data class Mod(
    var name: String,
    var filePath: String,
    var loadOrder: Int,
    var id: Int? = null,
    var plugins: List<String> = emptyList(),
    var creationId: String? = null,
    var downloadPath: String? = null,
    var fileId: Int? = null,
    var latestFileId: Int? = null,
    var version: String? = null,
    var latestVersion: String? = null,
    var enabled: Boolean = false,
    var categoryId: Int? = null,
    var endorsed: Boolean? = null,
    val tags: MutableSet<String> = mutableSetOf(),
)

private var inMemoryStorage = InMemoryStorage()
var characterCards: Map<String, HTMLElement> = mapOf()

fun clearStorage(){
    inMemoryStorage = InMemoryStorage()
    characterCards = mapOf()
}

fun resetStorage() {
    inMemoryStorage = InMemoryStorage()
    characterCards = mapOf()
//    loadExample(false).then {
//        persistMemory()
//    }
}

//fun getProfile(): Profile {
//    return inMemoryStorage.profile
//}
//
//fun saveProfile(profile: Profile) {
//    inMemoryStorage.profile = profile
//}

fun createDB() {
    config(LocalForageConfig("starfield-mod-manager"))
}

fun persistMemory() {
    LocalForage.setItem("memory", jsonMapper.encodeToString(inMemoryStorage))
}


fun loadMemory(): Promise<*> {
    return LocalForage.getItem("memory").then { persisted ->
        if (persisted != null && persisted != undefined) {
            inMemoryStorage = jsonMapper.decodeFromString(persisted as String)
        }
    }
}
