package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.serialization.Serializable
import org.manapart.Changes
import org.manapart.Mod
import org.manapart.getChanges
import org.manapart.jsonMapper

@Serializable
enum class SearchType { PROPERTY, NAME, CATEGORY, TAG }

var currentSearch: String = ""
val searchTerms: MutableMap<SearchType, MutableSet<String>> = SearchType.entries.associateWith { mutableSetOf<String>() }.toMutableMap()

fun parseSearchTerm(raw: String) {
    val parts = raw.split(":")
    val category = parts.first().lowercase()
    val term = parts.last().lowercase()
    if (parts.size == 1) {
        val type = if (listOf("endorsed", "unendorsed", "enabled", "disabled", "missing").contains(category) || category.toIntOrNull() != null) SearchType.PROPERTY else SearchType.NAME
        searchTerms[type]?.add(category)
    } else {
        val type = SearchType.entries.firstOrNull { it.name.lowercase() == category } ?: SearchType.NAME
        searchTerms[type]?.add(term)
    }
}

fun searchMods(mods: Map<String, Mod>) {
    val props = searchTerms[SearchType.PROPERTY]!!
    val searchId = props.firstNotNullOfOrNull { it.toIntOrNull() }?.toString() ?: currentSearch.toIntOrNull()?.toString()
    val endorsed = if (props.contains("endorsed")) true else null
    val unendorsed = props.contains("unendorsed") || props.contains("-endorsed")
    val enabled = props.parseFlag("enabled")
    val disabled = props.parseFlag("disabled")
    val enable = disabled?.let { false } ?: enabled
    val missing = props.parseFlag("missing")

    if (currentSearch.isBlank() && searchTerms.values.all { it.isEmpty() }) modDoms.values.forEach { it.removeClass("hidden") } else {
//        console.log("searching ", "'$currentSearch'", mods.keys.size, modDoms.keys.size, jsonMapper.encodeToString(searchTerms))
        mods.map { (id, mod) ->
            id to mod.isDisplayed(enable, missing, searchId, endorsed, unendorsed, searchTerms, currentSearch.lowercase(), getChanges())
        }.forEach { (id, shown) -> if (shown) modDoms[id]?.removeClass("hidden") else modDoms[id]?.addClass("hidden") }
    }
}

private fun Set<String>.parseFlag(term: String): Boolean? {
    if (contains(term)) return true
    else if (contains("-$term")) return false
    return null
}

private fun Mod.isDisplayed(
    enabled: Boolean?,
    missing: Boolean?,
    id: String?,
    endorsed: Boolean?,
    unendorsed: Boolean,
    terms: Map<SearchType, Set<String>>,
    search: String,
    changes: Changes
): Boolean {
    return (enabled != null && enabled == this.enabled) ||
            (missing != null && missing == (this.id == null)) ||
            (id != null && this.id?.toString()?.contains(id) ?: false) ||
            (endorsed != null && endorsed == this.endorsed) || (unendorsed && this.endorsed == null) ||
            stringSearch(terms, search, changes)
}

private fun Mod.stringSearch(terms: Map<SearchType, Set<String>>, search: String, changes: Changes): Boolean {
    return (search.isNotBlank() && name.contains(search))
            || SearchType.CATEGORY.search(terms) { category()?.lowercase()?.contains(it) ?: false }
            || SearchType.TAG.search(terms) { allTags(changes).any { tag -> tag.lowercase().contains(it) } }
            || SearchType.NAME.search(terms) { name.lowercase().contains(it) }
}

private fun SearchType.search(terms: Map<SearchType, Set<String>>, eval: (String) -> Boolean): Boolean {
    return terms[this]?.any { term ->
        if (term.startsWith("-")) {
            !eval(term.substring(1))
        } else eval(term)
    } ?: false
}
