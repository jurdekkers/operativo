package com.jurdekkers.operativo.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.ui.DirectEntryForm
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.SectionTitle
import com.jurdekkers.operativo.ui.formatDate
import com.jurdekkers.operativo.ui.priorityLabel

@Composable
fun TasksScreen(
    taskItems: List<CapturedItemEntity>,
    onAddDirect: (String, String, String, Int?) -> Unit,
    onFirstChange: (CapturedItemEntity, Boolean) -> Unit,
    onCompletedChange: (CapturedItemEntity, Boolean) -> Unit,
    onDelete: (CapturedItemEntity) -> Unit,
) {
    var pendingDelete by remember { mutableStateOf<CapturedItemEntity?>(null) }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("To do list")
            }
        }
        item {
            DirectEntryForm(
                titleLabel = "Cosa devi fare",
                descriptionLabel = "Note operative",
                showDueDate = true,
                showPriority = true,
                saveLabel = "Salva in To do",
                onSave = onAddDirect
            )
        }
        if (taskItems.isEmpty()) {
            item { EmptyCard("Nessuna cosa imminente da fare.") }
        } else {
            items(taskItems, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onFirstChange = { onFirstChange(task, it) },
                    onCompletedChange = { onCompletedChange(task, it) },
                    onDelete = { pendingDelete = task }
                )
            }
        }
    }

    pendingDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminare dalla To do list?") },
            text = { Text(task.title) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(task)
                        pendingDelete = null
                    }
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
private fun TaskCard(
    task: CapturedItemEntity,
    onFirstChange: (Boolean) -> Unit,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(task.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                task.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                task.dueDate?.let {
                    Text("Scadenza: ${it.formatDate()}", style = MaterialTheme.typography.bodySmall)
                }
                task.priority?.let {
                    Text("Priorita: ${it.priorityLabel()}", style = MaterialTheme.typography.bodySmall)
                }
                Text("Creata: ${task.createdAt.formatDate()}", style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Checkbox(
                    checked = task.isFirst,
                    enabled = task.status != ItemStatus.COMPLETED,
                    onCheckedChange = onFirstChange
                )
                Text("First", modifier = Modifier.weight(1f))
                Checkbox(
                    checked = task.status == ItemStatus.COMPLETED,
                    onCheckedChange = onCompletedChange
                )
                Text("Fatto", modifier = Modifier.weight(1f))
                TextButton(onClick = onDelete) {
                    Text("Elimina")
                }
            }
        }
    }
}
