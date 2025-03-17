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
    var categories: Map<Int, String> = mapOf(),
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
    fun category(): String? {
        //TODO - allow config uploading for category mappings
        return null
//        return categoryId?.let { toolConfig.categories[it] }
    }
}

@Serializable
data class Changes(
    val adds: MutableSet<Int> = mutableSetOf(),
    val deletes: MutableSet<String> = mutableSetOf(),
    val tagsAdded: MutableMap<String, MutableSet<String>> = mutableMapOf(),
    val tagsRemoved: MutableMap<String, MutableSet<String>> = mutableMapOf(),
)

@Serializable
data class Config(val categories: Map<String, String>) {
    fun parseKeys() = categories.mapKeys { it.key.toInt() }
}

private var inMemoryStorage = InMemoryStorage()

fun updateInMemoryStorage(newStorage: InMemoryStorage){
    //TODO - don't delete changes
    //Only load data object, not all in memory object
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
fun addTag(mod: Mod, tag: String){
    val added = getChanges().tagsAdded
    if (added[mod.uniqueId()] == null) added[mod.uniqueId()] = mutableSetOf()
    added[mod.uniqueId()]?.add(tag)
    persistMemory()
}
fun removeTag(mod: Mod, tag: String){
    val removed = getChanges().tagsRemoved
    if (removed[mod.uniqueId()] == null) removed[mod.uniqueId()] = mutableSetOf()
    removed[mod.uniqueId()]?.add(tag)
    persistMemory()
}

fun saveCategories(categories: Map<Int, String>) {
    inMemoryStorage.categories = categories
}

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
