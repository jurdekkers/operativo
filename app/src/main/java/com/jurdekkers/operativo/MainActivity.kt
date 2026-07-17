package com.jurdekkers.operativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jurdekkers.operativo.domain.model.ItemType
import com.jurdekkers.operativo.ui.archive.ArchiveScreen
import com.jurdekkers.operativo.ui.calendar.CalendarScreen
import com.jurdekkers.operativo.ui.OperativoViewModel
import com.jurdekkers.operativo.ui.capture.CaptureScreen
import com.jurdekkers.operativo.ui.dashboard.TodayScreen
import com.jurdekkers.operativo.ui.inbox.InboxScreen
import com.jurdekkers.operativo.ui.navigation.AppRoute
import com.jurdekkers.operativo.ui.tasks.TasksScreen
import com.jurdekkers.operativo.ui.theme.OperativoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OperativoTheme {
                OperativoApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperativoApp(viewModel: OperativoViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppRoute.Home.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Operativo", fontWeight = FontWeight.Bold)
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(AppRoute.Home, AppRoute.Calendar, AppRoute.Tasks, AppRoute.Archive).forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = { navController.navigateSingleTop(item.route) },
                        icon = { Text(item.label.take(1)) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppRoute.Home.route) {
                TodayScreen(
                    uiState = uiState,
                    onVoiceInputClick = { navController.navigateSingleTop(AppRoute.VoiceCapture.route) },
                    onManualInputClick = { navController.navigateSingleTop(AppRoute.ManualCapture.route) },
                    onInboxClick = { navController.navigateSingleTop(AppRoute.Inbox.route) },
                    onCompletedChange = viewModel::setTaskCompleted
                )
            }
            composable(AppRoute.ManualCapture.route) {
                CaptureScreen(
                    title = "Inserimento manuale",
                    helperText = "Scrivi il testo, scegli se deve andare in To do, Calendario o Archivio, poi conferma tutto dalla Inbox.",
                    initialType = ItemType.NOTE,
                    voiceEnabled = false,
                    onSave = { text, type ->
                        viewModel.capture(text, type)
                        navController.navigateSingleTop(AppRoute.Inbox.route)
                    }
                )
            }
            composable(AppRoute.VoiceCapture.route) {
                CaptureScreen(
                    title = "Inserimento vocale",
                    helperText = "Detta il testo, controlla la trascrizione e scegli la destinazione proposta.",
                    initialType = ItemType.TASK,
                    voiceEnabled = true,
                    onSave = { text, type ->
                        viewModel.capture(text, type)
                        navController.navigateSingleTop(AppRoute.Inbox.route)
                    }
                )
            }
            composable(AppRoute.Inbox.route) {
                InboxScreen(
                    inboxItems = uiState.inboxItems,
                    onConfirm = { item ->
                        viewModel.confirm(item)
                        navController.navigateSingleTop(item.type.destinationRoute())
                    },
                    onIgnore = viewModel::ignore,
                    onTransformToTask = { item ->
                        viewModel.transformToTask(item)
                        navController.navigateSingleTop(AppRoute.Tasks.route)
                    },
                    onMoveToCalendar = { item ->
                        viewModel.moveToCalendar(item)
                        navController.navigateSingleTop(AppRoute.Calendar.route)
                    },
                    onMoveToArchive = { item ->
                        viewModel.moveToArchive(item)
                        navController.navigateSingleTop(AppRoute.Archive.route)
                    },
                    onUpdate = viewModel::updateItem
                )
            }
            composable(AppRoute.Calendar.route) {
                CalendarScreen(calendarItems = uiState.calendarItems)
            }
            composable(AppRoute.Tasks.route) {
                TasksScreen(
                    taskItems = uiState.openTasks,
                    onCompletedChange = viewModel::setTaskCompleted,
                    onDelete = viewModel::delete,
                    onCaptureClick = { navController.navigateSingleTop(AppRoute.ManualCapture.route) }
                )
            }
            composable(AppRoute.Archive.route) {
                ArchiveScreen(archiveItems = uiState.archiveItems)
            }
        }
    }
}

private fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(AppRoute.Home.route) {
            saveState = true
        }
    }
}

private fun ItemType.destinationRoute(): String {
    return when (this) {
        ItemType.TASK -> AppRoute.Tasks.route
        ItemType.CALENDAR -> AppRoute.Calendar.route
        ItemType.NOTE,
        ItemType.IDEA,
        ItemType.ARCHIVE -> AppRoute.Archive.route
    }
}
