package org.manapart.pages

import kotlinx.html.js.div
import kotlinx.html.js.h1
import org.manapart.getMods
import org.manapart.replaceElement

fun modView() {
    replaceElement {
        div {
            h1 { +"Mod List" }
            getMods().forEach { mod ->
                div { +"${mod.name}" }
            }
        }
    }
}
