package pl.lemanski.tokir

import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.MouseEvent

fun main() {
    println("Hello world")
//    val button = document.getElementById("button") as HTMLButtonElement
//    val input = document.getElementById("file_input") as HTMLInputElement
//    button.onclick = ::onClick
}

private fun onClick(event: MouseEvent) {
    println("button clicked")
}