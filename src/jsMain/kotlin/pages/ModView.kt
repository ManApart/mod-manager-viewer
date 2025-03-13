package org.manapart.pages

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
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
            div { id = "changes" }
            controlsMenu(mods, modDoms)
            div {
                id = "mod-list"
                mods.values.forEach { mod ->
                    val classes = if (getChanges().deletes.contains(mod.uniqueId())) "hidden modRow" else "modRow"
                    div(classes) {
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
                                    changesView()
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
    changesView()
}

fun showMod(mod: Mod, display: Boolean) {
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
