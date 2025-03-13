package org.manapart.pages

import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.id
import org.manapart.getChanges
import org.manapart.replaceElement

fun changesView() {
    replaceElement("changes") {
        div {
            id = "changes"

            div {
                id = "changes-added"
                +"Added:"
                code { +getChanges().adds.joinToString(" ") }
            }
            div {
                id = "changes-removed"
                +"Removed:"
                code { +getChanges().deletes.joinToString(" ") }
            }
            div {
                div {
                    id = "changes-tags-added"
                    +"Tags Added:"
                }
                div {
                    id = "changes-tags-removed"
                    +"Tags Removed:"
                }
            }
        }
    }
}
