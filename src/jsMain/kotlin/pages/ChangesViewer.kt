package org.manapart.pages

import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.manapart.*
import org.w3c.dom.HTMLInputElement

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
                }
                button {
                    +"+"
                    onClickFunction = {
                        val id = el<HTMLInputElement>("add-mod-input").value.toIntOrNull()
                        if (id != null && !getMods().map { it.id }.contains(id)) {
                            console.log(id)
                            changes.adds.add(id)
                            changesView()
                            persistMemory()
                        }
                    }
                }
            }
        }
        if (changes.deletes.isNotEmpty()) {
            div {
                id = "changes-removed"
                +"Removed: "
                changes.deletes.forEach { delete ->
                    span("change-item") {
                        +delete
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
