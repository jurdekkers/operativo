package com.jurdekkers.operativo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.data.local.OperativoDatabase
import com.jurdekkers.operativo.data.repository.OperativoRepository
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OperativoUiState(
    val allItems: List<CapturedItemEntity> = emptyList(),
    val inboxItems: List<CapturedItemEntity> = emptyList(),
    val taskItems: List<CapturedItemEntity> = emptyList(),
    val calendarItems: List<CapturedItemEntity> = emptyList(),
    val archiveItems: List<CapturedItemEntity> = emptyList()
) {
    val openTasks: List<CapturedItemEntity> =
        taskItems.filter { it.status != ItemStatus.COMPLETED && it.status != ItemStatus.IGNORED }

    val dueSoonTasks: List<CapturedItemEntity> =
        openTasks.filter { it.dueDate != null }.take(3)

    val quickCheckItems: List<CapturedItemEntity> =
        (openTasks + calendarItems.filter { it.status == ItemStatus.CONFIRMED } + inboxItems)
            .distinctBy { it.id }
            .take(5)
}

class OperativoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OperativoRepository(
        OperativoDatabase.get(application).dao()
    )

    val uiState: StateFlow<OperativoUiState> = combine(
        repository.allItems,
        repository.inboxItems,
        repository.taskItems,
        repository.calendarItems,
        repository.archiveItems
    ) { allItems, inboxItems, taskItems, calendarItems, archiveItems ->
        OperativoUiState(
            allItems = allItems,
            inboxItems = inboxItems,
            taskItems = taskItems,
            calendarItems = calendarItems,
            archiveItems = archiveItems
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OperativoUiState()
    )

    fun capture(text: String, type: ItemType) {
        viewModelScope.launch {
            repository.capture(text, type)
        }
    }

    fun updateItem(
        item: CapturedItemEntity,
        title: String,
        description: String,
        type: ItemType,
        priority: Int?
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.update(
                item.copy(
                    title = title.trim(),
                    description = description.trim().ifBlank { null },
                    type = type,
                    priority = priority
                )
            )
        }
    }

    fun confirm(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.confirm(item)
        }
    }

    fun ignore(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.ignore(item)
        }
    }

    fun transformToTask(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.transformToTask(item)
        }
    }

    fun moveToCalendar(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.moveToCalendar(item)
        }
    }

    fun moveToArchive(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.moveToArchive(item)
        }
    }

    fun setTaskCompleted(item: CapturedItemEntity, completed: Boolean) {
        viewModelScope.launch {
            repository.setCompleted(item, completed)
        }
    }

    fun delete(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
