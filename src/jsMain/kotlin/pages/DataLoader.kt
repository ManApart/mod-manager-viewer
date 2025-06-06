package org.manapart.pages

import org.manapart.*
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Json
import kotlin.js.Promise

fun loadInitialData(): Promise<*> {
    return loadMemory().then {
        if (getMods().isEmpty()) {
            loadJson("exampleData.json").then { json ->
                loadFromJson("starfield-data.json", JSON.stringify(json))
            }.catch { e ->
                println("Failed to load example data ${JSON.stringify(e)}")
            }
        }
    }
}

fun loadFromJson(fileName: String, json: String) {
    val game = if (fileName.startsWith("starfield")) GameMode.STARFIELD else GameMode.OBLIVION_REMASTERED
    if (fileName.endsWith("config.json")) {
        jsonMapper.decodeFromString<Config>(json).parseKeys().let {  saveCategories(game, it)}
    } else {
        updateInMemoryStorage(game, jsonMapper.decodeFromString<DataJson>(json))
    }
    persistMemory()
}


@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
private fun loadJson(url: String): Promise<Json> {
    return Promise { resolve, _ ->
        XMLHttpRequest().apply {
            open("GET", url)
            responseType = XMLHttpRequestResponseType.JSON
            onerror = { println("Failed to get Json") }
            onload = {
                resolve(response as Json)
            }
            send()
        }
    }
}
