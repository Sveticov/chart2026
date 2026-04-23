package org.svetikov.chart2026

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ForkLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppTableData(viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    LaunchedEffect(Unit) {
        viewModel.getDataForTable()
        viewModel.dateShow()
    }

    val dataTableGroup: Map<String, List<ModelProcess>> = viewModel.dataGroupTable.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState()
    val dateTimeCountShow = viewModel.dateTimeSecondShow.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            if (isRefreshing){
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(2.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalItemSpacing = 12.dp,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // item {  Text(dateTimeCountShow.value.toString(), modifier = Modifier.align(Alignment.TopCenter)) }
                items(dataTableGroup.toList()) { (plcName, plcGroup) ->
                    Box(
                        Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                plcName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            plcGroup.forEach { p ->
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(p.processName, modifier = Modifier.weight(1f))
                                    Text(
                                        p.processValue,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End
                                    )
                                    if (p.trend != 0) {
                                        Icon(
                                            imageVector = if (p.trend > 0) Icons.Default.ArrowDropUp
                                            else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = if (p.trend > 0) Color.Green else Color.Red
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Coffee,
                                            contentDescription = null
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }

                }
            }
        }

        if (isLoading.value && dataTableGroup.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color(0xFFD9D5D5),
                strokeWidth = 4.dp

            )
        } else if (isLoading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

