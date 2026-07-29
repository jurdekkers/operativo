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
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
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
                    title = "Cattura rapida",
                    helperText = "Scrivi un appunto veloce, scegli una destinazione proposta e conferma tutto dalla Inbox.",
                    initialDestination = ItemDestination.ARCHIVE,
                    voiceEnabled = false,
                    onSaveToInbox = { text, destination ->
                        viewModel.capture(text, destination)
                        navController.navigateSingleTop(AppRoute.Inbox.route)
                    },
                    onSaveDirect = { text, destination ->
                        viewModel.saveQuickDirect(text, destination)
                        navController.navigateSingleTop(destination.destinationRoute())
                    }
                )
            }
            composable(AppRoute.VoiceCapture.route) {
                CaptureScreen(
                    title = "Inserimento vocale",
                    helperText = "Detta il testo, controlla la trascrizione e scegli la destinazione proposta.",
                    initialDestination = ItemDestination.TODO,
                    voiceEnabled = true,
                    onSaveToInbox = { text, destination ->
                        viewModel.capture(text, destination)
                        navController.navigateSingleTop(AppRoute.Inbox.route)
                    },
                    onSaveDirect = { text, destination ->
                        viewModel.saveQuickDirect(text, destination)
                        navController.navigateSingleTop(destination.destinationRoute())
                    }
                )
            }
            composable(AppRoute.Inbox.route) {
                InboxScreen(
                    inboxItems = uiState.inboxItems,
                    onConfirm = { item ->
                        viewModel.confirm(item)
                        navController.navigateSingleTop(item.destination.destinationRoute())
                    },
                    onIgnore = viewModel::ignore,
                    onTransformToTask = viewModel::transformToTask,
                    onMoveToCalendar = viewModel::moveToCalendar,
                    onMoveToArchive = viewModel::moveToArchive,
                    onUpdate = viewModel::updateItem
                )
            }
            composable(AppRoute.Calendar.route) {
                CalendarScreen(
                    calendarItems = uiState.calendarItems,
                    onAddDirect = { title, description, dueDateText, priority ->
                        viewModel.addDirect(title, description, dueDateText, priority, ItemDestination.CALENDAR)
                    }
                )
            }
            composable(AppRoute.Tasks.route) {
                TasksScreen(
                    taskItems = uiState.orderedTasks,
                    onAddDirect = { title, description, dueDateText, priority ->
                        viewModel.addDirect(title, description, dueDateText, priority, ItemDestination.TODO)
                    },
                    onFirstChange = viewModel::setTaskFirst,
                    onCompletedChange = viewModel::setTaskCompleted,
                    onDelete = viewModel::delete
                )
            }
            composable(AppRoute.Archive.route) {
                ArchiveScreen(
                    archiveItems = uiState.archiveItems,
                    onAddArchive = { title, description, category, uri, name, mimeType ->
                        viewModel.addArchiveDirect(title, description, category, uri, name, mimeType)
                    }
                )
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

private fun ItemDestination.destinationRoute(): String {
    return when (this) {
        ItemDestination.TODO -> AppRoute.Tasks.route
        ItemDestination.CALENDAR -> AppRoute.Calendar.route
        ItemDestination.ARCHIVE -> AppRoute.Archive.route
    }
}

private fun OperativoViewModel.saveQuickDirect(
    text: String,
    destination: ItemDestination
) {
    when (destination) {
        ItemDestination.ARCHIVE -> addArchiveDirect(
            title = text,
            description = "",
            category = ArchiveCategory.GENERAL,
            attachmentUri = null,
            attachmentName = null,
            attachmentMimeType = null
        )
        else -> addDirect(
            title = text,
            description = "",
            dueDateText = "",
            priority = null,
            destination = destination
        )
    }
}
