package pl.lemanski.tokir.generator

/**
 * Enum representing the different themes for Material icons.
 *
 * @property themePackageName the lower case name used for package names and in xml files
 * @property themeClassName the CameCase name used for the theme objects
 */
enum class IconTheme(val themePackageName: String, val themeClassName: String) {
    Filled("filled", "Filled"),
    Outlined("outlined", "Outlined"),
    Rounded("rounded", "Rounded"),
    TwoTone("twotone", "TwoTone"),
    Sharp("sharp", "Sharp")
}

/**
 * Returns the matching [IconTheme] from [this] [IconTheme.themePackageName].
 */
fun String.toIconTheme() = requireNotNull(
    IconTheme.values().find {
        it.themePackageName == this
    }
) { "No matching theme found" }

/**
 * The ClassName representing this [IconTheme] object, so we can generate extension properties on
 * the object.
 *
 * @see [autoMirroredClassName]
 */
// FIXME
val IconTheme.className
    get() = "class_name"

/**
 * The ClassName representing this [IconTheme] object so we can generate extension properties on the
 * object when used for auto-mirrored icons.
 *
 * @see [className]
 */
// FIXME
val IconTheme.autoMirroredClassName
    get() = "auto_mirrored_class_name"

internal const val AutoMirroredName = "AutoMirrored"
internal val AutoMirroredPackageName = AutoMirroredName.lowercase()
