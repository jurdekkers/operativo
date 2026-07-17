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

    @Query("SELECT * FROM captured_items WHERE type = 'TASK' AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY createdAt DESC")
    fun observeTasks(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE type = 'CALENDAR' AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY createdAt DESC")
    fun observeCalendarItems(): Flow<List<CapturedItemEntity>>

    @Query("SELECT * FROM captured_items WHERE type IN ('NOTE', 'IDEA', 'ARCHIVE') AND status IN ('CONFIRMED', 'COMPLETED') ORDER BY createdAt DESC")
    fun observeArchiveItems(): Flow<List<CapturedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CapturedItemEntity): Long

    @Update
    suspend fun update(item: CapturedItemEntity)

    @Delete
    suspend fun delete(item: CapturedItemEntity)
}
