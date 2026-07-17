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
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.SectionTitle
import com.jurdekkers.operativo.ui.formatDate

@Composable
fun TasksScreen(
    taskItems: List<CapturedItemEntity>,
    onCompletedChange: (CapturedItemEntity, Boolean) -> Unit,
    onDelete: (CapturedItemEntity) -> Unit,
    onCaptureClick: () -> Unit
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
                TextButton(onClick = onCaptureClick) {
                    Text("Nuovo")
                }
            }
        }
        if (taskItems.isEmpty()) {
            item { EmptyCard("Nessuna cosa imminente da fare.") }
        } else {
            items(taskItems, key = { it.id }) { task ->
                TaskCard(
                    task = task,
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
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = task.status == ItemStatus.COMPLETED,
                onCheckedChange = onCompletedChange
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                task.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Text("Creata: ${task.createdAt.formatDate()}", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onDelete) {
                Text("Elimina")
            }
        }
    }
}
