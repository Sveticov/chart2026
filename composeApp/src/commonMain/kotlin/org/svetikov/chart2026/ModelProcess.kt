package org.svetikov.chart2026

import kotlinx.serialization.Serializable

@Serializable
data class ModelProcess(
    val processId:String="",
    val processNamePLC:String="",
    val processValue:String="",
    val processName:String="",
    val processMin:String="",
    val processMax:String="",
    val processStatus:String=""
)