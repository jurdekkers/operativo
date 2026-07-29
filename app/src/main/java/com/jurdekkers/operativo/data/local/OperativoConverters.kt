package com.jurdekkers.operativo.data.local

import androidx.room.TypeConverter
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType

class OperativoConverters {
    @TypeConverter
    fun itemTypeToString(value: ItemType): String = value.name

    @TypeConverter
    fun stringToItemType(value: String): ItemType = ItemType.valueOf(value)

    @TypeConverter
    fun itemStatusToString(value: ItemStatus): String = value.name

    @TypeConverter
    fun stringToItemStatus(value: String): ItemStatus = ItemStatus.valueOf(value)

    @TypeConverter
    fun itemDestinationToString(value: ItemDestination): String = value.name

    @TypeConverter
    fun stringToItemDestination(value: String): ItemDestination = ItemDestination.valueOf(value)

    @TypeConverter
    fun archiveCategoryToString(value: ArchiveCategory): String = value.name

    @TypeConverter
    fun stringToArchiveCategory(value: String): ArchiveCategory = ArchiveCategory.valueOf(value)
}
