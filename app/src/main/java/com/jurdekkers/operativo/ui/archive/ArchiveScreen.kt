package com.jurdekkers.operativo.ui.archive

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.domain.model.ArchiveCategory
import com.jurdekkers.operativo.ui.CapturedItemSummary
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.SectionTitle
import com.jurdekkers.operativo.ui.label
import com.jurdekkers.operativo.ui.shortLabel

@Composable
fun ArchiveScreen(
    archiveItems: List<CapturedItemEntity>,
    onAddArchive: (String, String, ArchiveCategory, String?, String?, String?) -> Unit
) {
    val groupedItems = archiveItems.groupBy { it.archiveCategory }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionTitle("Archivio") }
        item {
            Text("Schede, documenti, foto, file Excel, Word, PDF e altri allegati.")
        }
        item {
            ArchiveEntryForm(onSave = onAddArchive)
        }
        item { SectionTitle("Cartelle") }
        ArchiveCategory.entries.forEach { category ->
            item {
                ArchiveFolderCard(
                    category = category,
                    count = groupedItems[category].orEmpty().size
                )
            }
        }
        if (archiveItems.isEmpty()) {
            item { EmptyCard("Nessun elemento archiviato.") }
        } else {
            ArchiveCategory.entries.forEach { category ->
                val items = groupedItems[category].orEmpty()
                if (items.isNotEmpty()) {
                    item { SectionTitle(category.label) }
                    items(items, key = { it.id }) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                CapturedItemSummary(item)
                                item.attachmentName?.let {
                                    Text(
                                        text = "Allegato: $it",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                item.attachmentMimeType?.let {
                                    Text("Tipo file: $it", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArchiveEntryForm(
    onSave: (String, String, ArchiveCategory, String?, String?, String?) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(ArchiveCategory.GENERAL) }
    var attachmentUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentName by remember { mutableStateOf<String?>(null) }
    var attachmentMimeType by remember { mutableStateOf<String?>(null) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            attachmentUri = uri
            attachmentName = uri.lastPathSegment ?: "File allegato"
            attachmentMimeType = context.contentResolver.getType(uri)
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo scheda o documento") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Note, riferimenti, collegamenti") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            Text("Cartella", style = MaterialTheme.typography.titleMedium)
            ArchiveCategory.entries.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { option ->
                        val selected = category == option
                        if (selected) {
                            Button(
                                onClick = { category = option },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(option.shortLabel)
                            }
                        } else {
                            OutlinedButton(
                                onClick = { category = option },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(option.shortLabel)
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Column(modifier = Modifier.weight(1f)) {}
                    }
                }
            }
            OutlinedButton(
                onClick = { filePicker.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(attachmentName ?: "Allega file")
            }
            Button(
                onClick = {
                    onSave(
                        title,
                        description,
                        category,
                        attachmentUri?.toString(),
                        attachmentName,
                        attachmentMimeType
                    )
                    title = ""
                    description = ""
                    category = ArchiveCategory.GENERAL
                    attachmentUri = null
                    attachmentName = null
                    attachmentMimeType = null
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salva in ${category.shortLabel}")
            }
        }
    }
}

@Composable
private fun ArchiveFolderCard(
    category: ArchiveCategory,
    count: Int
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(category.label, style = MaterialTheme.typography.titleMedium)
            Text("$count elementi", style = MaterialTheme.typography.bodySmall)
        }
    }
}
