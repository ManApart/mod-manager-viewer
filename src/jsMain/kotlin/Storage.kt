package org.manapart

import kotlinx.serialization.Serializable
import org.manapart.LocalForage.config
import org.manapart.pages.loadInitialData
import kotlin.js.Promise

enum class GameMode(val displayName: String, val pathName: String) {
    STARFIELD("Starfield", "starfield"),
    OBLIVION_REMASTERED("Oblivion Remastered", "oblivionremastered");

    fun game() = inMemoryStorage.games[this]!!
}

@Serializable
data class InMemoryStorage(
    var currentMode: GameMode = GameMode.STARFIELD,
    val games: MutableMap<GameMode, GameSpecificStorage> = mutableMapOf()
) {
    init {
        GameMode.entries.forEach { if (!games.containsKey(it)) games[it] = GameSpecificStorage() }
    }
}

@Serializable
data class GameSpecificStorage(
    var mods: List<Mod> = listOf(),
    var profiles: List<Profile> = listOf(),
    val changes: Changes = Changes(),
    var categories: Map<Int, String> = mapOf(),
)

@Serializable
data class DataJson(
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
) {
    fun uniqueId(): String {
        return id?.toString() ?: fileId?.toString() ?: name
    }

    fun category(): String? {
        return categoryId?.let { currentGame().categories[it] ?: it.toString() }
    }

    fun allTags(changes: Changes): Set<String> {
        return (tags + (changes.tagsAdded[uniqueId()] ?: emptySet())) - (changes.tagsRemoved[uniqueId()] ?: emptySet()).toSet()
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

fun updateInMemoryStorage(game: GameMode, data: DataJson) {
    game.game().mods = data.mods
    game.game().profiles = data.profiles
}

fun clearStorage() {
    inMemoryStorage = InMemoryStorage()
}

fun resetStorage() {
    inMemoryStorage = InMemoryStorage()
    loadInitialData()
}

fun currentMode() = inMemoryStorage.currentMode
fun nextMode() {
    val i = GameMode.entries.indexOf(currentMode()) + 1
    if (i >= GameMode.entries.size) {
        inMemoryStorage.currentMode = GameMode.entries.first()
    } else {
        inMemoryStorage.currentMode = GameMode.entries[i]
    }
    persistMemory()
}

fun currentGame() = inMemoryStorage.games[inMemoryStorage.currentMode]!!

fun getProfiles() = currentGame().profiles
fun getMods() = currentGame().mods
fun getChanges() = currentGame().changes
fun addTag(mod: Mod, tag: String) {
    val added = getChanges().tagsAdded
    if (added[mod.uniqueId()] == null) added[mod.uniqueId()] = mutableSetOf()
    added[mod.uniqueId()]?.add(tag)
    persistMemory()
}

fun removeTag(mod: Mod, tag: String) {
    val removed = getChanges().tagsRemoved
    if (removed[mod.uniqueId()] == null) removed[mod.uniqueId()] = mutableSetOf()
    removed[mod.uniqueId()]?.add(tag)
    persistMemory()
}

fun getCategories() = currentGame().categories.toMap()

fun saveCategories(game: GameMode, categories: Map<Int, String>) {
    game.game().categories = categories
}

fun createDB() {
    config(LocalForageConfig("starfield-mod-manager"))
}

fun persistMemory() {
    LocalForage.setItem("starfield-mod-manager", jsonMapper.encodeToString(inMemoryStorage))
}

fun hasMemory(): Promise<Boolean> {
    return LocalForage.getItem("starfield-mod-manager").then { persisted -> return@then persisted != null }
}

fun loadMemory(): Promise<*> {
    return LocalForage.getItem("starfield-mod-manager").then { persisted ->
        if (persisted != null && persisted != undefined) {
            inMemoryStorage = jsonMapper.decodeFromString(persisted as String)
        }
    }
}
