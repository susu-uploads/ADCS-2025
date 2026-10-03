package org.alexcawl.reverse

internal class ReverseTextService {

    fun reverse(text: String): String {
        val codePoints: IntArray = text.codePoints().toArray()
        val builder = StringBuilder(text.length)
        for (index in codePoints.indices.reversed()) {
            builder.appendCodePoint(codePoints[index])
        }
        return builder.toString()
    }
}
