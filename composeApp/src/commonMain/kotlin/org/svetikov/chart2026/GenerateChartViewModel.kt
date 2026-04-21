package org.svetikov.chart2026

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class GenerateChartViewModel(val serviceProcess: ServiceProcess = ServiceProcess()) : ViewModel() {
    private val _sizeListTake = MutableStateFlow("50")
    val sizeListTake = _sizeListTake.asStateFlow()
    private val _some = MutableStateFlow(0f)
    val some = _some.asStateFlow()

    private val _chartData = MutableStateFlow(
        listOf(
            LineData(
                name = "",
                values = listOf<ChartPoint>(ChartPoint(value = 0f, time = Clock.System.now())),
                color = Color.Blue
            )
        )
    )
    val chartData: StateFlow<List<LineData>> = _chartData.asStateFlow()

    private val _run = MutableStateFlow(true)
    val run: StateFlow<Boolean> = _run.asStateFlow()

    private val _getData = MutableStateFlow<List<ModelProcess>>(emptyList())
    val getData: StateFlow<List<ModelProcess>> = _getData.asStateFlow()
    private val _onlyOneData = MutableStateFlow(0f)
    val onlyOneData: StateFlow<Float> = _onlyOneData.asStateFlow()
    private val _processIndex = MutableStateFlow(0)

    private val _chartsData5 = MutableStateFlow<Map<IndexChart, List<LineData>>>(
        /*  mapOf(
              IndexChart(0) to listOf(
                  LineData(
                      name = "",
                      values = listOf<ChartPoint>(ChartPoint(value = 0f, time = Clock.System.now())),
                      color = Color.Blue
                  )
              )
          )*/
        emptyMap()
    )
    val chartData5 = _chartsData5.asStateFlow()

    init {

    }

    fun sizeListTakeInit(size: String) {
        _sizeListTake.value = size
    }

    fun realTimeRun(status: Boolean) {
        _run.value = status
    }

    private fun generate() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                _some.value = _onlyOneData.value
            }
        }

    }


    fun getData() {
        viewModelScope.launch {
            try {
                val data = serviceProcess.getFactoryData()
                println(data)
                _getData.value = data
                if (_getData.value.isNotEmpty())
                    _getData.value.first()?.let { println(it) } ?: println("no data 1")
                else println("no data 2")
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }

        }
    }

    private val _dataTable = MutableStateFlow<List<ModelProcess>>(emptyList())
    val dataTable = _dataTable.asStateFlow()

    private val _dataGroupTable = MutableStateFlow<Map<String, List<ModelProcess>>>(emptyMap())
    val dataGroupTable = _dataGroupTable.asStateFlow()
    fun getDataForTable() {
        viewModelScope.launch {
            while (true) {
                val data = serviceProcess.getFactoryData()
                val dataGroups = data.groupBy { it.processNamePLC }
                _dataGroupTable.value = dataGroups
                _dataTable.value = data
                delay(1000)
            }

        }
    }

    private fun initProcess() {
        val index = _processIndex.value
        viewModelScope.launch {
            while (true) {
                delay(500)
                _onlyOneData.value = serviceProcess.getFactoryData()[index]
                    .processValue.toFloat()
                if (_run.value) {
                    val newValue = _onlyOneData.value//(-100..100).random().toFloat()
                    addNewValue(newValue)
                }
            }
        }
        generate()
    }

    private fun addNewValue(value: Float) {
        val newPoint = ChartPoint(
            value = value,
            time = Clock.System.now()
        )
        _chartData.update { lines ->
            lines.map { line ->
                val currentValues = if (line.values.size == 1 && line.values.first().value == 0f)
                    listOf(newPoint)
                else
                    line.values + newPoint
                val newValues = currentValues
                    .takeLast(_sizeListTake.value.toInt())
                line.copy(values = newValues)
            }
        }
    }

    fun takeId(processId: String) {
        _processIndex.value = _getData.value.indexOfFirst { it.processId == processId }
        initProcess()
    }


    fun initChart(index: IndexChart) {
        if (_chartsData5.value.containsKey(index)) return
        _chartsData5.update {
            it + (index to listOf(
                LineData(
                    name = "",
                    values = listOf(
                        ChartPoint(value = 0f, time = Clock.System.now())
                    ),
                    color = Color.Blue
                )
            ))
        }
    }

    private fun addNewValueToChart5(index: IndexChart, value: Float) {
        val newPoint = ChartPoint(
            value = value,
            time = Clock.System.now()
        )
        _chartsData5.update { map ->
            val lines = map[index] ?: return@update map

            val updatedLines = lines.map { line ->
                val currentValues =
                    if (line.values.size == 1 && line.values.first().value == 0f)
                        listOf(newPoint)
                    else
                        line.values + newPoint
                val newValues = currentValues
                    .takeLast(_sizeListTake.value.toInt())
                line.copy(values = newValues)
            }
            map + (index to updatedLines)
        }
    }

    private val jobs = mutableMapOf<IndexChart, Job>()
    fun startChart5(index: IndexChart, processId: String) {
        // println("processId $processId index $index")
        jobs[index]?.cancel()
        val model = _getData.value.firstOrNull { it.processId == processId }
        val indexModel = _getData.value.indexOf(model)

        println("model $model indexModel $indexModel ")
        if (model == null) {
            println("❌ model not found in _getData")
            return
        }
        jobs[index] = viewModelScope.launch {
            while (true) {
                //  println("while $index processId $processId")
                delay(500)
                val value = serviceProcess.getFactoryData()[indexModel]
                    /*  .find {*//* println( it.processId == model.processId )*//*
                        it.processId == model.processId
                    }*/
                    .processValue.toFloat()
                // println("value $value")
                if (_run.value) {
                    addNewValueToChart5(index, value)
                }
            }
        }
    }
}

