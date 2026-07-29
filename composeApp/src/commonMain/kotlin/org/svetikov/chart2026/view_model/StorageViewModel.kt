package org.svetikov.chart2026.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.internal.JSJoda.ChronoUnit
import org.svetikov.chart2026.ServiceProcess
import org.svetikov.chart2026.models.CarSend
import kotlin.time.Clock


class StorageViewModel(
    val serviceProcess: ServiceProcess = ServiceProcess()
) : ViewModel() {
    private val _storage = MutableStateFlow(listOf<CarSend>())
    val storage = _storage.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        println("start")
    }

    fun getAllBoards() {
        viewModelScope.launch {
            while (isActive) {
                try {
                    _isRefreshing.value = true
                    _storage.value = serviceProcess.getAllStorageBoard()
                    delay(10000)
                } finally {
                    _isRefreshing.value = false
                }

            }
        }


    }

    fun deleteBoardFromID(idModel: String) {
        viewModelScope.launch {
            try {
                _isRefreshing.value = true
                serviceProcess.deleteBoard(idModel)
                delay(1000)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun getHoursAgo(dateString: String = "2026-07-25 15:14:23.476898"): String {
        if (dateString.isNotEmpty()) {
            val isoString = dateString.replace(" ", "T")
            val past = LocalDateTime.parse(isoString)
            val nowDate = Clock.System.now()
            val now = LocalDateTime.parse(nowDate.toString().replace("Z", ""))
            val pastHours = toTotalHours(past.year, past.monthNumber, past.dayOfMonth, past.hour)
            val nowHours = toTotalHours(now.year, now.monthNumber, now.dayOfMonth, now.hour)
            val hour = nowHours - pastHours+2
            return "$hour"
        }
        return "0h"
    }

    private fun toTotalHours(year: Int, month: Int, day: Int, hour: Int): Long {
        var m = month
        var y = year
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val days = 365L * y + y / 4 - y / 100 + y / 400 + (153L * (m + 1)) / 5 + day
        return days * 24 + hour
    }

    fun boardName(name:String):String{
        return when(name){
            "car1"->"5511"
            "car3"->"5511 M"
            "car2"->"5514"
            "car4"->"5514 M"
            else -> "No name $name"
        }
    }
}