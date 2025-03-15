package org.manapart.pages

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

fun modListView() {
    updateUrl("mods")
    mods = getMods().associateBy { it.uniqueId() }
    replaceElement {
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
            mods.values.forEach { modView(it) }
        }
        div { id = "tag-modal" }
    }
    modDoms = mods.keys.associateWith { el("mod-$it") }
    changesView()
}

fun showMod(mod: Mod, display: Boolean) {
    val id = mod.uniqueId()
    modDoms[id]?.let { e ->
        if (display && !getChanges().deletes.contains(id)) e.removeClass("hidden") else e.addClass("hidden")
    }
}

fun TagConsumer<HTMLElement>.tableRow(header: String, value: String) {
    tr {
        td { +header }
        td { +value }
    }
}

fun String.capitalizeWords() = this.split(" ").joinToString(" ") { it.capitalize() }
