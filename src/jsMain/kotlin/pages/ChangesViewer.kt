package org.manapart.pages

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyUpFunction
import org.manapart.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent

fun changesView() {
    replaceElement("changes") {
        val changes = getChanges()
        div {
            id = "changes-added"
            +"Added: "
            changes.adds.forEach { add ->
                span("change-item") {
                    a("https://www.nexusmods.com/starfield/mods/$add", target = "_blank") { +add.toString() }
                    button {
                        +"X"
                        onClickFunction = {
                            getChanges().adds.remove(add)
                            changesView()
                            persistMemory()
                        }
                    }
                }
            }
            span {
                id = "add-mod"
                input(InputType.text) {
                    id = "add-mod-input"
                    placeholder = "Mod Id"

                    onKeyUpFunction = { key ->
                        if ((key as KeyboardEvent).key == "Enter") {
                            addMod(changes)
                        }
                    }
                }
                button {
                    +"+"
                    onClickFunction = {
                        addMod(changes)
                    }
                }
            }
            span("notification") { id = "add-notification" }
        }
        if (changes.deletes.isNotEmpty()) {
            div {
                id = "changes-removed"
                +"Removed: "
                val mods = getMods().associateBy { it.uniqueId() }
                changes.deletes.forEach { delete ->
                    div("change-item") {
                        val name = mods[delete]?.name ?: delete
                        +"$name "
                        button {
                            +"X"
                            onClickFunction = {
                                getChanges().deletes.remove(delete)
                                modListView()
                                persistMemory()
                            }
                        }
                    }
                }
            }
        }
        div {
            id = "changes-import"
            +"Import: "
            button {
                img(classes = "btn-icon", src = "./assets/copy.svg")
                onClickFunction = {
                    window.navigator.clipboard.writeText(el("import-code").innerText)
                    playNotification("copy-confirmation", "Copied!")
                }
            }
            span("notification") { id = "copy-confirmation" }
            div {
                id = "import-code"
                code {
                    +jsonMapper.encodeToString(changes)
                }
            }
        }
    }
}

private fun playNotification(element: String, message: String) {
    with(el(element)) {
        innerText = message
        removeClass("play-notification")
        offsetWidth
        addClass("play-notification")
    }
}

private fun addMod(changes: Changes) {
    val id = el<HTMLInputElement>("add-mod-input").value.between("/", "?").toIntOrNull()
    when {
        id == null -> playNotification("add-notification", "Couldn't parse Id!")
        changes.adds.contains(id) || getMods().map { it.id }.contains(id) -> playNotification("add-notification", "Already Exists!")
        else -> {
            changes.adds.add(id)
            changesView()
            persistMemory()
            playNotification("add-notification", "Added $id!")
        }
    }
}

private fun String.between(prefix: String, suffix: String): String {
    val start = lastIndexOf(prefix) + 1
    val end = indexOf(suffix, start).let { if (it == -1) length else it }
    return substring(start, end)
}
