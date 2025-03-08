package org.manapart.pages

import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.TagConsumer
import kotlinx.html.id
import kotlinx.html.js.*
import kotlinx.html.span
import kotlinx.html.table
import org.manapart.el
import org.manapart.getMods
import org.manapart.replaceElement
import org.manapart.updateUrl
import org.w3c.dom.HTMLElement

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
                            tableRow("Id", mod.id?.toString() ?: "?")
                            tableRow("Version", mod.version ?: "?")
                            tableRow("Load Order", mod.loadOrder.toString())
                            tableRow("Enabled", mod.enabled.toString())
                            tableRow("Endorsed", mod.endorsed?.toString() ?: "")
                            tableRow("Category", mod.categoryId?.toString() ?: "")
                            val tagContent = if (mod.tags.isNotEmpty()) mod.tags.joinToString() else ""
                            tableRow("Tags", tagContent)
                        }
//                        //TODO - show update icon
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
