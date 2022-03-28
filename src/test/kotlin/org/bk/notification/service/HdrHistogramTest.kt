package org.bk.notification.service

import org.HdrHistogram.AbstractHistogram
import org.HdrHistogram.Histogram
import org.junit.jupiter.api.Test

class HdrHistogramTest {

    @Test
    fun testHistograms() {
        val hdrHistogram: Histogram = Histogram(3)
        hdrHistogram.recordValue(100)
        hdrHistogram.recordValue(90)
        hdrHistogram.recordValue(80)
        hdrHistogram.recordValue(70)
        hdrHistogram.recordValue(60)
        hdrHistogram.recordValue(50)
        val percentiles: AbstractHistogram.Percentiles = hdrHistogram.percentiles(30)
        percentiles.forEach { value ->
            println(value)
        }

    }
}