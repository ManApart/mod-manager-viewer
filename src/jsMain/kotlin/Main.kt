package org.manapart

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.createElement
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.manapart.pages.loadInitialData
import org.manapart.pages.modListView
import org.manapart.pages.uploadView
import org.w3c.dom.HTMLElement

val jsonMapper = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

//TODO link between static pages and viewer and back
//TODO keyboard shortcuts for entry, escaping, etc
fun main() {
    window.onload = {
        createDB()
        loadInitialData().then {
            println("Loaded ${getMods().size} mods")
            modListView()
        }
    }
}

fun el(id: String) = document.getElementById(id) as HTMLElement
fun <T> el(id: String) = document.getElementById(id) as T


fun replaceElement(id: String = "root", rootClasses: String? = null, newHtml: TagConsumer<HTMLElement>.() -> Unit) {
    val root = el<HTMLElement?>(id)
    if (root != null) {
        val newRoot = document.createElement("div") {
            this.id = id
            rootClasses?.split(" ")?.forEach {
                this.addClass(it)
            }
        }
        newRoot.append {
            newHtml()
        }
        root.replaceWith(newRoot)
    }
}
