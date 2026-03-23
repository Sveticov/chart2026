package org.svetikov.chart2026


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.key.Key.Companion.R

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.painterResource

import kotlin.math.roundToInt

@Composable
fun AdvancedChart(lines: List<LineData>, viewModel: GenerateChartViewModel = viewModel { GenerateChartViewModel() }) {
    var zoom by remember { mutableStateOf(1f) }
    val scrollState = rememberScrollState()
    var selectedPoint by remember { mutableStateOf<Pair<Int, Float>?>(null) }

    val max = lines.flatMap { it.values }.maxOrNull() ?: 0f
    val min = lines.flatMap { it.values }.minOrNull() ?: 0f
    val range = (max - min).takeIf { it != 0f } ?: 1f
    val textMeasurer = rememberTextMeasurer()

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000)
    )
    var density by remember { mutableStateOf(50f) }
    var sizeChart by remember { mutableStateOf(150) }
    val sizeListTake by viewModel.sizeListTake.collectAsState("150")
    Column {
        Box {
            Canvas(
                modifier = Modifier.fillMaxWidth()
                    .height(sizeChart.dp)
                    .horizontalScroll(scrollState)
                    .width((lines.first().values.size * density * zoom).dp)
                    .pointerInput(lines) {
                        detectTapGestures { offset ->
                            val pointCount = lines.first().values.size
                            val stepX = size.width / (pointCount - 1)
                            val index = (offset.x / stepX).roundToInt().coerceIn(0, pointCount - 1)
                            lines.first().values.getOrNull(index)?.let {
                                selectedPoint = index to it
                            }
                        }
                    }
            ) {
                val stepX = size.width / (lines.first().values.size - 1)

                drawGrid(size, min, max, textMeasurer)

                lines.forEach { line ->
                    val points = line.values.mapIndexed { index, value ->
                        val x = index * stepX
                        val normalized = if (range == 0f) 0.5f else (value - min) / range
                        val y = size.height - normalized * size.height //* animationProgress
                        Offset(x, y)
                    }

                    val path = Path()

                    points.forEachIndexed { index, point ->

                        if (index == 0) path.moveTo(point.x, point.y)
                        else path.lineTo(point.x, point.y)
                    }

                    drawPath(
                        path = path,
                        color = line.color,
                        style = Stroke(width = 4f)
                    )
                }
                selectedPoint?.let { (index, value) ->
                    val x = index * stepX
                    val normalized = (value - min) / range
                    val y = size.height - normalized * size.height

                    drawCircle(
                        color = Color.Red,
                        radius = 10f,
                        center = Offset(x, y)
                    )
                }
            }
            selectedPoint?.let { (index, value) ->
                Text(
                    text = "index=$index  value=$value",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(Color.Black)
                        .padding(6.dp),
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ControlButtons()
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text("Zoom", modifier = Modifier.weight(0.2f).padding(6.dp))
                    Slider(
                        modifier = Modifier.weight(0.5f).padding(6.dp).scale(0.7f),
                        value = zoom,
                        onValueChange = { zoom = it },
                        valueRange = 1f..5f
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(0.3f),
                        value = sizeChart.toString(),
                        onValueChange = { sizeChart = it.toInt() },
                        label = { Text("size chart") },
                        trailingIcon = {
                            Row{
                                IconButton(onClick = { sizeChart+=5 }) {
                                    Icon(imageVector = Icons.Default.ArrowCircleUp, contentDescription = null)
                                }
                                IconButton(onClick = { sizeChart-=5 }) {
                                    Icon(imageVector = Icons.Default.ArrowCircleDown, contentDescription = null)
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text("Density $density", modifier = Modifier.weight(0.2f).padding(6.dp))
                    Slider(
                        modifier = Modifier.weight(0.5f).padding(6.dp).scale(0.7f),
                        value = density,
                        onValueChange = { density = it },
                        valueRange = 5f..150f
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(0.3f),
                        value = sizeListTake,
                        onValueChange = { viewModel.sizeListTakeInit(it) },
                        label = { Text("size list ") },
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { viewModel.sizeListTakeInit((sizeListTake.toInt() + 5).toString()) }) {
                                    Icon(imageVector = Icons.Default.ArrowCircleUp, contentDescription = null)
                                }
                                IconButton(onClick = { viewModel.sizeListTakeInit((sizeListTake.toInt() - 5).toString()) }) {
                                    Icon(imageVector = Icons.Default.ArrowCircleDown, contentDescription = null)
                                }
                            }

                        }
                    )
                    Spacer(modifier = Modifier.weight(0.1f))
                }
            }
        }


        //   GenerateChart()
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawGrid(
    size: Size,
    min: Float = 0f,
    max: Float = 0f,
    textMeasurer: TextMeasurer
) {
    val gridLines = 5
    val stepY = size.height / gridLines

    val textStyle = TextStyle(
        color = Color.Gray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    for (i in 0..gridLines) {
        val y = i * stepY

        drawLine(
            color = Color.LightGray,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )

        val lable = when (i) {
            0 -> "Max: ${max}"
            gridLines -> "Min: ${min}"
            else -> null
        }
        lable?.let { text ->
            val textLayoutResult = textMeasurer.measure(text, textStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(10f, y = y - textLayoutResult.size.height),
            )
        }
    }


}