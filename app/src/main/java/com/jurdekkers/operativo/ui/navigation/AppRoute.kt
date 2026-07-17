package com.jurdekkers.operativo.ui.navigation

enum class AppRoute(
    val route: String,
    val label: String
) {
    Home("home", "Home"),
    Inbox("inbox", "Inbox"),
    Calendar("calendar", "Calendario"),
    Tasks("tasks", "To do"),
    Archive("archive", "Archivio"),
    ManualCapture("manual_capture", "Manuale"),
    VoiceCapture("voice_capture", "Voce")
}
