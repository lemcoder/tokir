package pl.lemanski.tokir.generator

import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FilePropertyBag

/**
 * Generates programmatic representation of [icon] using [ImageVectorGenerator].
 *
 * @property icon the [Icon] to generate Kotlin files for
 */
class IconWriter(private val icon: Icon) {
    /**
     * Generates icon and writes to [File],
     */
    fun generate(): File {
        val vector = IconParser(icon).parse()

        val fileSpec = ImageVectorGenerator(
            icon.kotlinName,
            icon.theme,
            vector
        ).createFileSpec()

        val blob = Blob(arrayOf(fileSpec), BlobPropertyBag("text/plain"))
        return File(arrayOf(blob.asDynamic()), "icon_generated", FilePropertyBag(type = "kt"))
    }
}
