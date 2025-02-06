package org.manapart

import kotlinx.browser.window
import org.manapart.pages.loadInitialData

val jsonMapper = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

fun main() {
    println("Starting!")
    window.onload = {
        println("on Load triggered")
        createDB()
        loadInitialData().then { println("Loaded ${getMods().size} mods") }
        //get some example mod data and use that as an example
        //load and process based on that data
        //add ability to upload your own data
        //deploy as own page on mod manager site
        //link between static pages and viewer and back
    }
}
