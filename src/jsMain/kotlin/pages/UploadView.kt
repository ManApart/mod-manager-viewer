package org.manapart.pages

import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.*
import org.manapart.replaceElement
import org.manapart.updateUrl
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import org.w3c.files.get

fun uploadView() {
    updateUrl("upload")
    replaceElement {
        div {
            h1 { +"Upload" }
            p { +"Upload your data" }
            input(InputType.file) {
                id = "import-input"
                onChangeFunction = {
                    val element = document.getElementById(id) as HTMLInputElement
                    if (element.files != undefined) {
                        val file = element.files!![0]!!
                        val reader = FileReader()
                        reader.onload = {
                            loadFromJson(reader.result as String)
                            modView()
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
