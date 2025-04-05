package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.html.*
import kotlinx.html.js.*
import org.manapart.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.get

fun controlsMenu() {
    replaceElement("controls") {
        val mods = getMods().associateBy { it.uniqueId() }
        div {
            id = "controls"
            div("control-row") {
                button {
                    img(classes = "icon", src = "./assets/filter.svg") { }
                    +"Enabled"
                    onClickFunction = {
                        searchTerms[SearchType.PROPERTY]?.add("enabled")
                        updateSearchTerms()
                        searchMods(mods)
                    }
                }
                button {
                    img(classes = "icon", src = "./assets/filter.svg") { }
                    +"Endorsed"
                    onClickFunction = {
                        searchTerms[SearchType.PROPERTY]?.add("endorsed")
                        updateSearchTerms()
                        searchMods(mods)
                    }
                }
                select {
                    id = "category-select"
                    getCategories().entries.sortedBy { it.value }.forEach { (catId, name) ->
                        option { +name }
                    }
                    onChangeFunction = {
                        val select = el<HTMLSelectElement>("category-select")
                        select.selectedOptions[0]?.textContent?.let { term ->
                            searchTerms[SearchType.CATEGORY]?.add(term.lowercase())
                            updateSearchTerms()
                            searchMods(mods)
                        }
                    }
                }
                select {
                    id = "tag-select"
                    val tagChoices = (getMods().flatMap { it.tags } + getChanges().tagsAdded.values.flatten()).toSet()
                    tagChoices.map { it.capitalizeWords() }.sorted().forEach { option { +it } }
                    onChangeFunction = {
                        val select = el<HTMLSelectElement>("tag-select")
                        select.selectedOptions[0]?.textContent?.let { term ->
                            searchTerms[SearchType.TAG]?.add(term.lowercase())
                            updateSearchTerms()
                            searchMods(mods)
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
                            parseSearchTerm(search.value)
                            search.value = ""
                            currentSearch = ""
                            updateSearchTerms()
                            searchMods(mods)
                        } else {
                            currentSearch = search.value
                            if (currentSearch.isBlank() || (!currentSearch.contains(":") && SearchType.entries.none { it.name.lowercase().contains(currentSearch.lowercase()) })) {
                                searchMods(mods)
                            }
                        }
                    }
                }
                button {
                    +"X"
                    onClickFunction = {
                        val search = el<HTMLInputElement>("search")
                        search.value = ""
                        currentSearch = ""
                        searchMods(mods)
                    }
                }
                div { id = "search-terms" }
            }
            hr { }
        }
    }
}

private fun updateSearchTerms() {
    replaceElement("search-terms") {
        searchTerms.entries.filter { it.value.isNotEmpty() }.forEach { (kind, terms) ->
            div {
                +(kind.name.capitalizeWords() + ": ")
                terms.forEach { term ->
                    button {
                        val text = if (term.startsWith("-")) "Not ${term.substring(1)}" else term
                        +text
                        onClickFunction = {
                            console.log("click", term)
                            if (term.startsWith("-")) {
                                searchTerms[kind]?.remove(term)
                                searchTerms[kind]?.add(term.substring(1))
                            } else {
                                searchTerms[kind]?.remove(term)
                                searchTerms[kind]?.add("-$term")
                            }
                            updateSearchTerms()
                            searchMods(getMods().associateBy { it.uniqueId() })
                        }
                    }
                    button {
                        +"X"
                        onClickFunction = {
                            searchTerms[kind]?.remove(term)
                            updateSearchTerms()
                            console.log(kind, term)
                            searchMods(getMods().associateBy { it.uniqueId() })
                        }
                    }
                }
            }
        }
    }
}
