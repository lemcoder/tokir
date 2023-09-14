package pl.lemanski.tokir

import org.w3c.files.File
import pl.lemanski.tokir.generator.IconProcessor
import pl.lemanski.tokir.generator.IconWriter

@JsName("generateComposeIcon")
suspend fun generateComposeIcon(file: File): File {
    val processor = IconProcessor()
    val icon = processor.processIcon(file)
    println(icon.toString())
    val writer = IconWriter(icon)
    return writer.generate()
}