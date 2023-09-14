package pl.lemanski.tokir.generator

import kotlinx.coroutines.delay
import org.w3c.files.File
import org.w3c.files.FileReader

/**
 * Processes vector drawables in into icons, removing any unwanted
 * attributes (such as android: attributes that reference the theme) from the XML source.
 */
class IconProcessor {
    /**
     * @return a processed [Icon], from the provided [File].
     */
    suspend fun processIcon(file: File): Icon {
        val filename = file.name
        val kotlinName = filename.toKotlinPropertyName()

        val reader = FileReader()
        reader.readAsText(file)
        while (reader.result == null) {
            delay(200)
        }
        val fileContent = reader.result.toString()

        return Icon(
            kotlinName = kotlinName,
            xmlFileName = kotlinName, // TODO do I really need that?
            theme = IconTheme.Filled, // TODO handle themes (or not)
            fileContent = processXmlFile(fileContent),
            autoMirrored = isAutoMirrored(fileContent)
        )
    }
}

/**
 * Processes the given [fileContent] by removing android theme attributes and values.
 */
private fun processXmlFile(fileContent: String): String {
    // Remove any defined tint for paths that use theme attributes
    val tintAttribute = Regex.escape("""android:tint="?attr/colorControlNormal"""")
    val tintRegex = """\n.*?$tintAttribute""".toRegex(RegexOption.MULTILINE)

    return fileContent
        // TODO
        // .replace(tintRegex, "")
        // The imported icons have white as the default path color, so let's change it to be
        // black as is typical on Android.
        .replace("@android:color/white", "@android:color/black")
}

/**
 * Returns true if the given [fileContent] includes an `android:autoMirrored="true"` attribute.
 */
private fun isAutoMirrored(fileContent: String): Boolean =
    fileContent.contains(Regex.fromLiteral("""android:autoMirrored="true""""))

/**
 * Ensures that each icon in each theme is available in every other theme
 */
private fun ensureIconsExistInAllThemes(icons: List<Icon>) {
    val groupedIcons = icons.groupBy { it.theme }

    check(groupedIcons.keys.containsAll(IconTheme.values().toList())) {
        "Some themes were missing from the generated icons"
    }

    val expectedIconNames = groupedIcons.values.map { themeIcons ->
        themeIcons.map { icon -> icon.kotlinName }.sorted()
    }

    expectedIconNames.first().let { expected ->
        expectedIconNames.forEach { actual ->
            check(actual == expected) {
                "Not all icons were found in all themes $actual $expected"
            }
        }
    }
}

// TODO
/**
 * Converts a snake_case name to a KotlinProperty name.
 *
 * If the first character of [this] is a digit, the resulting name will be prefixed with an `_`
 */
private fun String.toKotlinPropertyName(): String = this.substringBefore(".")