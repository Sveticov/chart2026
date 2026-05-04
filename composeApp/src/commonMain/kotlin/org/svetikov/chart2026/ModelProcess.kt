package org.svetikov.chart2026

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class ModelProcess(
    val processId:String="",
    val processNamePLC:String="",
    val processValue:String="",
    val processName:String="",
    val processMin:String="",
    val processMax:String="",
    val processStatus:String="",
    val trend:Int=0,
    val color: Boolean=false,
    val colorMin: Boolean=false,
    val ignoreColorMin:Boolean=false,
)