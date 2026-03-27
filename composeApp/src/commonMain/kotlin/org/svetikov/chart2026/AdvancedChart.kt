package org.svetikov.chart2026


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdvancedChart(lines: List<LineData>, viewModel: GenerateChartViewModel) {
    var zoom by remember { mutableStateOf(1f) }
    val scrollState = rememberScrollState()
    var selectedPoint by remember { mutableStateOf<Pair<Int, ChartPoint>?>(null) }


    val max = lines.flatMap { it.values.map { v -> v.value } }.maxOrNull() ?: 0f
    val min = lines.flatMap { it.values.map { v -> v.value } }.minOrNull() ?: 0f
    val range = (max - min).takeIf { it != 0f } ?: 1f
    val textMeasurer = rememberTextMeasurer()

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000)
    )
    var density by remember { mutableStateOf(50f) }
    var sizeChart by remember { mutableStateOf(150) }
    val sizeListTake by viewModel.sizeListTake.collectAsState("150")
    var hideControlButton by remember { mutableStateOf(true) }

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
                        val normalized = if (range == 0f) 0.5f else (value.value - min) / range
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
                    val normalized = (value.value - min) / range
                    val y = size.height - normalized * size.height

                    drawCircle(
                        color = Color.Red,
                        radius = 2f,
                        center = Offset(x, y)
                    )
                    drawLine(
                        color = Color.Red,
                        strokeWidth = 2f,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                    )

                }
            }
            selectedPoint?.let { (index, point) ->
                val localTime = (point.time + 2.hours)
                    .toString()
                    .substringAfter("T")
                    .substringBefore(".")


                Text(
                    text = if (lines.isEmpty() || lines.first().values.isEmpty()) "No data"
                    else "index=$index  value=${point.value} time = ${localTime}",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(Color.Black)
                        .padding(6.dp)
                        .onClick(onClick = {hideControlButton=!hideControlButton} ),
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        if(hideControlButton)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,

        ) {
            ControlButtons(viewModel)
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
                            Row {
                                IconButton(onClick = { sizeChart += 5 }) {
                                    Icon(imageVector = Icons.Default.ArrowCircleUp, contentDescription = null)
                                }
                                IconButton(onClick = { sizeChart -= 5 }) {
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