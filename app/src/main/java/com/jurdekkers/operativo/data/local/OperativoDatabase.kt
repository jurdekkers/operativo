package com.jurdekkers.operativo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CapturedItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OperativoConverters::class)
abstract class OperativoDatabase : RoomDatabase() {
    abstract fun dao(): OperativoDao

    companion object {
        @Volatile
        private var instance: OperativoDatabase? = null

        fun get(context: Context): OperativoDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    OperativoDatabase::class.java,
                    "operativo.db"
                ).build().also { instance = it }
            }
        }
    }
}
