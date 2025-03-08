package org.manapart.pages

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.*
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.h1
import kotlinx.html.js.hr
import kotlinx.html.js.img
import kotlinx.html.js.p
import kotlinx.html.js.span
import kotlinx.html.js.td
import kotlinx.html.js.tr
import org.manapart.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

const val THUMBS_UP = "\uD83D\uDC4D"
const val THUMBS_DOWN = "\uD83D\uDC4E"
const val ENABLED = "🔗"
const val UPDATE = "\uD83D\uDCE9"

fun modView() {
    updateUrl("mods")
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
            val mods = getMods().associateBy { it.uniqueId() }
            controlsMenu(mods)
            mods.values.forEach { mod ->
                div("modRow") {
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
                        id = "${mod.id ?: mod.name}-stats-row"

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

fun TagConsumer<HTMLElement>.controlsMenu(mods: Map<String, Mod>) {
    div {
        id = "controls"
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/collapse.svg") { }
                +"Collapse"
            }
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Enabled"
            }
            button {
                img(classes = "icon", src = "./assets/filter.svg") { }
                +"Endorsed"
            }
        }
        div("control-row") {
            button {
                img(classes = "icon", src = "./assets/sort.svg") { }
                +"Alpha"
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

fun TagConsumer<HTMLElement>.tableRow(header: String, value: String) {
    tr {
        td { +header }
        td { +value }
    }
}

fun String.capitalizeWords() = this.split(" ").joinToString(" ") { it.capitalize() }
