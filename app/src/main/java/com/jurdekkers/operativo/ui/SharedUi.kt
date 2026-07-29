package com.jurdekkers.operativo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.domain.model.ItemType
import java.text.DateFormat
import java.util.Date

@Composable
fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun EmptyCard(text: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CapturedItemSummary(item: CapturedItemEntity) {
    Column {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${item.destination.label} - ${item.status.label}",
            style = MaterialTheme.typography.bodySmall
        )
        if (item.destination == ItemDestination.ARCHIVE) {
            Text(
                text = "Cartella: ${item.archiveCategory.label}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item.description?.takeIf { it.isNotBlank() }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        item.dueDate?.let {
            Text(
                text = "Scadenza: ${it.formatDate()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item.priority?.let {
            Text(
                text = "Priorita: ${it.priorityLabel()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
        item.attachmentName?.let {
            Text(
                text = "Allegato: $it",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = "Creato: ${item.createdAt.formatDate()}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

val ItemType.label: String
    get() = when (this) {
        ItemType.NOTE -> "Archivio"
        ItemType.IDEA -> "Archivio"
        ItemType.TASK -> "To do"
        ItemType.CALENDAR -> "Calendario"
        ItemType.ARCHIVE -> "Archivio"
    }

val ItemType.inputLabel: String
    get() = when (this) {
        ItemType.NOTE -> "Nota / Archivio"
        ItemType.IDEA -> "Nota / Archivio"
        ItemType.TASK -> "To do"
        ItemType.CALENDAR -> "Calendario"
        ItemType.ARCHIVE -> "Archivio"
    }

val ItemStatus.label: String
    get() = when (this) {
        ItemStatus.INBOX -> "Da controllare"
        ItemStatus.CONFIRMED -> "Confermato"
        ItemStatus.COMPLETED -> "Completato"
        ItemStatus.IGNORED -> "Ignorato"
    }

val ItemDestination.label: String
    get() = when (this) {
        ItemDestination.TODO -> "To do"
        ItemDestination.CALENDAR -> "Calendario"
        ItemDestination.ARCHIVE -> "Archivio"
    }

val ItemDestination.inputLabel: String
    get() = when (this) {
        ItemDestination.TODO -> "To do list"
        ItemDestination.CALENDAR -> "Calendario"
        ItemDestination.ARCHIVE -> "Archivio"
    }

val ArchiveCategory.label: String
    get() = when (this) {
        ArchiveCategory.GENERAL -> "Generale"
        ArchiveCategory.PRACTICES -> "Pratiche"
        ArchiveCategory.AGRICULTURE -> "Azienda agricola"
        ArchiveCategory.PROPERTIES -> "Immobili"
        ArchiveCategory.PERSONAL -> "Personale"
        ArchiveCategory.DOCUMENTS -> "Documenti"
        ArchiveCategory.PHOTOS -> "Foto"
        ArchiveCategory.PEOPLE -> "Persone"
        ArchiveCategory.CLIENTS -> "Clienti"
        ArchiveCategory.SUPPLIERS -> "Fornitori"
        ArchiveCategory.VEHICLES -> "Mezzi"
        ArchiveCategory.PLACES -> "Luoghi"
    }

val ArchiveCategory.shortLabel: String
    get() = when (this) {
        ArchiveCategory.AGRICULTURE -> "Agricola"
        ArchiveCategory.PROPERTIES -> "Immobili"
        ArchiveCategory.SUPPLIERS -> "Fornitori"
        else -> label
    }

fun Long.formatDate(): String {
    return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(this))
}

fun Int.priorityLabel(): String {
    return when (this) {
        1 -> "Bassa"
        2 -> "Media"
        3 -> "Alta"
        else -> toString()
    }
}
