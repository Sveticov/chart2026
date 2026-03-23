package org.svetikov.chart2026

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GenerateChart(viewModel: GenerateChartViewModel = viewModel{GenerateChartViewModel()}) {
    val generate = viewModel.some.collectAsState(12)

    Text(text = generate.value.toString().takeIf { it.isNotBlank() } ?: "None", modifier = Modifier.padding(16.dp))
}