package com.jurdekkers.operativo.data.repository

import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.data.local.OperativoDao
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType

class OperativoRepository(
    private val dao: OperativoDao
) {
    val allItems = dao.observeAll()
    val inboxItems = dao.observeInbox()
    val taskItems = dao.observeTasks()
    val calendarItems = dao.observeCalendarItems()
    val archiveItems = dao.observeArchiveItems()

    suspend fun capture(text: String, type: ItemType) {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return

        dao.insert(
            CapturedItemEntity(
                originalText = cleanText,
                title = cleanText.lineSequence().firstOrNull().orEmpty().take(80),
                description = cleanText,
                type = type
            )
        )
    }

    suspend fun update(item: CapturedItemEntity) {
        dao.update(item.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun confirm(item: CapturedItemEntity) {
        val finalType = when (item.type) {
            ItemType.NOTE,
            ItemType.IDEA,
            ItemType.ARCHIVE -> ItemType.ARCHIVE
            ItemType.TASK -> ItemType.TASK
            ItemType.CALENDAR -> ItemType.CALENDAR
        }
        update(item.copy(type = finalType, status = ItemStatus.CONFIRMED))
    }

    suspend fun ignore(item: CapturedItemEntity) {
        update(item.copy(status = ItemStatus.IGNORED))
    }

    suspend fun transformToTask(item: CapturedItemEntity) {
        update(item.copy(type = ItemType.TASK, status = ItemStatus.CONFIRMED))
    }

    suspend fun moveToCalendar(item: CapturedItemEntity) {
        update(item.copy(type = ItemType.CALENDAR, status = ItemStatus.CONFIRMED))
    }

    suspend fun moveToArchive(item: CapturedItemEntity) {
        update(item.copy(type = ItemType.ARCHIVE, status = ItemStatus.CONFIRMED))
    }

    suspend fun setCompleted(item: CapturedItemEntity, completed: Boolean) {
        update(item.copy(status = if (completed) ItemStatus.COMPLETED else ItemStatus.CONFIRMED))
    }

    suspend fun delete(item: CapturedItemEntity) {
        dao.delete(item)
    }
}
