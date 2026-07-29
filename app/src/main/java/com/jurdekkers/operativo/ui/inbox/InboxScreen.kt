package com.jurdekkers.operativo.ui.inbox

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.ui.CapturedItemSummary
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.SectionTitle
import com.jurdekkers.operativo.ui.inputLabel
import com.jurdekkers.operativo.ui.label

@Composable
fun InboxScreen(
    inboxItems: List<CapturedItemEntity>,
    onConfirm: (CapturedItemEntity) -> Unit,
    onIgnore: (CapturedItemEntity) -> Unit,
    onTransformToTask: (CapturedItemEntity) -> Unit,
    onMoveToCalendar: (CapturedItemEntity) -> Unit,
    onMoveToArchive: (CapturedItemEntity) -> Unit,
    onUpdate: (CapturedItemEntity, String, String, ItemDestination, Int?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionTitle("Inbox / Da controllare") }
        item {
            Text("Qui confermi dove deve finire ogni elemento: To do, Calendario o Archivio.")
        }
        if (inboxItems.isEmpty()) {
            item { EmptyCard("Nessun elemento da controllare.") }
        } else {
            items(inboxItems, key = { it.id }) { item ->
                InboxItemCard(
                    item = item,
                    onConfirm = { onConfirm(item) },
                    onIgnore = { onIgnore(item) },
                    onTransformToTask = { onTransformToTask(item) },
                    onMoveToCalendar = { onMoveToCalendar(item) },
                    onMoveToArchive = { onMoveToArchive(item) },
                    onUpdate = { title, description, type, priority ->
                        onUpdate(item, title, description, type, priority)
                    }
                )
            }
        }
    }
}

@Composable
private fun InboxItemCard(
    item: CapturedItemEntity,
    onConfirm: () -> Unit,
    onIgnore: () -> Unit,
    onTransformToTask: () -> Unit,
    onMoveToCalendar: () -> Unit,
    onMoveToArchive: () -> Unit,
    onUpdate: (String, String, ItemDestination, Int?) -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var title by remember(item.id) { mutableStateOf(item.title) }
    var description by remember(item.id) { mutableStateOf(item.description.orEmpty()) }
    var destination by remember(item.id) { mutableStateOf(item.destination) }
    var priorityText by remember(item.id) { mutableStateOf(item.priority?.toString().orEmpty()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (editing) {
                OutlinedTextField(title, { title = it }, label = { Text("Titolo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(description, { description = it }, label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)
                OutlinedTextField(priorityText, { priorityText = it.filter(Char::isDigit) }, label = { Text("Priorita") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                DestinationSelector(selected = destination, onSelected = { destination = it })
                Button(
                    onClick = {
                        onUpdate(title, description, destination, priorityText.toIntOrNull())
                        editing = false
                    },
                    enabled = title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salva modifiche")
                }
            } else {
                CapturedItemSummary(item = item)
                Text(
                    text = "Destinazione proposta: ${item.destination.label}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f)) {
                        Text("Conferma in ${item.destination.label}")
                    }
                    Button(onClick = { editing = true }, modifier = Modifier.weight(1f)) {
                        Text("Modifica")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.destination != ItemDestination.TODO) {
                        Button(onClick = onTransformToTask, modifier = Modifier.weight(1f)) {
                            Text("To do")
                        }
                    }
                    if (item.destination != ItemDestination.CALENDAR) {
                        Button(onClick = onMoveToCalendar, modifier = Modifier.weight(1f)) {
                            Text("Calendario")
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (item.destination != ItemDestination.ARCHIVE) {
                        Button(onClick = onMoveToArchive, modifier = Modifier.weight(1f)) {
                            Text("Archivio")
                        }
                    }
                    TextButton(onClick = onIgnore, modifier = Modifier.weight(1f)) {
                        Text("Ignora")
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationSelector(
    selected: ItemDestination,
    onSelected: (ItemDestination) -> Unit
) {
    Column {
        listOf(ItemDestination.TODO, ItemDestination.CALENDAR, ItemDestination.ARCHIVE).forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == option, onClick = { onSelected(option) })
                Text(option.inputLabel)
            }
        }
    }
}
