package com.jurdekkers.operativo.ui.dashboard

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jurdekkers.operativo.data.local.CapturedItemEntity
import com.jurdekkers.operativo.domain.model.ItemStatus
import com.jurdekkers.operativo.ui.EmptyCard
import com.jurdekkers.operativo.ui.OperativoUiState
import com.jurdekkers.operativo.ui.SectionTitle
import com.jurdekkers.operativo.ui.label

@Composable
fun TodayScreen(
    uiState: OperativoUiState,
    onVoiceInputClick: () -> Unit,
    onManualInputClick: () -> Unit,
    onInboxClick: () -> Unit,
    onCompletedChange: (CapturedItemEntity, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onVoiceInputClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Inserimento vocale")
                }
                OutlinedButton(onClick = onManualInputClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Inserimento manuale")
                }
            }
        }

        item { SectionTitle("Controllo rapido") }
        if (uiState.quickCheckItems.isEmpty()) {
            item { EmptyCard("Nessuna urgenza da controllare.") }
        } else {
            items(uiState.quickCheckItems, key = { it.id }) { item ->
                QuickCheckCard(
                    item = item,
                    onInboxClick = onInboxClick,
                    onCompletedChange = { checked -> onCompletedChange(item, checked) }
                )
            }
        }
    }
}

@Composable
private fun QuickCheckCard(
    item: CapturedItemEntity,
    onInboxClick: () -> Unit,
    onCompletedChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item.status == ItemStatus.CONFIRMED) {
                Checkbox(
                    checked = false,
                    onCheckedChange = onCompletedChange
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.type.label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (item.status == ItemStatus.INBOX) {
                OutlinedButton(onClick = onInboxClick) {
                    Text("Controlla")
                }
            }
        }
    }
}
