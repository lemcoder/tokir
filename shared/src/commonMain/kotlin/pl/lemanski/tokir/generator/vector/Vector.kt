package pl.lemanski.tokir.generator.vector

/**
 * Simplified representation of a vector, with root [nodes].
 *
 * @param autoMirrored a boolean that indicates if this Vector can be auto-mirrored on left to right
 * locales
 * @param nodes may either be a singleton list of the root group, or a list of root paths / groups
 * if there are multiple top level declaration
 */
class Vector(val autoMirrored: Boolean, val nodes: List<VectorNode>)

/**
 * Simplified vector node representation, as the total set of properties we need to care about
 * for Material icons is very limited.
 */
sealed class VectorNode {
    class Group(val paths: MutableList<Path> = mutableListOf()) : VectorNode()
    class Path(
        val strokeAlpha: Float,
        val fillAlpha: Float,
        val fillType: FillType,
        val nodes: List<PathNode>
    ) : VectorNode()
}
