package pl.lemanski.tokir.generator

import org.kobjects.ktxml.api.EventType
import org.kobjects.ktxml.api.XmlPullParser
import org.kobjects.ktxml.api.XmlPullParserException
import org.kobjects.ktxml.mini.MiniXmlPullParser
import pl.lemanski.tokir.generator.vector.FillType
import pl.lemanski.tokir.generator.vector.PathParser
import pl.lemanski.tokir.generator.vector.Vector
import pl.lemanski.tokir.generator.vector.VectorNode

/**
 * Parser that converts [icon]s into [Vector]s
 */
class IconParser(private val icon: Icon) {
    /**
     * @return a [Vector] representing the provided [icon].
     */
    fun parse(): Vector {
        val parser = MiniXmlPullParser(icon.fileContent.iterator()).apply {
            seekToStartTag()
        }

        check(parser.name == VECTOR) { "The start tag must be <vector>!" }

        val nodes = mutableListOf<VectorNode>()
        var autoMirrored = false

        var currentGroup: VectorNode.Group? = null

        while (!parser.isAtEnd()) {
            when (parser.eventType) {
                EventType.START_TAG -> {
                    when (parser.name) {
                        VECTOR    -> {
                            autoMirrored = parser.getValueAsBoolean(AUTO_MIRRORED)
                        }

                        PATH      -> {
                            val pathData = parser.getAttributeValue(
                                "",
                                PATH_DATA
                            )
                            val fillAlpha = parser.getValueAsFloat(FILL_ALPHA)
                            val strokeAlpha = parser.getValueAsFloat(STROKE_ALPHA)
                            val fillType = when (parser.getAttributeValue("", FILL_TYPE)) {
                                // evenOdd and nonZero are the only supported values here, where
                                // nonZero is the default if no values are defined.
                                EVEN_ODD -> FillType.EvenOdd
                                else     -> FillType.NonZero
                            }
                            val path = VectorNode.Path(
                                strokeAlpha = strokeAlpha ?: 1f,
                                fillAlpha = fillAlpha ?: 1f,
                                fillType = fillType,
                                nodes = PathParser.parsePathString(pathData!!)
                            )
                            if (currentGroup != null) {
                                currentGroup.paths.add(path)
                            } else {
                                nodes.add(path)
                            }
                        }
                        // Material icons are simple and don't have nested groups, so this can be simple
                        GROUP     -> {
                            val group = VectorNode.Group()
                            currentGroup = group
                            nodes.add(group)
                        }

                        CLIP_PATH -> { /* TODO: b/147418351 - parse clipping paths */
                        }
                    }
                }

                else                -> {}
            }
            parser.next()
        }

        return Vector(autoMirrored, nodes)
    }
}

/**
 * @return the float value for the attribute [name], or null if it couldn't be found
 */
private fun MiniXmlPullParser.getValueAsFloat(name: String) =
    getAttributeValue("", name)?.toFloatOrNull()

/**
 * @return the boolean value for the attribute [name], or 'false' if it couldn't be found
 */
private fun XmlPullParser.getValueAsBoolean(name: String) =
    getAttributeValue("", name).toBoolean()

private fun XmlPullParser.seekToStartTag(): XmlPullParser {
    var type = next()
    while (type != EventType.START_TAG && type != EventType.END_DOCUMENT) {
        // Empty loop
        type = next()
    }
    if (type != EventType.START_TAG) {
        throw XmlPullParserException("No start tag found", "")
    }
    return this
}

private fun XmlPullParser.isAtEnd() =
    eventType == EventType.END_DOCUMENT || (depth < 1 && eventType == EventType.END_TAG)

// XML tag names
private const val VECTOR = "vector"
private const val CLIP_PATH = "clip-path"
private const val GROUP = "group"
private const val PATH = "path"

// XML attribute names
private const val AUTO_MIRRORED = "android:autoMirrored"
private const val PATH_DATA = "android:pathData"
private const val FILL_ALPHA = "android:fillAlpha"
private const val STROKE_ALPHA = "android:strokeAlpha"
private const val FILL_TYPE = "android:fillType"

// XML attribute values
private const val EVEN_ODD = "evenOdd"
