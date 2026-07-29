package com.jurdekkers.operativo.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.data.local.OperativoDatabase
import com.jurdekkers.operativo.data.repository.OperativoRepository
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.domain.model.ItemStatus
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
    val orderedTasks: List<CapturedItemEntity> =
        taskItems.sortedWith(taskComparator)

    val openTasks: List<CapturedItemEntity> =
        orderedTasks.filter { it.status != ItemStatus.COMPLETED && it.status != ItemStatus.IGNORED }

    val firstTasks: List<CapturedItemEntity> =
        openTasks.filter { it.isFirst }.sortedWith(taskComparator)

    val dueSoonTasks: List<CapturedItemEntity> =
        openTasks.filter { it.dueDate != null }.take(3)

    val quickCheckItems: List<CapturedItemEntity> =
        (openTasks + calendarItems.filter { it.status == ItemStatus.CONFIRMED } + inboxItems)
            .distinctBy { it.id }
            .sortedWith(
                compareBy<CapturedItemEntity> { it.dueDate ?: Long.MAX_VALUE }
                    .thenByDescending { it.priority ?: 0 }
            )
            .take(5)

    companion object {
        private val taskComparator =
            compareBy<CapturedItemEntity> { it.status == ItemStatus.COMPLETED }
                .thenByDescending { it.priority ?: 0 }
                .thenBy { it.dueDate ?: Long.MAX_VALUE }
                .thenByDescending { it.createdAt }
    }
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

    fun capture(text: String, destination: ItemDestination) {
        viewModelScope.launch {
            repository.capture(text, destination)
        }
    }

    fun addDirect(
        title: String,
        description: String,
        dueDateText: String,
        priority: Int?,
        destination: ItemDestination
    ) {
        viewModelScope.launch {
            repository.addDirect(title, description, dueDateText, priority, destination)
        }
    }

    fun addArchiveDirect(
        title: String,
        description: String,
        category: ArchiveCategory,
        attachmentUri: String?,
        attachmentName: String?,
        attachmentMimeType: String?
    ) {
        viewModelScope.launch {
            repository.addArchiveDirect(
                title = title,
                description = description,
                category = category,
                attachmentUri = attachmentUri,
                attachmentName = attachmentName,
                attachmentMimeType = attachmentMimeType
            )
        }
    }

    fun updateItem(
        item: CapturedItemEntity,
        title: String,
        description: String,
        destination: ItemDestination,
        priority: Int?
    ) {
        if (title.isBlank()) return

        viewModelScope.launch {
            repository.update(
                item.copy(
                    title = title.trim(),
                    description = description.trim().ifBlank { null },
                    destination = destination,
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

    fun setTaskFirst(item: CapturedItemEntity, first: Boolean) {
        viewModelScope.launch {
            repository.setFirst(item, first)
        }
    }

    fun delete(item: CapturedItemEntity) {
        viewModelScope.launch {
            repository.delete(item)
        }
    }
}
