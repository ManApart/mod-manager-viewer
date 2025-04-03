package org.manapart.pages

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
            div {
                id = "import-code"
                code {
                    +jsonMapper.encodeToString(changes)
                }
            }
        }
    }
}

private fun addMod(changes: Changes) {
    val id = el<HTMLInputElement>("add-mod-input").value.toIntOrNull()
    if (id != null && !getMods().map { it.id }.contains(id)) {
        console.log(id)
        changes.adds.add(id)
        changesView()
        persistMemory()
    }
}
