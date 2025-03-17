package org.manapart.pages

import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.js.*
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get

fun TagConsumer<HTMLElement>.uploadView() {
    div {
        p { +"Upload your data.json and config.json to track your mods" }
        label("upload-button") {
            +"Upload Data"
            input(InputType.file) {
                id = "import-input"
                style = "display: none"
                onChangeFunction = {
                    val element = document.getElementById(id) as HTMLInputElement
                    if (element.files != undefined) {
                        val file = element.files!![0]!!
                        val reader = FileReader()
                        reader.onload = {
                            loadFromJson(file.name, reader.result as String)
                            modListView()
                        }
                        reader.onerror = { error ->
                            console.error("Failed to read File $error")
                        }
                        reader.readAsText(file)
                    }
                }
            }
        }
    }
}
