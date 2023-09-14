package pl.lemanski.tokir.generator

import pl.lemanski.tokir.generator.vector.Vector
import pl.lemanski.tokir.generator.vector.VectorNode

/**
 * Generator for creating a Kotlin source file with an ImageVector property for the given [vector],
 * with name [iconName] and theme [iconTheme].
 *
 * @param iconName the name for the generated property, which is also used for the generated file.
 * I.e if the name is `Menu`, the property will be `Menu` (inside a theme receiver object) and
 * the file will be `Menu.kt` (under the theme package name).
 * @param iconTheme the theme that this vector belongs to. Used to scope the property to the
 * correct receiver object, and also for the package name of the generated file.
 * @param vector the parsed vector to generate ImageVector.Builder commands for
 */
class ImageVectorGenerator(
    private val iconName: String,
    private val iconTheme: IconTheme,
    private val vector: Vector
) {
    /**
     * @return a [FileSpec] representing a Kotlin source file containing the property for this
     * programmatic [vector] representation.
     *
     * The package name and hence file location of the generated file is:
     * [PackageNames.MaterialIconsPackage] + [IconTheme.themePackageName].
     */
    fun createFileSpec(): String {
        val builder = StringBuilder()

        val packageName = iconTheme.themePackageName
        builder.append("package $packageName\n\n")

        val backingPropertyString = getBackingProperty()

        // The getter logic should return a formatted string
        val iconGetterString = iconGetter(
            backingPropertyName = backingPropertyString,
            iconName = iconName,
            iconTheme = iconTheme,
        )

        builder.append("val $iconName: ImageVector\n")
        builder.append("    get() {\n")
        builder.append(iconGetterString) // This should already have the appropriate indentation and formatting
        builder.append("    }\n")

        // Assuming the backing property can be added like this
        builder.append("\nprivate var $backingPropertyString: ImageVector? = null\n")

        return builder.toString()
    }

    private fun getBackingProperty(): String {
        // Use a unique property name for the private backing property

        return "_" + iconName.replaceFirstChar { it.lowercase() }
    }

    /**
     * @return the body of the getter for the icon property. This getter returns the backing
     * property if it is not null, otherwise creates the icon and 'caches' it in the backing
     * property, and then returns the backing property.
     */
    private fun iconGetter(
        backingPropertyName: String,
        iconName: String,
        iconTheme: IconTheme
    ): String {
        val builder = StringBuilder()

        // Check if the backing property is not null
        builder.append("if ($backingPropertyName != null) return $backingPropertyName!!\n")

        // Create the icon and 'cache' it in the backing property
        builder.append("$backingPropertyName = MaterialIcon(name = \"${iconTheme.name}.$iconName\") {\n")

        // Assuming vector.nodes can be represented as a list of strings, or another appropriate method exists
        vector.nodes.forEach { node ->
            builder.append(addRecursively(node)) // This should add the logic for each node
        }

        builder.append("}\n")

        // Return the backing property
        builder.append("return $backingPropertyName!!\n")

        return builder.toString()
    }
}

/**
 * Recursively adds function calls to construct the given [vectorNode] and its children.
 */
private fun addRecursively(vectorNode: VectorNode): String {
    val builder = StringBuilder()

    when (vectorNode) {
        is VectorNode.Group -> {
            builder.append("group (\n")
            vectorNode.paths.forEach { path ->
                builder.append(addRecursively(path))
            }
            builder.append(")\n")
        }

        is VectorNode.Path -> {
            builder.append(addPath(vectorNode) {
                vectorNode.nodes.joinToString("\n") { it.asFunctionCall() }
            })
        }
    }

    return builder.toString()
}

/**
 * Adds a function call to create the given [path], with [pathBody] containing the commands for
 * the path.
 */
private fun addPath(path: VectorNode.Path, pathBody: () -> String): String {
    val builder = StringBuilder()

    val parameterList = with(path) {
        listOfNotNull(
            "fillAlpha = ${fillAlpha}f".takeIf { fillAlpha != 1f },
            "strokeAlpha = ${strokeAlpha}f".takeIf { strokeAlpha != 1f }
        )
    }

    val parameters = if (parameterList.isNotEmpty()) {
        parameterList.joinToString(prefix = "(", postfix = ")")
    } else {
        ""
    }

    builder.append("MaterialPath$parameters {\n")
    builder.append(pathBody())  // Use the provided pathBody function to inject path-specific code
    builder.append("}\n")

    return builder.toString()
}