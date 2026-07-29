package com.jurdekkers.operativo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OperativoDao {
    @Query("SELECT * FROM captured_items ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE status = 'INBOX' ORDER BY createdAt DESC")
    fun observeInbox(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE destination = 'TODO' AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END ASC, priority DESC, dueDate IS NULL ASC, dueDate ASC, createdAt DESC")
    fun observeTasks(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE destination = 'CALENDAR' AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY createdAt DESC")
    fun observeCalendarItems(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE destination = 'ARCHIVE' AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY createdAt DESC")
    fun observeArchiveItems(): Flow<List<CapturedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CapturedItemEntity): Long

    @Update
    suspend fun update(item: CapturedItemEntity)

    @Delete
    suspend fun delete(item: CapturedItemEntity)
}
