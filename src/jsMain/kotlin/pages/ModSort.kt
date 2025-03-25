package org.manapart.pages

import org.manapart.Mod


enum class ModSort(val sort: (Collection<Mod>, Boolean) -> List<Mod>) {
    ALPHA({ modList, asc -> modList.doSort(asc) { it.name } }),
    CATEGORY({ modList, asc -> modList.doSort(asc) { it.category() + it.name } }),
    LOAD({ modList, asc -> modList.doSort(asc) { it.loadOrder } }),
}

private var modSort = ModSort.LOAD
private var modSortAsc = true


fun sortMods(kind: ModSort) {
    when {
        modSort != kind -> {
            modSort = kind
            modSortAsc = true
        }

        modSort == kind && modSortAsc -> {
            modSortAsc = false
        }

        else -> {
            modSortAsc = true
        }
    }
    modListView()
}

fun Collection<Mod>.sorted(): List<Mod> {
    return modSort.sort(this, modSortAsc)
}

private inline fun <R: Comparable<R>> Collection<Mod>.doSort(asc: Boolean, crossinline selector: (Mod) -> R?): List<Mod> {
    return if (asc) sortedBy(selector) else sortedByDescending(selector)
}
