package com.jurdekkers.operativo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DirectEntryForm(
    titleLabel: String,
    descriptionLabel: String,
    showDueDate: Boolean,
    showPriority: Boolean,
    saveLabel: String,
    onSave: (String, String, String, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf<Int?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(titleLabel) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(descriptionLabel) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )
            if (showDueDate) {
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Scadenza, es. 28/07/2026") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            if (showPriority) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3).forEach { value ->
                        val label = when (value) {
                            1 -> "Bassa"
                            2 -> "Media"
                            else -> "Alta"
                        }
                        Button(
                            onClick = { priority = value },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (priority == value) "$label *" else label)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    onSave(title, description, dueDate, priority)
                    title = ""
                    description = ""
                    dueDate = ""
                    priority = null
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(saveLabel)
            }
        }
    }
}
