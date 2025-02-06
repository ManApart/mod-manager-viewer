package org.manapart.pages

import org.manapart.*
import org.w3c.xhr.JSON
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Json
import kotlin.js.Promise

fun loadInitialData(): Promise<*> {
    return hasMemory().then { hasData ->
        if (hasData) {
            loadMemory()
        } else {
            loadJson("exampleData.json").then { json ->
                JSON.stringify(json)
                    .let { jsonMapper.decodeFromString<InMemoryStorage>(it) }
                    .let { updateInMemoryStorage(it) }
                persistMemory()
            }
        }
    }
}


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
