package com.jurdekkers.operativo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType

@Entity(tableName = "captured_items")
data class CapturedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalText: String,
    val title: String,
    val description: String? = null,
    val type: ItemType,
    val destination: ItemDestination = ItemDestination.ARCHIVE,
    val status: ItemStatus = ItemStatus.INBOX,
    val dueDate: Long? = null,
    val priority: Int? = null,
    val isFirst: Boolean = false,
    val archiveCategory: ArchiveCategory = ArchiveCategory.GENERAL,
    val attachmentUri: String? = null,
    val attachmentName: String? = null,
    val attachmentMimeType: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
