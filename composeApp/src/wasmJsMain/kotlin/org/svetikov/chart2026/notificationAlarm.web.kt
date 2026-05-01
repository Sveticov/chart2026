package org.svetikov.chart2026

import org.w3c.dom.HTMLAudioElement

@OptIn(ExperimentalWasmJsInterop::class)
actual fun notificationAlarm() {
    val audio = kotlinx.browser.document.createElement("audio") as HTMLAudioElement
    audio.src = "composeApp/build/dist/wasmJs/productionExecutable/composeResources/chart2026.composeapp.generated.resources/files/ringtone-025.mp3"//"composeApp/src/commonMain/composeResources/files/ringtone-025.mp3"//"https://actions.google.com/sounds/v1/alarms/beep_short.ogg"
    audio.play()
}