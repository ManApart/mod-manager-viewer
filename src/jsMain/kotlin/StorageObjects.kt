package org.manapart

import kotlinx.serialization.Serializable

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
) {
    fun clear(){
        adds.clear()
        deletes.clear()
        tagsAdded.clear()
        tagsRemoved.clear()
    }
}

@Serializable
data class Config(val categories: Map<String, String>) {
    fun parseKeys() = categories.mapKeys { it.key.toInt() }
}
