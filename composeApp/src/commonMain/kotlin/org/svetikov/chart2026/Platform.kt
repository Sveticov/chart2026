package org.svetikov.chart2026

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform