package org.svetikov.chart2026

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.repeat

@Composable
fun ShowAllCharts(colorTheme: Boolean, viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    var chartsCount by remember { mutableStateOf(1) }

    LazyColumn(
    ) {
        items(chartsCount) { index ->
            val chartIndex = IndexChart(index)
            viewModel.initChart(chartIndex)
            Text("Графік №${index + 1}", modifier = Modifier.padding(8.dp))
            RealTimeChartScreen(colorTheme = colorTheme, indexChart = IndexChart(index), viewModel = viewModel)
            // Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
        item {
            AddNewChart(
                enabled = chartsCount < 5,
                countChartOnClick = { if (chartsCount < 5) chartsCount++ }
            )
        }

    }


}

@Composable
fun AddNewChart(enabled: Boolean, countChartOnClick: () -> Unit) {
    IconButton(
        enabled = enabled,
        onClick = { countChartOnClick() },
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add New Chart",
            tint = if (enabled) Color.Blue else Color.Gray
        )
    }
}