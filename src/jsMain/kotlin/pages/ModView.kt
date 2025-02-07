package org.manapart.pages

import kotlinx.html.id
import kotlinx.html.js.*
import kotlinx.html.span
import org.manapart.getMods
import org.manapart.replaceElement
import org.manapart.updateUrl

fun modView() {
    updateUrl("mods")
    replaceElement {
        div {
            id = "mods"
            h1 { +"Mod Viewer" }
            p { +"Upload your data.json to view your mod list on the go!" }
            button {
                +"Upload"
                onClickFunction = { uploadView()}
            }
            div {
                id = "header"
                span("idStat") { +"Id" }
                span("stat versionStat") { +"Version" }
                span("stat loadStat") { +"Load" }
                span("stat enabledStat") { +"Enabled" }
                span("stat endorsedStat") { +"Endorsed" }
                span("stat categoryStat") { +"Category" }
            }
            getMods().forEach { mod ->
                div("modRow") {
                    div("nameRow") {
                        +mod.name
                    }
                    div("statsRow") {
                        span("idStat") { +(mod.id?.toString() ?: "?") }
                        //TODO - show update icon
                        span("stat versionStat") { +(mod.version ?: "?") }
                        span("stat loadStat") { +mod.loadOrder.toString() }
                        //Show icons
                        span("stat enabledStat") { +mod.enabled.toString() }
                        span("stat endorsedStat") { +(mod.endorsed?.toString() ?: "") }
                        span("stat categoryStat") { +(mod.categoryId?.toString() ?: "") }
                        val tagContent = if (mod.tags.isNotEmpty()) mod.tags.joinToString() else ""
                        span("stat tagsStat") { +tagContent }
                    }
                }
            }
        }
    }
}
