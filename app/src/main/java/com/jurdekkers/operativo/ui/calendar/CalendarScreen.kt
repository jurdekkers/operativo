package com.jurdekkers.operativo.ui.calendar

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
fun CalendarScreen(
    calendarItems: List<CapturedItemEntity>
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { SectionTitle("Calendario") }
        item {
            Text("Scadenze, appuntamenti e ricorrenze confermate.")
        }
        if (calendarItems.isEmpty()) {
            item { EmptyCard("Nessun elemento nel calendario.") }
        } else {
            items(calendarItems, key = { it.id }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        CapturedItemSummary(item)
                    }
                }
            }
        }
    }
}
