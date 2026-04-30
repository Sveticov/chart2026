package org.svetikov.chart2026

import org.w3c.dom.HTMLAudioElement

actual fun notificationAlarm() {
    val audio = kotlinx.browser.document.createElement("audio") as HTMLAudioElement
    audio.src = "composeApp/src/commonMain/composeResources/files/ringtone-025.mp3"//"https://actions.google.com/sounds/v1/alarms/beep_short.ogg"
    audio.play()
}