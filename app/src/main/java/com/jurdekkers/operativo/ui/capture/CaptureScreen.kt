package com.jurdekkers.operativo.ui.capture

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.domain.model.ItemType
import com.jurdekkers.operativo.ui.inputLabel
import java.util.Locale

@Composable
fun CaptureScreen(
    title: String,
    helperText: String,
    initialType: ItemType,
    voiceEnabled: Boolean,
    onSave: (String, ItemType) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(initialType) }
    var voiceMessage by remember { mutableStateOf<String?>(null) }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                .orEmpty()
            val spokenText = matches.firstOrNull().orEmpty()
            if (spokenText.isNotBlank()) {
                text = spokenText
                voiceMessage = "Testo acquisito. Controlla e salva in Inbox."
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = helperText,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (voiceEnabled) {
            OutlinedButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALIAN.toLanguageTag())
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Detta a Operativo")
                    }
                    try {
                        speechLauncher.launch(intent)
                    } catch (_: ActivityNotFoundException) {
                        voiceMessage = "Dettatura non disponibile su questo dispositivo. Puoi scrivere il testo manualmente."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Avvia dettatura")
            }
            voiceMessage?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Testo da controllare") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8
        )
        Text("Dove deve finire?", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DestinationChoice(
                selected = type == ItemType.TASK,
                label = "To do list",
                description = "Cosa imminente da fare",
                onClick = { type = ItemType.TASK }
            )
            DestinationChoice(
                selected = type == ItemType.CALENDAR,
                label = "Calendario",
                description = "Scadenza, appuntamento o ricorrenza",
                onClick = { type = ItemType.CALENDAR }
            )
            DestinationChoice(
                selected = type == ItemType.NOTE || type == ItemType.ARCHIVE,
                label = "Archivio",
                description = "Nota, documento, pratica, persona, azienda o immobile",
                onClick = { type = ItemType.ARCHIVE }
            )
        }
        Button(
            onClick = { onSave(text, type) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva in Inbox per ${type.inputLabel}")
        }
        Text(
            text = "Dopo il salvataggio apri Inbox: potrai confermare, cambiare destinazione o ignorare.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun DestinationChoice(
    selected: Boolean,
    label: String,
    description: String,
    onClick: () -> Unit
) {
    val text = if (selected) "$label selezionato\n$description" else "$label\n$description"
    if (selected) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(text)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(text)
        }
    }
}
