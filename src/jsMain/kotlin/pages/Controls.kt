package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
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
                button {
                    +"?"
                    onClickFunction = {
                        val help = el("search-help")
                        if (help.hasClass("hidden")) {
                            help.removeClass("hidden")
                        } else help.addClass("hidden")
                    }
                }
                +"Search: "
                input(classes = "search") {
                    id = "search"
                    placeholder = "Filter: Name, Categories, Tags. - excludes"
                    value = ""
                    onKeyUpFunction = { key ->
                        val search = el<HTMLInputElement>("search")
                        when {
                            (key as KeyboardEvent).key == "Enter" -> {
                                parseSearchTerm(search.value)
                                search.value = ""
                                currentSearch = ""
                                updateSearchTerms()
                                searchMods(mods)
                            }

                            currentSearch.isBlank()
                                    && search.value.length == 1
                                    && search.value.toIntOrNull() == null
                                -> {
                                //Don't search on single character, unless number
                            }

                            else -> {
                                currentSearch = search.value
                                if (currentSearch.isBlank() || (!currentSearch.contains(":") && SearchType.entries.none { it.name.lowercase().contains(currentSearch.lowercase()) })) {
                                    searchMods(mods)
                                }
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
                searchHelp()
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

private fun DIV.searchHelp() {
    div("hidden") {
        id = "search-help"
        ul {
            li { +"Search for a term in the title" }
            li { +"Press enter to search for a specific property term" }
            li {
                +"Use "
                code { +"-" }
                +" to exclude from search"
            }
            li { +"Click a property to exclude/include that property" }
        }
        p {

        }
        table {
            tr {
                td { +"Term" }
                td { +"Explanation" }
            }
            tr {
                td { +"1234" }
                td { +"Search mod id" }
            }
            tr {
                td { +"Better" }
                td {
                    +"Show all mods with "
                    code { +"better" }
                    +" in the title"
                }
            }
            tr {
                td { +"tag:try" }
                td {
                    +"Show all mods tagged with the "
                    code { +"try" }
                    +" tag"
                }
            }
            tr {
                td { +"category:misc" }
                td {
                    +"Show all mods in a category whose name contains "
                    code { +"misc" }
                }
            }
            tr {
                td { +"enabled" }
                td { +"Show only enabled mods" }
            }
            tr {
                td { +"-endorsed" }
                td { +"Exclude endorsed mods" }
            }
            tr {
                td { +"missing" }
                td { +"Show mods that lack ana id" }
            }
        }
    }
}
