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
import com.jurdekkers.operativo.domain.model.ItemDestination
import com.jurdekkers.operativo.ui.inputLabel
import java.util.Locale

@Composable
fun CaptureScreen(
    title: String,
    helperText: String,
    initialDestination: ItemDestination,
    voiceEnabled: Boolean,
    onSaveToInbox: (String, ItemDestination) -> Unit,
    onSaveDirect: (String, ItemDestination) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf(initialDestination) }
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
                selected = destination == ItemDestination.TODO,
                label = "To do list",
                description = "Cosa imminente da fare",
                onClick = { destination = ItemDestination.TODO }
            )
            DestinationChoice(
                selected = destination == ItemDestination.CALENDAR,
                label = "Calendario",
                description = "Scadenza, appuntamento o ricorrenza",
                onClick = { destination = ItemDestination.CALENDAR }
            )
            DestinationChoice(
                selected = destination == ItemDestination.ARCHIVE,
                label = "Archivio",
                description = "Nota, documento, pratica, persona, azienda o immobile",
                onClick = { destination = ItemDestination.ARCHIVE }
            )
        }
        Button(
            onClick = { onSaveToInbox(text, destination) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva in Inbox per ${destination.inputLabel}")
        }
        OutlinedButton(
            onClick = { onSaveDirect(text, destination) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salva subito in ${destination.inputLabel}")
        }
        Text(
            text = "Usa Inbox se vuoi controllare dopo. Usa salvataggio diretto se sai gia dove deve finire.",
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
