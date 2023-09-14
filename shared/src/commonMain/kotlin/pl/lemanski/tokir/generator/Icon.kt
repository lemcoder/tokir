package pl.lemanski.tokir.generator

/**
 * Represents a icon's Kotlin name, processed XML file name, theme, and XML file content.
 *
 * The [kotlinName] is typically the PascalCase equivalent of the original icon name, with the
 * caveat that icons starting with a number are prefixed with an underscore.
 *
 * @property kotlinName the name of the generated Kotlin property, for example `ZoomOutMap`.
 * @property xmlFileName the name of the processed XML file
 * @property theme the theme of this icon
 * @property fileContent the content of the source XML file that will be parsed.
 * @property autoMirrored indicates that this Icon can be auto-mirrored on Right to Left layouts.
 */
data class Icon(
    val kotlinName: String,
    val xmlFileName: String,
    val theme: IconTheme,
    val fileContent: String,
    val autoMirrored: Boolean
)
