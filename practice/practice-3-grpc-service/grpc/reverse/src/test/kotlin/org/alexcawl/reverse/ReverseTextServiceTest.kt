package org.alexcawl.reverse

import kotlin.test.Test
import kotlin.test.assertEquals

class ReverseTextServiceTest {

    private val reverseTextService = ReverseTextService()

    @Test
    fun `reverse should reverse plain text`() {
        assertEquals("tset a si siht", reverseTextService.reverse("this is a test"))
    }

    @Test
    fun `reverse should keep surrogate pairs intact`() {
        assertEquals("B😀A", reverseTextService.reverse("A😀B"))
    }
}
