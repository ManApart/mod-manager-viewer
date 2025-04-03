package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.manapart.Mod

private enum class SearchType { NAME, CATEGORY, TAG, ALL }

var currentSearch: String = ""
val searchTerms: MutableSet<String> = mutableSetOf()

fun searchMods(mods: Map<String, Mod>) {
    val searchId = searchTerms.firstNotNullOfOrNull { it.toIntOrNull() }?.toString()
    val endorsed = if(searchTerms.contains("endorsed")) true else null
    val unendorsed = if(searchTerms.contains("unendorsed") || searchTerms.contains("-endorsed")) true else false

    if (currentSearch.isBlank() && searchTerms.isEmpty()) modDoms.values.forEach { it.removeClass("hidden") } else {
        console.log("searching ", "'$currentSearch'", mods.keys.size, modDoms.keys.size, searchTerms)
        mods.map { (id, mod) -> id to mod.isDisplayed(parseFlag("enabled"), parseFlag("missing"), searchId, endorsed, unendorsed, SearchType.ALL, currentSearch.lowercase()) }
            .forEach { (id, shown) -> if (shown) modDoms[id]?.removeClass("hidden") else modDoms[id]?.addClass("hidden") }
    }
}

private fun parseFlag(term: String): Boolean? {
    if (searchTerms.contains(term)) return true
    else if (searchTerms.contains("-$term")) return false
    return null
}

private fun Mod.isDisplayed(
    enabled: Boolean?,
    missing: Boolean?,
    id: String?,
    endorsed: Boolean?,
    unendorsed: Boolean,
    searchType: SearchType,
    search: String
): Boolean {
    return (enabled != null && enabled == this.enabled) ||
            (missing != null && missing == (this.id == null)) ||
            (id != null && this.id?.toString()?.contains(id) ?: false) ||
            (endorsed != null && endorsed == this.endorsed) || (unendorsed && this.endorsed == null) ||
            (search.isNotBlank() && stringSearch(searchType, search))
}

private fun Mod.stringSearch(kind: SearchType, search: String): Boolean {
    return when (kind) {
        SearchType.NAME -> name.contains(search)
        SearchType.CATEGORY -> category()?.lowercase()?.contains(search) ?: false
        SearchType.TAG -> tags.any { tag -> tag.lowercase().contains(search) }
        else -> {
            name.contains(search) || (category()?.lowercase()?.contains(search) ?: false) || tags.any { tag -> tag.lowercase().contains(search) }
        }
    }
}
