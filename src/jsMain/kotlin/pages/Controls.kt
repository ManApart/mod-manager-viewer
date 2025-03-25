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

private enum class FilterState(val show: (Boolean?) -> Boolean) {
    ONLY({ it == true }),
    NONE({ it != true }),
    ANY({ true });

    fun next() = when (this) {
        ONLY -> NONE
        NONE -> ANY
        ANY -> ONLY
    }
}

fun TagConsumer<HTMLElement>.controlsMenu() {
    val mods = getMods().associateBy { it.uniqueId() }
    div {
        id = "controls"
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Enabled"
                var filter = FilterState.ANY
                onClickFunction = {
                    filter = filter.next()
                    mods.values.forEach { mod ->
                        showMod(mod, filter.show(mod.enabled))
                    }
                }
            }
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Endorsed"
                var filter = FilterState.ANY
                onClickFunction = {
                    filter = filter.next()
                    mods.values.forEach { mod ->
                        showMod(mod, filter.show(mod.endorsed))
                    }
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
