package org.alexcawl.client.ui

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal fun String.encodeNavArg(): String = URLEncoder.encode(this, StandardCharsets.UTF_8)

internal fun String.decodeNavArg(): String = URLDecoder.decode(this, StandardCharsets.UTF_8)
