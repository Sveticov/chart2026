package org.svetikov.chart2026

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RealTimeChartScreen(viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    val lines by viewModel.chartData.collectAsStateWithLifecycle()
    var hideListData by remember { mutableStateOf(true) }

    val data by viewModel.getData.collectAsState(emptyList<ModelProcess>())
    var onlyOneData by remember { mutableStateOf(ModelProcess()) }
    Column {
        Text(
            "RealTimeChartScreen  ${onlyOneData.processNamePLC} ${onlyOneData.processName}  ",
            modifier = Modifier.padding(start = 100.dp)
        )
        AdvancedChart(lines = lines)
        if (hideListData) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ) {
                itemsIndexed(data) { index, it ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .background(if (index % 2 == 0) Color.Gray else Color.DarkGray)
                            .onClick {
                                onlyOneData = it
                                viewModel.takeId(it.processId)
                                hideListData = false
                            }

                    ) {
                        Text(
                            "plc: ${it.processNamePLC} name: ${it.processName} value: ${it.processValue}",
                            color = Color.White
                        )
                    }

                }

            }
        }
    }
}

@Composable
fun ControlButtons(viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    val run by viewModel.run.collectAsState(true)
    Column(
        modifier = Modifier.padding(start = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { viewModel.realTimeRun(!run) }) {
            Text(if (run) "Run" else "Stop")
        }
        Spacer(modifier = Modifier.padding(6.dp))
        Button(onClick = { viewModel.getData() }) { Text("Server") }
    }
}