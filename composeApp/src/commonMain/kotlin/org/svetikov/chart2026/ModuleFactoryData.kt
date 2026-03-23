package org.svetikov.chart2026

import kotlinx.serialization.Serializable

@Serializable
data class ModuleFactoryData(
    val id: String = "",
    val titleEquipment: String = "",
    val valueProcess: Float = 0f,
    val minProcess: Float = 0f,
    val maxProcess: Float = 100f,
)
