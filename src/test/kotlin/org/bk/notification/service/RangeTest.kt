package org.bk.notification.service

import com.google.cloud.bigtable.data.v2.models.Range
import org.junit.jupiter.api.Test

class RangeTest {
    @Test
    fun testRange() {
        val range = Range.ByteStringRange.prefix("abc")
        println(range.start)
        println(range.startBound)
        println(range.end)
        println(range.endBound)
    }
}