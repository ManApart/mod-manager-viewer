package org.manapart.pages

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import kotlinx.html.js.tr
import org.manapart.*
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

const val THUMBS_UP = "\uD83D\uDC4D"
const val THUMBS_DOWN = "\uD83D\uDC4E"
const val ENABLED = "🔗"
const val UPDATE = "\uD83D\uDCE9"

private var modDoms = mapOf<String, Element>()
private var mods = mapOf<String, Mod>()

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

fun modView() {
    updateUrl("mods")
    mods = getMods().associateBy { it.uniqueId() }
    replaceElement {
        div {
            id = "mods"
            h1 { +"Mod Viewer" }
            p {
                button {
                    id = "upload-button"
                    +"Upload"
                    onClickFunction = { uploadView() }
                }
                +"your data.json to view your mod list on the go!"
            }

            controlsMenu()
            div {
                id = "mod-list"
                mods.values.forEach { mod ->
                    div("modRow") {
                        id = "mod-${mod.uniqueId()}"
                        val needsUpdate = if (mod.version != mod.latestVersion && mod.latestVersion != null) UPDATE else ""
                        div("nameRow") {
                            val enabled = if (mod.enabled) ENABLED else ""
                            val endorsed = when (mod.endorsed) {
                                true -> THUMBS_UP
                                false -> THUMBS_DOWN
                                else -> ""
                            }

                            span("modName") { +mod.name.capitalizeWords() }
                            span("modEmojis") { +(" $enabled$endorsed$needsUpdate") }

                            onClickFunction = {
                                val stats = el("${mod.uniqueId()}-stats-row")
                                val toggle = !stats.className.contains("minimized")
                                if (toggle) stats.addClass("minimized") else stats.removeClass("minimized")
                            }
                        }
                        div("statsRow minimized") {
                            id = "${mod.uniqueId()}-stats-row"
                            button {
                                +"Remove"
                                onClickFunction = {
                                    getChanges().deletes.add(mod.uniqueId())
                                    el("mod-${mod.uniqueId()}").addClass("hidden")
                                    persistMemory()
                                }
                            }
                            span {
                                +"Todo"
                                checkBoxInput { }
                            }

                            table("statsRowTable") {
                                val externalLink = if (mod.id != null) " externalLink" else ""
                                tr("idRow$externalLink") {
                                    td { +"Id" }
                                    td { +(mod.id?.toString() ?: "?") }
                                    onClickFunction = { if (mod.id != null) window.open("https://www.nexusmods.com/starfield/mods/${mod.id}", "_blank") }
                                }

                                tableRow("Version", needsUpdate + (mod.version ?: "?"))
                                tableRow("Load Order", mod.loadOrder.toString())
                                tableRow("Category", mod.categoryId?.toString() ?: "")
                                val tagContent = if (mod.tags.isNotEmpty()) mod.tags.joinToString() else ""
                                tableRow("Tags", tagContent)
                            }
                        }
                    }
                }
            }
        }
    }
    modDoms = mods.keys.associateWith { el("mod-$it") }
}

fun TagConsumer<HTMLElement>.controlsMenu() {
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

fun showMod(mod: Mod, display: Boolean){
    val id = mod.uniqueId()
    modDoms[id]?.let { e ->
        if (display && !getChanges().adds.contains(id)) e.removeClass("hidden") else e.addClass("hidden")
    }
}

fun TagConsumer<HTMLElement>.tableRow(header: String, value: String) {
    tr {
        td { +header }
        td { +value }
    }
}

fun String.capitalizeWords() = this.split(" ").joinToString(" ") { it.capitalize() }
