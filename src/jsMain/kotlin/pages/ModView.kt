package org.manapart.pages

import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import org.manapart.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent


fun TagConsumer<HTMLElement>.modView(mod: Mod) {
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
            button {
                +"Reset"
                onClickFunction = {
                    with(getChanges()) {
                        tagsAdded[mod.uniqueId()]?.clear()
                        tagsRemoved[mod.uniqueId()]?.clear()
                    }
                    refreshTags(mod)
                    persistMemory()
                }
            }

            table("statsRowTable") {
                val externalLink = if (mod.id != null) " externalLink" else ""
                tr("idRow$externalLink") {
                    td { +"Id" }
                    td { +(mod.id?.toString() ?: "?") }
                    onClickFunction = {
                        if (mod.id != null) window.open(
                            "https://www.nexusmods.com/${currentMode().pathName}/mods/${mod.id}",
                            "_blank"
                        )
                    }
                }

                tableRow("Version", needsUpdate + (mod.version ?: "?"))
                tableRow("Load Order", mod.loadOrder.toString())
                tableRow("Category", mod.category() ?: "")
                tr {
                    td { +"Tags" }
                    td {
                        id = "${mod.uniqueId()}-tags"
                        tagContent(mod)
                    }
                }
            }
        }
    }
}

private fun refreshTags(mod: Mod) = replaceElement("${mod.uniqueId()}-tags") { tagContent(mod) }

private fun TagConsumer<HTMLElement>.tagContent(mod: Mod) {
    val changes = getChanges()
    val del = changes.tagsRemoved[mod.uniqueId()] ?: setOf()
    val add = changes.tagsAdded[mod.uniqueId()] ?: mutableSetOf()
    (mod.tags.filter { !del.contains(it) } + add).forEach { tag ->
        span("change-item") {
            +tag
            button {
                +"X"
                onClickFunction = {
                    if (add.contains(tag)) add.remove(tag) else changes.tagsRemoved[mod.uniqueId()]
                    refreshTags(mod)
                    changesView()
                    persistMemory()
                }
            }
        }
    }
    span {
        id = "${mod.uniqueId()}-add-tag"
        button {
            +"+"
            onClickFunction = { tagModal(mod) }
        }
    }
}

private fun tagModal(mod: Mod) {
    replaceElement("tag-modal") {
        val excluded = mod.tags + (getChanges().tagsAdded[mod.uniqueId()] ?: setOf())
        val tagChoices = (getMods().flatMap { it.tags } + getChanges().tagsAdded.values.flatten()).toSet() - excluded
        div {
            id = "tag-modal-content"
            tagChoices.forEach { option ->
                button {
                    +option
                    onClickFunction = {
                        addTag(mod, option)
                        refreshTags(mod)
                        replaceElement("tag-modal") {}
                        el("mod-list").removeClass("blur")
                    }
                }
            }
            span {
                id = "add-tag"
                input(InputType.text) {
                    id = "add-tag-input"
                    placeholder = "New Tag"
                    onKeyUpFunction = { key ->
                        if ((key as KeyboardEvent).key == "Enter") {
                            addNewTag(mod)
                        } else if (key.key == "Escape") {
                            replaceElement("tag-modal") {}
                            el("mod-list").removeClass("blur")
                        }
                    }
                }
                button {
                    +"+"
                    onClickFunction = { addNewTag(mod) }
                }
            }
        }
    }
    el("add-tag-input").focus()
    el("mod-list").addClass("blur")
    val root = el<HTMLDivElement>("root")
    root.onclick = { event ->
        if (event.target !is HTMLButtonElement && event.target !is HTMLInputElement) {
            root.onclick = "".asDynamic()
            replaceElement("tag-modal") {}
            el("mod-list").removeClass("blur")
        }
    }
}

private fun addNewTag(mod: Mod) {
    val newTag = el<HTMLInputElement>("add-tag-input").value
    if (newTag.isNotBlank()) {
        addTag(mod, newTag)
        refreshTags(mod)
        changesView()
        controlsMenu()
        replaceElement("tag-modal") {}
        el("mod-list").removeClass("blur")
    }
}
