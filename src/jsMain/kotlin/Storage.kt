package org.manapart

import kotlinx.serialization.Serializable
import org.manapart.LocalForage.config
import org.manapart.pages.loadInitialData
import kotlin.js.Promise

@Serializable
data class InMemoryStorage(
    var mods: List<Mod> = listOf(),
    var profiles: List<Profile> = listOf(),
    val changes: Changes = Changes(),
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
) {
    fun uniqueId(): String {
        return id?.toString() ?: fileId?.toString() ?: name
    }
}

@Serializable
data class Changes(
    val adds: MutableSet<Int> = mutableSetOf(),
    val deletes: MutableSet<String> = mutableSetOf(),
    val tagsAdded: MutableMap<String, MutableSet<String>> = mutableMapOf(),
    val tagsRemoved: MutableMap<String, MutableSet<String>> = mutableMapOf(),
)

private var inMemoryStorage = InMemoryStorage()

fun updateInMemoryStorage(newStorage: InMemoryStorage){
    inMemoryStorage = newStorage
}

fun clearStorage(){
    inMemoryStorage = InMemoryStorage()
}

fun resetStorage() {
    inMemoryStorage = InMemoryStorage()
    loadInitialData()

}

fun getProfiles() = inMemoryStorage.profiles
fun getMods() = inMemoryStorage.mods
fun getChanges() = inMemoryStorage.changes

fun createDB() {
    config(LocalForageConfig("starfield-mod-manager"))
}

fun persistMemory() {
    LocalForage.setItem("memory", jsonMapper.encodeToString(inMemoryStorage))
}

fun hasMemory(): Promise<Boolean> {
    return LocalForage.getItem("memory").then { persisted -> return@then persisted != null }
}

fun loadMemory(): Promise<*> {
    return LocalForage.getItem("memory").then { persisted ->
        if (persisted != null && persisted != undefined) {
            inMemoryStorage = jsonMapper.decodeFromString(persisted as String)
        }
    }
}
