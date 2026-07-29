package org.svetikov.chart2026

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.svetikov.chart2026.models.CarSend
import org.svetikov.chart2026.view_model.StorageViewModel

@Composable
fun AppStorage(viewModel: StorageViewModel = viewModel { StorageViewModel() }) {
    val storage = viewModel.storage.collectAsState().value

    var idModelSelected by remember { mutableStateOf(-1L) }
    var switchNameOrDateHour by remember { mutableStateOf(true) }
    val isRefreshing = viewModel.isRefreshing.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.getAllBoards()
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Column {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp)
                ) {
                    Text("All Storage Boards size: ${storage.size} ID: $idModelSelected")
                    Box(
                        modifier = Modifier
                            .size(width = 120.dp, height = 30.dp)
                            .background(Color(0xFF0A5DE8))
                            .clickable(onClick = {
                                switchNameOrDateHour = !switchNameOrDateHour
                                println("hi")
                            }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "All Storage " + if (switchNameOrDateHour) "Name" else "Hours",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
                if (isRefreshing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        item {
            StorageMap(
                viewModel,
                storage,
                switchNameOrDateHour,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .background(Color(0xFF3F423F))
            )
            { selectId -> idModelSelected = selectId }
        }
        items(storage) { it ->
            with(it) {
                Text(
                    "$id , $name, $posX, $posZ, $date, $thickness " +
                            if (id.toLong() == idModelSelected) "  ←  " else "",
                    color = if (id.toLong() == idModelSelected) Color.Blue else Color.Black
                )
            }
        }
    }
}

@Composable
fun StorageMap(
    viewModel: StorageViewModel,
    storage: List<CarSend>,
    switchThicknessOrDateHour: Boolean = false,
    modifier: Modifier = Modifier,
    onBoardClick: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var countOffset by remember { mutableStateOf(0) }
    if (storage.isEmpty()) return

    // 1. Знаходимо межі системи координат (Min/Max)
    val minX = 101000//storage.minOf { it.posX.toInt() }
    val maxX = 142300//storage.maxOf { it.posX.toInt() }
    val minZ = 92800//storage.minOf { it.posZ.toInt() }
    val maxZ = 220000//storage.maxOf { it.posZ.toInt() }

    val realWidth = (maxX - minX).takeIf { it > 0 } ?: 1
    val realHeight = (maxZ - minZ).takeIf { it > 0 } ?: 1

    // BoxWithConstraints дозволяє дізнатися розміри доступного екрана в DP
    BoxWithConstraints(modifier = modifier.border(2.dp, color = Color.White)) {
        Box(modifier = Modifier.size(45.dp, 25.dp).offset(180.dp, 265.dp).background(Color(0xFF2F2E2E)))
        Box(modifier = Modifier.size(45.dp, 25.dp).offset(180.dp, 365.dp).background(Color(0xFF2F2E2E)))
        Box(modifier = Modifier.size(45.dp, 25.dp).offset(180.dp, 412.dp).background(Color(0xFF2F2E2E)))
        Box(modifier = Modifier.size(45.dp, 25.dp).offset(22.dp, 362.dp).background(Color(0xFF2F2E2E)))
        var start = 22
        repeat(6) {
            Box(modifier = Modifier.size(620.dp, 25.dp).offset(162.dp, start.dp).background(Color(0xFF2F2E2E)))
            start += 35
        }
        // Залишаємо невеликі відступи (padding) та враховуємо розмір самих блоків
        val padding = 25.dp//todo 20.dp
        val availableWidthDp = (maxWidth - padding * 2)
        val availableHeightDp = (maxHeight - padding * 2)

        // 2. Рахуємо коефіцієнти масштабування
        val scaleX = availableWidthDp / realHeight
        val scaleZ = availableHeightDp / realWidth

        // Використовуємо єдиний масштаб, щоб зберегти пропорції (пропорційне стиснення)
        val scale = minOf(scaleX, scaleZ)

        // 3. Малюємо плашки з розрахованими координатами
        Box(modifier = Modifier.size(maxWidth, maxHeight)) {
            var showInfoID by remember { mutableStateOf(-1L) }
            for (board in storage) {
                var showInfo by remember { mutableStateOf(false) }
                var screenYOffset by remember { mutableStateOf(0) }
                // Преобразуємо реальні координати в екранні DP:
                // (значення - min) * scale + padding
                val screenX = (((board.posZ.toInt() - minZ) * scale.value).dp + padding)
                val screenZ = (((maxX - board.posX.toInt()) * scale.value).dp + padding)

                Box(
                    modifier = Modifier
                        .offset(x = screenX, y = screenZ)
                        .size(40.dp, 20.dp)
                        .background(
                            if (board.name in listOf<String>(
                                    "car3",
                                    "car4",
                                    "3",
                                    "4"
                                )
                            ) Color(0xFFE04B8A) else Color(0xFF0555E3)
                        )
                        .clickable(onClick = {
                            showInfo = !showInfo
                            showInfoID = board.id.toLong()
                            screenYOffset = MyConstant.SIZE_TABLE_INFO * countOffset
                            if (showInfo) {
                                countOffset++
                            } else countOffset--
                            onBoardClick(board.id.toLong())
                        })
                        .border(1.dp, if (board.id.toLong() == showInfoID) Color(0xFFF60395) else Color(0xFF031BF6))
                ) {
                    Text(
                        text = if (switchThicknessOrDateHour) board.thickness else viewModel.getHoursAgo(board.date),
                        modifier = Modifier.align(Alignment.Center).padding(bottom = 4.dp),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
                if (showInfo)
                    Box(
                        modifier = Modifier.offset(920.dp, y = (screenYOffset + 10).dp)
                            .size(MyConstant.SIZE_TABLE_INFO.dp)
                            .border(2.dp, Color.Blue)
                            .background(Color.DarkGray)
                    ) {
                        var showMoreInfo by remember { mutableStateOf(false) }
                        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
                            Row(horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(text="${viewModel.boardName(board.name)}", fontSize = 10.sp, color = Color.White)
                                Spacer(modifier = Modifier.width(16.dp))
                                IconButton(onClick = {showMoreInfo = !showMoreInfo}) {
                                    Icon(imageVector = Icons.Default.Info, contentDescription = "",
                                        tint = Color.White,
                                        modifier = Modifier.size(25.dp).padding(bottom = 4.dp))
                                }
                            }
                            if(showMoreInfo) {
                                Text("$screenX ", fontSize = 10.sp, color = Color.White)
                                Text("$screenZ ", fontSize = 10.sp, color = Color.White)
                            }

                            Text("ID: ${board.id}", fontSize = 10.sp, color = Color.White)
                            if (showMoreInfo) {
                                Text("X   ${board.posX}", fontSize = 10.sp, color = Color.White)
                                Text("Z   ${board.posZ} ", fontSize = 10.sp, color = Color.White)
                            }

                            Text(
                                "Date   ${board.date} ",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(text = "board", fontSize = 10.sp, color = Color.White)
                                    Text(" wb: ${board.width}", fontSize = 10.sp, color = Color.White)
                                    Text(" lb: ${board.length}", fontSize = 10.sp, color = Color.White)
                                    Text(" tb: ${board.thickness}", fontSize = 10.sp, color = Color.White)
                                    Text(" sb: ${board.asStack}", fontSize = 10.sp, color = Color.White)
                                }
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(text = "cover", fontSize = 10.sp, color = Color.White)
                                    Text(" wc: ${board.coverBoardWidth}", fontSize = 10.sp, color = Color.White)
                                    Text(" lc: ${board.coverBoardLength}", fontSize = 10.sp, color = Color.White)
                                    Text(" tc: ${board.coverBoardThickness}", fontSize = 10.sp, color = Color.White)
                                    Text(" sc: ${board.coverBoardStack}", fontSize = 10.sp, color = Color.White)
                                }
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 20.dp, bottom = 10.dp)
                                            .background(if(showMoreInfo)Color(0xFF3781F1) else Color.Gray)
                                            .size(width = 50.dp, height = 30.dp)
                                            .clickable(onClick = {
                                                if (showMoreInfo) {
                                                    showInfo = false
                                                    showInfoID = -1
                                                    countOffset--
                                                    viewModel.deleteBoardFromID(board.id)
                                                }
                                            }), contentAlignment = Alignment.Center
                                    ) { Text("Delete", fontSize = 10.sp, color = Color.White) }
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 20.dp, bottom = 10.dp)
                                            .background(Color(0xFF3781F1))
                                            .size(width = 50.dp, height = 30.dp)
                                            .clickable(onClick = {
                                                showInfo = false
                                                showInfoID = -1
                                                countOffset--
                                            }), contentAlignment = Alignment.Center
                                    ) { Text("Exit", fontSize = 10.sp, color = Color.White) }
                                }
                            }
                        }
                    }
            }
        }
    }
}