package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.manapart.Mod

private enum class SearchType { NAME, CATEGORY, TAG, ALL }

fun searchMods(mods: Map<String, Mod>, search: String){
    //TODO - calculate flags
    console.log("serching ",search, mods.keys.size, modDoms.keys.size)
    mods.map { (id, mod) -> id to mod.isDisplayed(null, null, null, null, false, SearchType.ALL, search) }.forEach { (id, shown) -> if (shown) modDoms[id]?.removeClass("hidden") else modDoms[id]?.addClass("hidden") }
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