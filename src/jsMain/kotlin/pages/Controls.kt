package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.html.*
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import org.manapart.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent

private fun next(term: String) {
    with(searchTerms) {
        when {
            contains("-$term") -> remove("-$term")
            contains(term) -> {
                remove(term)
                add("-$term")
            }
            else -> add(term)
        }
    }
    updateSearchTerms()
}

fun TagConsumer<HTMLElement>.controlsMenu() {
    val mods = getMods().associateBy { it.uniqueId() }
    div {
        id = "controls"
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Enabled"
                onClickFunction = {
                    next("enabled")
                    searchMods(mods)
                }
            }
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Endorsed"
                onClickFunction = {
                    next("endorsed")
                    searchMods(mods)
                }
            }
        }
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/collapse.svg") { }
                +"Collapse"
                onClickFunction = {
                    mods.keys.forEach { el("$it-stats-row").addClass("minimized") }
                }
            }
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Alpha"
                onClickFunction = { sortMods(ModSort.ALPHA) }
            }
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Load"
                onClickFunction = { sortMods(ModSort.LOAD) }
            }
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Category"
                onClickFunction = { sortMods(ModSort.CATEGORY) }
            }
        }
        div("control-row") {
            +"Search: "
            input(classes = "search") {
                id = "search"
                placeholder = "Filter: Name, Categories, Tags. - excludes"
                value = ""
                onKeyUpFunction = { key ->
                    val search = el<HTMLInputElement>("search")
                    if ((key as KeyboardEvent).key == "Enter") {
                        searchTerms.add(search.value)
                        search.value = ""
                        updateSearchTerms()
                    } else {
                        currentSearch = search.value
                    }
                    searchMods(mods)
                }
            }
            div { id = "search-terms" }
        }
        hr { }
    }
}

private fun updateSearchTerms() {
    replaceElement("search-terms") {
        searchTerms.forEach { term ->
            button {
                +term
                onClickFunction = {
                    searchTerms.remove(term)
                    updateSearchTerms()
                    searchMods(getMods().associateBy { it.uniqueId() })
                }
            }
        }
    }
}
