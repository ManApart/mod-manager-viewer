package org.manapart.pages

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.TagConsumer
import kotlinx.html.id
import kotlinx.html.js.*
import kotlinx.html.table
import org.manapart.el
import org.manapart.getMods
import org.manapart.replaceElement
import org.manapart.updateUrl
import org.w3c.dom.HTMLElement

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
            hr {  }
            getMods().forEach { mod ->
                div("modRow") {
                    div("nameRow") {
                        +mod.name.capitalizeWords()
                        onClickFunction = {
                            val stats = el("${mod.id ?: mod.name}-stats-row")
                            val toggle = !stats.className.contains("minimized")
                            if (toggle) stats.addClass("minimized") else stats.removeClass("minimized")
                        }
                    }
                    div("statsRow minimized") {
                        id = "${mod.id ?: mod.name}-stats-row"

                        table("statsRowTable") {
                            tr("idRow") {
                                td { +"Id" }
                                td { +(mod.id?.toString() ?: "?") }
                                onClickFunction = { window.open("https://www.nexusmods.com/starfield/mods/${mod.id}", "_blank")}
                            }

                            val needsUpdate = if (mod.version != mod.latestVersion && mod.latestVersion != null) UPDATE else ""
                            tableRow("Version", needsUpdate + (mod.version ?: "?"))
                            tableRow("Load Order", mod.loadOrder.toString())
                            tableRow("Enabled", if (mod.enabled) ENABLED else "")
                            tableRow("Endorsed", when (mod.endorsed) {
                                true -> THUMBS_UP
                                false -> THUMBS_DOWN
                                else -> ""
                            })
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

fun TagConsumer<HTMLElement>.tableRow(header: String, value: String) {
    tr {
        td { +header }
        td { +value }
    }
}

fun String.capitalizeWords() = this.split(" ").joinToString(" ") { it.capitalize() }
