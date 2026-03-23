package org.svetikov.chart2026

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.repeat

class GenerateChartViewModel(val serviceProcess: ServiceProcess = ServiceProcess()) : ViewModel() {
    private val _sizeListTake= MutableStateFlow("50")
    val sizeListTake = _sizeListTake.asStateFlow()
    private val _some = MutableStateFlow(0f)
    val some = _some.asStateFlow()

    private val _chartData = MutableStateFlow(listOf(LineData(name = "", values = listOf(0f), color = Color.Blue)))
    val chartData: StateFlow<List<LineData>> = _chartData.asStateFlow()

    private val _run = MutableStateFlow(true)
    val run: StateFlow<Boolean> = _run.asStateFlow()

    private val _getData = MutableStateFlow<List<ModelProcess>>(emptyList())
    val getData: StateFlow<List<ModelProcess>> = _getData.asStateFlow()
    private val _onlyOneData = MutableStateFlow(0f)
    val onlyOneData: StateFlow<Float> = _onlyOneData.asStateFlow()
    private val _processIndex = MutableStateFlow(0)

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

    private fun addNewValue(value: Float) {
        if (_chartData.value.size==0){
            repeat(50){
                _chartData.update { lines ->
                    lines.map { line ->
                        val newValues = (line.values + 0.0f)
                        line.copy(values = newValues)
                    }
                }
            }
        }
        _chartData.update { lines ->
            lines.map { line ->
                val newValues = (line.values + value).takeLast(_sizeListTake.value.toInt())
                line.copy(values = newValues)
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

    fun takeId(processId: String) {
        _processIndex.value = _getData.value.indexOfFirst { it.processId == processId }
        initProcess()
    }
}