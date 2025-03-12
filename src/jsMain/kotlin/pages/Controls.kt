package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import org.manapart.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Json
import kotlin.js.Promise

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

fun TagConsumer<HTMLElement>.controlsMenu(mods: Map<String, Mod>, modDoms: Map<String, Element>) {
    div {
        id = "controls"
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/collapse.svg") { }
                +"Collapse"
                onClickFunction = {
                    mods.keys.forEach { el("$it-stats-row").addClass("minimized") }
                }
            }
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
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Alpha"
//                el("mod").append(modDoms.values.first())
                onClickFunction = {
                    replaceElement("mod-list"){
                        div {
                            id = "mod-list"
                            modDoms.values.forEach { mod ->
                                //todo - append mod dom
                            }
                        }
                    }
                }
            }
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Load"
            }
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Category"
            }
        }
        div("control-row") {
            +"Search: "
            input(classes = "search") {
                id = "search"
                placeholder = "Filter: Name, Categories, Tags. Comma separated"
                value = ""
                onKeyUpFunction = {
//                    planetSearchOptions.searchText = el<HTMLInputElement>("search").value
//                    searchPlanets()
                }
            }
        }
        hr { }
    }
}
