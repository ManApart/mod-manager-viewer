package org.manapart

import org.manapart.LocalForage.config
import org.manapart.pages.loadInitialData
import kotlin.js.Promise

enum class GameMode(val displayName: String, val pathName: String) {
    STARFIELD("Starfield", "starfield"),
    OBLIVION_REMASTERED("Oblivion Remastered", "oblivionremastered");

    fun game() = inMemoryStorage.games[this]!!
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
fun addTag(mod: Mod, rawTag: String) {
    val tag = rawTag.lowercase()
    val changes = getChanges()
    val del = changes.getRemoved(mod)
    val add = changes.getAdded(mod)

    if (del.contains(tag)){
        del.remove(tag)
        if (del.isEmpty()){
            changes.tagsRemoved.remove(mod.uniqueId())
        } else {
            changes.tagsRemoved[mod.uniqueId()] = del
        }
    } else {
        add.add(tag)
        changes.tagsAdded[mod.uniqueId()] = add
    }

    persistMemory()
}

fun byId(id: Int) = getMods().firstOrNull { it.id == id }
fun byName(name: String, silent: Boolean = false) = getMods().firstOrNull { it.name == name }.also { if (it == null && !silent) println("No Mod found for $name") }

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
