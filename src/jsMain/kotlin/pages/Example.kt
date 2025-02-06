package org.manapart.pages

import org.manapart.*
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Json
import kotlin.js.Promise

fun loadInitialData(): Promise<*> {
    return loadMemory().then {
        if (getMods().isNotEmpty()) {
            println("Loaded Memory")
        } else {
            println("Loading example")
            loadJson("exampleData.json").then { json ->
                JSON.stringify(json)
                    .let { jsonMapper.decodeFromString<InMemoryStorage>(it) }
                    .let { updateInMemoryStorage(it) }
                persistMemory()
            }.catch { e ->
                println("Failed to load example data ${JSON.stringify(e)}")
            }
        }
    }
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
