package org.svetikov.chart2026

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Brightness1
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewDay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var colorTheme by remember { mutableStateOf(true) }
        var settingTheme by remember { mutableStateOf(false) }
        var rowCount by remember { mutableStateOf("3") }
        var _isHaide by remember { mutableStateOf(false) }
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
                /*.onPointerEvent(PointerEventType.Enter) { _isHaide = true }
                .onPointerEvent(PointerEventType.Exit) { _isHaide = false }*/
                .background(
                    if (colorTheme) Color(0xFFABAFAF)
                    else
                        Color(0xFF6F7272)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onPointerEvent(PointerEventType.Enter) { _isHaide = true }
                    .onPointerEvent(PointerEventType.Exit) { _isHaide = false }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    if (_isHaide) {
                        IconButton(onClick = { colorTheme = !colorTheme }, modifier = Modifier.fillMaxWidth(0.1f)) {
                            if (colorTheme)
                                Icon(Icons.Default.Brightness1, contentDescription = "")
                            else
                                Icon(Icons.Default.Brightness7, contentDescription = "")
                        }
                    }

                    if (_isHaide) {
                        IconButton(
                            onClick = { settingTheme = !settingTheme },
                            modifier = Modifier.fillMaxWidth(0.1f)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "")
                        }
                    }

                    if (_isHaide) {
                        if (settingTheme) {
                            OutlinedTextField(
                                value = rowCount,
                                onValueChange = { rowCount = it },
                                label = { Text("row count") },
                                minLines = 2,
                                modifier = Modifier.width(200.dp).height(52.dp).padding(start = 8.dp),
                                textStyle = TextStyle(fontSize = 12.sp),
                                trailingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        IconButton(onClick = {
                                            if (rowCount.toInt() < 10)
                                                rowCount = (rowCount.toInt() + 1).toString()

                                        }) {
                                            Icon(
                                                Icons.Default.ArrowDropUp,
                                                contentDescription = "row count up"
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(onClick = {
                                            if (rowCount.toInt() >= 3)
                                                rowCount = (rowCount.toInt() - 1).toString()

                                        }) {
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = "row count down"
                                            )
                                        }

                                    }
                                }
                            )
                        }
                    }
                    if (_isHaide)
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment =Alignment.CenterEnd) {
                        Text("Svetikov 2026", fontSize = 12.sp, color = Color(0xffffffff))
                    }

                }
            }


            // }

            // RealTimeChartScreen(colorTheme=colorTheme)
            // ShowAllCharts(colorTheme=colorTheme)
            AppTableData(rowCount = if (rowCount.isNotEmpty()) rowCount.toInt() else 3)
        }
    }

}

