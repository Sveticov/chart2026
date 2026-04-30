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
/*import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime*/
import kotlin.time.*
import kotlinx.datetime.*
import kotlinx.datetime.format.*
import kotlin.time.Duration.Companion.hours



/*import kotlin.time.Instant*/

class GenerateChartViewModel(val serviceProcess: ServiceProcess = ServiceProcess()) : ViewModel() {
    private val _sizeListTake = MutableStateFlow("50")
    val sizeListTake = _sizeListTake.asStateFlow()
    private val _some = MutableStateFlow(0f)
    val some = _some.asStateFlow()

    private val _chartData = MutableStateFlow(
        listOf(
            LineData(
                name = "",
                values = listOf<ChartPoint>(ChartPoint(value = 0f, time = kotlin.time.Clock.System.now())),
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

    private val _lastUpdate = MutableStateFlow("")
    val lastUpdate = _lastUpdate.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    private val _dataTable = MutableStateFlow<List<ModelProcess>>(emptyList())
    private val _dataGroupTable = MutableStateFlow<Map<String, List<ModelProcess>>>(emptyMap())
    val dataGroupTable = _dataGroupTable.asStateFlow()
    val format = LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    fun getDataForTable() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                while (true) {
                    _isRefreshing.value = true
                    val dataOld = _dataTable.value
                    val newData = serviceProcess.getFactoryData()
                    //time server responde
                    val currentMoment: kotlin.time.Instant = kotlin.time.Clock.System.now()
                    val datetime = currentMoment.plus(3.hours)//.toLocalDateTime(TimeZone.currentSystemDefault())

                    _lastUpdate.value =
                        datetime.toString()//.format(format)//"${datetime.hour.toString().padStart(2, '0')}:${datetime.minute.toString().padStart(2, '0')}:${datetime.second.toString().padStart(2, '0')}"

                    //  println(dataOld)
                    val newDataWithTrend = newData.map { newItem ->
                        val oldItem = dataOld.find {
                            //  println(it.processName)
                            it.processName == newItem.processName
                        }

                        val calculateTrend = when {
                            oldItem == null -> 0
                            newItem.processValue.toDouble() > oldItem.processValue.toDouble() -> 1
                            newItem.processValue.toDouble() < oldItem.processValue.toDouble() -> -1
                            else -> 0
                        }
                        newItem.copy(trend = calculateTrend)
                    }

                    val dataGroups = newDataWithTrend.groupBy { it.processNamePLC }
                    //println(dataGroups)
                    _dataGroupTable.value = dataGroups
                    _dataTable.value = newDataWithTrend
                    println(_dataTable.value)
                    delay(1000)
                }
            } catch (e: Exception) {
                _isLoading.value = false
            } finally {
                _isRefreshing.value = false
                delay(2000)
            }

        }
    }

    private val _dateTimeSecondShow = MutableStateFlow(0)
    val dateTimeSecondShow = _dateTimeSecondShow.asStateFlow()
    fun dateShow() {
        viewModelScope.launch {
            var count = 0
            while (true) {

                _dateTimeSecondShow.update { count++ }
                delay(1000)
                if (count > 10) count = 0
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
            time = kotlin.time.Clock.System.now()
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
                        ChartPoint(value = 0f, time = kotlin.time.Clock.System.now())
                    ),
                    color = Color.Blue
                )
            ))
        }
    }

    private fun addNewValueToChart5(index: IndexChart, value: Float) {
        val newPoint = ChartPoint(
            value = value,
            time = kotlin.time.Clock.System.now()
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

    fun sound(){
        notificationAlarm()
    }

    private val _messagesStatus = MutableStateFlow<List<MessageStatus>>(emptyList())
    val messageStatus = _messagesStatus.asStateFlow()
    private val _status = MutableStateFlow<List<Boolean>>(emptyList())
    private val _lastUpdateStatus = MutableStateFlow("")
    val lastUpdateStatus = _lastUpdateStatus.asStateFlow()
    fun getMessagesFromBot() {

        viewModelScope.launch {
            while (true) {
                val currentMoment: kotlin.time.Instant = kotlin.time.Clock.System.now()
                val datetime = currentMoment.plus(3.hours)
                val mess/*_messagesStatus.value */ = serviceProcess.getMessagesFromBot()
                if (_status.value.isNotEmpty()) {
                   // println("if (_status.value.isNotEmpty()) ${_status.value.isNotEmpty()}")
                    _messagesStatus.value = mess.mapIndexed { index, it ->
                        if (it.status != _status.value[index]) {
                            //println(" if (it.status != _status.value[index]) ${it.status != _status.value[index]}")
                            notificationAlarm()
                            it.copy(
                                date = datetime.toString()
                            )
                        } else {
                            println("else (it.status != _status.value[index]) ${it.status != _status.value[index]}")
                            it.copy(date = _messagesStatus.value[index].date)
                        }

                    }
                } else _messagesStatus.value = mess
                _status.value = mess.map { it.status }
                _lastUpdateStatus.value = datetime.toString()
                /* println(_messagesStatus.value)*/



                delay(3000)
            }
        }

    }


}

