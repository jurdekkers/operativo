package com.jurdekkers.operativo.ui.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.ui.CapturedItemSummary
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.SectionTitle

@Composable
fun ArchiveScreen(
    archiveItems: List<CapturedItemEntity>
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionTitle("Archivio") }
        item {
            Text("Qui andranno note, documenti, pratiche, foto, persone, aziende e immobili.")
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Cartelle previste")
                    Text("Attivita, Pratiche, Azienda agricola, Immobili, Personale")
                    Text("Documenti, Foto, Note, Persone, Clienti, Fornitori, Mezzi, Luoghi")
                }
            }
        }
        if (archiveItems.isEmpty()) {
            item { EmptyCard("Nessun elemento archiviato.") }
        } else {
            items(archiveItems, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CapturedItemSummary(item)
                    }
                }
            }
        }
    }
}
