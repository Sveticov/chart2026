package org.svetikov.chart2026

import io.ktor.client.HttpClient
import io.ktor.client.call.body
//import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DataConversion.install
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json


import kotlinx.serialization.json.Json

class ServiceProcess {
    private val client = HttpClient() {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getFactoryData()=
        client.get("http://10.10.12.137:8083/model_process").body<List<ModelProcess>>()

    suspend fun getMessagesFromBot() =
        client.get ("http://10.10.12.137:8080/bot/messages").body<List<MessageStatus>>()
}