package org.svetikov.chart2026

import kotlinx.serialization.Serializable

@Serializable
data class MessageStatus(
    val id:Int=-1,
    val message: String="",
    val status: Boolean=false,
    val date: String=""

)