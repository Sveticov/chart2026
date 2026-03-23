package org.svetikov.chart2026

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        /*   var showContent by remember { mutableStateOf(false) }
           Column(
               modifier = Modifier
                   .background(MaterialTheme.colorScheme.primaryContainer)
                   .safeContentPadding()
                   .fillMaxSize(),
               horizontalAlignment = Alignment.CenterHorizontally,
           ) {*/
        //  AdvancedChart(chartData)
       RealTimeChartScreen()
       //ShowAllCharts()
        // }
    }
}