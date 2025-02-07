package org.manapart

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.createElement
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.manapart.pages.loadInitialData
import org.manapart.pages.modView
import org.manapart.pages.uploadView
import org.w3c.dom.HTMLElement

val jsonMapper = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

fun main() {
    println("Starting!")
    window.onload = {
        println("on Load triggered")
        createDB()
        loadInitialData().then {
            println("Loaded ${getMods().size} mods")
            doRouting()
        }
        //get some example mod data and use that as an example
        //load and process based on that data
        //add ability to upload your own data
        //deploy as own page on mod manager site
        //link between static pages and viewer and back
    }
}


fun doRouting(windowHash: String = window.location.hash) {
    when {
        windowHash.startsWith("#upload") -> uploadView()
        else -> modView()
    }
}

fun updateUrl(path: String) {
    val pathName = path.split("/").first().capitalize()
    if (!window.location.href.endsWith("#$path")) {
        window.history.pushState(null, "", "#$path")
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
