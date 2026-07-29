package org.svetikov.chart2026.models

import kotlinx.serialization.Serializable

@Serializable
data class CarSend (
    val id: String = "",
    val name: String = "",
    val posX: String ="",
    val posZ: String ="",
    val length: String = "",
    val width: String = "",
    val thickness: String = "",
    val asStack: String = "",
    val coverBoardLength: String = "",
    val coverBoardWidth: String = "",
    val coverBoardThickness: String = "",
    val coverBoardStack: String = "",
    val date: String = "",
    val idModel: String = "",
)