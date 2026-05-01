@file:Suppress("EQUALITY_NOT_APPLICABLE_WARNING")

package org.svetikov.chart2026

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BlurOff
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Month

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppTableData(rowCount: Int = 3, viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    LaunchedEffect(Unit) {
        viewModel.getDataForTable()
        // viewModel.dateShow()
        viewModel.getMessagesFromBot()
    }

    val dataTableGroup: Map<String, List<ModelProcess>> = viewModel.dataGroupTable.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState()

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val lastUpdate by viewModel.lastUpdate.collectAsState()
    val lastUpdateStatus by viewModel.lastUpdateStatus.collectAsState()

    val messageStatus by viewModel.messageStatus.collectAsState()



    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth().padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (lastUpdateStatus.isNotEmpty()) "Last update: $lastUpdateStatus" else "Waite data...",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Blue,
                        modifier = Modifier.fillMaxWidth(0.25f).clickable(onClick = { viewModel.sound() })
                    )

                    Row(modifier = Modifier.fillMaxWidth(0.5f)) {
                        messageStatus.forEach { it ->
                            var isHovered by remember { mutableStateOf(false) }
                            var isStatus by remember { mutableStateOf(false) }
                            val st = if (it.id == 0) !it.status else it.status
                            if (st != isStatus && !isLoading.value) {
                                scope.launch {
                                    isHovered = true

                                    delay(10000)
                                    isStatus = st
                                    isHovered = false
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                                    .onPointerEvent(PointerEventType.Exit) { isHovered = false }
                                    /*.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))*/,
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (st) Icons.Default.Done else Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = if (st) Color.Green else Color.Red,
                                    modifier = Modifier.size(22.dp).padding(4.dp)
                                )
                                if (isHovered) {
                                    Box(
                                        modifier = Modifier
                                            .offset(y = (-20).dp)
                                            .background(Color(0xFF333333), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = it.message,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Text(
                        text = if (lastUpdate.isNotEmpty()) "Last update: $lastUpdate" else "Waite data...",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isRefreshing) Color.Blue else Color.Gray,
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )
                }
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(rowCount),
                modifier = Modifier.fillMaxSize().padding(8.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // item {  Text(dateTimeCountShow.value.toString(), modifier = Modifier.align(Alignment.TopCenter)) }
                items(dataTableGroup.toList()) { (plcName, plcGroup) ->
                   // var _isWarning by remember { mutableStateOf(false) }
                    var _isHideGroups by remember { mutableStateOf(true) }
                    Box(
                        Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                plcName,
                                style = if (rowCount >= 3) MaterialTheme.typography.bodySmall
                                else MaterialTheme.typography.titleMedium,
                                color = if (plcGroup.any { it.color }) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp).clickable(onClick = {_isHideGroups = !_isHideGroups})
                            )
                            if (_isHideGroups)
                            plcGroup.forEach { p ->
                              /*  if (p.processValue.toFloat() > p.processMax.toFloat()) {
                                    _isWarning = true
                                } else _isWarning = false*/
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        p.processName,
                                        style = if (rowCount >= 3) MaterialTheme.typography.bodySmall
                                        else MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        p.processValue,
                                        style = if (rowCount >= 3) MaterialTheme.typography.bodySmall
                                        else MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.End,
                                        color = if (p.color) Color.Red else Color.Black,
                                    )
                                    if (p.trend != 0) {
                                        Icon(
                                            imageVector = if (p.trend > 0) Icons.Default.ArrowDropUp
                                            else Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = if (p.trend > 0) Color.Green else Color.Red,
                                            modifier = Modifier.size(if (rowCount <= 3) 22.dp else 15.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Coffee,
                                            contentDescription = null,
                                            tint = Color(0xFFC87FF3),
                                            modifier = Modifier.size(if (rowCount <= 3) 22.dp else 15.dp)
                                        )
                                    }
                                }
                                HorizontalDivider(
                                    thickness = if (p.color) 0.7.dp else 0.5.dp,
                                    color = if (p.color) Color.Red.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.outline.copy(
                                        alpha = 0.3f
                                    )
                                )
                            }
                        }
                    }

                }

                item() {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            var _isHideGroups by remember { mutableStateOf(true) }
                            Text(
                                "Status Line",
                                style = if (rowCount >= 3) MaterialTheme.typography.bodySmall
                                else MaterialTheme.typography.titleMedium,
                                color =  MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 8.dp).clickable(onClick = {_isHideGroups = !_isHideGroups})
                            )
                            if (_isHideGroups)
                            messageStatus.forEach { it ->

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    var text = it.message + " \n" + it.date
                                    val st = if (it.id == 0) !it.status else it.status

                                    if (text.length > 45)
                                        text = text.chunked(35).joinToString("\n")

                                    Text(
                                        text,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.fillMaxWidth(0.7f)
                                    )


                                    Icon(
                                        imageVector = if (st) Icons.Default.Done else Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = if (st) Color.Green else Color.Red,
                                        modifier = Modifier.fillMaxWidth(0.3f)
                                    )

                                }
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            }
                        }

                    }
                }
                item {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
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

