package org.manapart.pages

import kotlinx.html.js.div
import kotlinx.html.js.h1
import kotlinx.html.js.p
import org.manapart.getMods
import org.manapart.replaceElement

fun modView() {
    replaceElement {
        div {
            h1 { +"Mod Viewer" }
            p { +"Upload your data.json to view your mod list on the go!" }
            //TODO - link to upload page
            getMods().forEach { mod ->
                div { +"${mod.name}" }
            }
        }
    }
}
