package com.jurdekkers.operativo.data.repository

import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.data.local.OperativoDao
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType
import java.text.SimpleDateFormat
import java.util.Locale

class OperativoRepository(
    private val dao: OperativoDao
) {
    val allItems = dao.observeAll()
    val inboxItems = dao.observeInbox()
    val taskItems = dao.observeTasks()
    val calendarItems = dao.observeCalendarItems()
    val archiveItems = dao.observeArchiveItems()

    suspend fun capture(text: String, destination: ItemDestination) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return

        dao.insert(
            CapturedItemEntity(
                originalText = cleanText,
                title = cleanText.lineSequence().firstOrNull().orEmpty().take(80),
                description = cleanText,
                type = ItemType.NOTE,
                destination = destination
            )
        )
    }

    suspend fun addDirect(
        title: String,
        description: String,
        dueDateText: String,
        priority: Int?,
        destination: ItemDestination
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) return

        dao.insert(
            CapturedItemEntity(
                originalText = cleanTitle,
                title = cleanTitle.take(80),
                description = description.trim().ifBlank { null },
                type = destination.toItemType(),
                destination = destination,
                status = ItemStatus.CONFIRMED,
                dueDate = dueDateText.parseDateOrNull(),
                priority = priority
            )
        )
    }

    suspend fun addArchiveDirect(
        title: String,
        description: String,
        category: ArchiveCategory,
        attachmentUri: String?,
        attachmentName: String?,
        attachmentMimeType: String?
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) return

        dao.insert(
            CapturedItemEntity(
                originalText = cleanTitle,
                title = cleanTitle.take(80),
                description = description.trim().ifBlank { null },
                type = ItemType.ARCHIVE,
                destination = ItemDestination.ARCHIVE,
                status = ItemStatus.CONFIRMED,
                archiveCategory = category,
                attachmentUri = attachmentUri,
                attachmentName = attachmentName,
                attachmentMimeType = attachmentMimeType
            )
        )
    }

    suspend fun update(item: CapturedItemEntity) {
        dao.update(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun confirm(item: CapturedItemEntity) {
        update(item.copy(status = ItemStatus.CONFIRMED))
    }

    suspend fun ignore(item: CapturedItemEntity) {
        update(item.copy(status = ItemStatus.IGNORED))
    }

    suspend fun transformToTask(item: CapturedItemEntity) {
        update(item.copy(destination = ItemDestination.TODO))
    }

    suspend fun moveToCalendar(item: CapturedItemEntity) {
        update(item.copy(destination = ItemDestination.CALENDAR))
    }

    suspend fun moveToArchive(item: CapturedItemEntity) {
        update(item.copy(destination = ItemDestination.ARCHIVE))
    }

    suspend fun setCompleted(item: CapturedItemEntity, completed: Boolean) {
        update(item.copy(status = if (completed) ItemStatus.COMPLETED else ItemStatus.CONFIRMED))
    }

    suspend fun setFirst(item: CapturedItemEntity, first: Boolean) {
        update(item.copy(isFirst = first))
    }

    suspend fun delete(item: CapturedItemEntity) {
        dao.delete(item)
    }

    private fun ItemDestination.toItemType(): ItemType {
        return when (this) {
            ItemDestination.TODO -> ItemType.TASK
            ItemDestination.CALENDAR -> ItemType.CALENDAR
            ItemDestination.ARCHIVE -> ItemType.ARCHIVE
        }
    }

    private fun String.parseDateOrNull(): Long? {
        val cleanValue = trim()
        if (cleanValue.isBlank()) return null

        return runCatching {
            SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN).apply {
                isLenient = false
            }.parse(cleanValue)?.time
        }.getOrNull()
    }
}
