package org.svetikov.chart2026

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var colorTheme by remember { mutableStateOf(true) }
        /*   var showContent by remember { mutableStateOf(false) }
           Column(
               modifier = Modifier
                   .background(MaterialTheme.colorScheme.primaryContainer)
                   .safeContentPadding()
                   .fillMaxSize(),
               horizontalAlignment = Alignment.CenterHorizontally,
           ) {*/
        //  AdvancedChart(chartData)
        Column(
            modifier = Modifier
                .background(
                    if (colorTheme) MaterialTheme.colorScheme.primaryContainer
                    else
                        Color(0xFF6F7272)
                )
        ) {
            IconButton(onClick = { colorTheme = !colorTheme }) {
                if (colorTheme)
                    Icon(Icons.Default.Brightness1, contentDescription = "")
                else
                    Icon(Icons.Default.Brightness7, contentDescription = "")
            }
           // RealTimeChartScreen(colorTheme=colorTheme)
           // ShowAllCharts(colorTheme=colorTheme)
            AppTableData()
        }


        // }
    }
}