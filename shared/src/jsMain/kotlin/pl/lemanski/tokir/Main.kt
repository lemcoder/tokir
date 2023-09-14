package pl.lemanski.tokir

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.asList
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.url.URL
import org.w3c.files.File

fun main() {
    val button = document.getElementById("button") as HTMLButtonElement
    button.onclick = ::onClick
}

private fun onClick(event: MouseEvent) {
    val input = document.getElementById("file_input") as HTMLInputElement
    checkNotNull(input.files)
    val file = input.files!!.asList().first()
    println(file.name)
    CoroutineScope(Job()).launch {
        try {
            val composableIcon = generateComposeIcon(file)
            println(composableIcon.name)
            downloadFile(composableIcon)
        } catch (e: Exception) {
            println(e.message)
            println(e.stackTraceToString())
        }
    }
}

private fun downloadFile(file: File) {
    println("download start")
    val url = URL.createObjectURL(file)
    val downloadLink = document.createElement("a") as HTMLAnchorElement
    downloadLink.href = url
    println("download mid")
    downloadLink.download = "${file.name}.kt"
    document.body?.appendChild(downloadLink)  // Temporarily add the link to the document

    downloadLink.click()  // Simulate a click to start the download

    println("download end")
    // Cleanup
    document.body?.removeChild(downloadLink)
    URL.revokeObjectURL(url)
}