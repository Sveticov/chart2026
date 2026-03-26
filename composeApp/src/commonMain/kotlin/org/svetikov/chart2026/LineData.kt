package org.svetikov.chart2026

import androidx.compose.ui.graphics.Color
import kotlin.time.Instant

data class LineData(
    val name: String,
    val values:List<ChartPoint>,
    val color: Color
)

data class ChartPoint(
    val value: Float,
    val time: Instant
)
