package org.manapart

import kotlinx.browser.window

fun main() {
    println("Starting!")
    window.onload = {
        println("on Load triggered")
    }
}
