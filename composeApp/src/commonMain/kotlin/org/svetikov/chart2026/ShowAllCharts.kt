package org.svetikov.chart2026

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun ShowAllCharts() {
    var countAddChart by remember { mutableStateOf(0) }
    LazyColumn(


    ) {
        item { RealTimeChartScreen() }
        if (countAddChart > 0)
            item { RealTimeChartScreen() }
        if (countAddChart > 1)
            item { RealTimeChartScreen() }
        if (countAddChart > 2)
            item { RealTimeChartScreen() }
        if (countAddChart > 3)
            item { RealTimeChartScreen() }
        if (countAddChart > 4)
            countAddChart = 4

        item { AddNewChart({ countAddChart++ }) }
    }


}

@Composable
fun AddNewChart(countChart: () -> Unit) {
    IconButton(onClick = { countChart() }) {
        Icon(Icons.Default.Add, contentDescription = "Add New Chart")
    }
}