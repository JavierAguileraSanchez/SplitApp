package com.example.splitapp.ui.group

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// Corporate palette — Indigo primary + Mint/Teal secondary + harmonized variants
private val PIE_PALETTE = listOf(
    Color(0xFF3F51B5), // Indigo 600
    Color(0xFF26A69A), // Mint / Teal 400
    Color(0xFF5C6BC0), // Indigo 400
    Color(0xFF4DB6AC), // Teal 300
    Color(0xFF7986CB), // Indigo 300
    Color(0xFF80CBC4), // Teal 200
    Color(0xFF42A5F5), // Soft blue
    Color(0xFF546E7A), // Slate / Blue-Grey 600
)

@Composable
fun GroupExpensesPieChart(
    expensesByCategory: Map<String, Long>,
    modifier: Modifier = Modifier
) {
    val entries = expensesByCategory.entries
        .filter { it.value > 0L }
        .sortedByDescending { it.value }
    val total = entries.sumOf { it.value.toDouble() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (total <= 0.0 || entries.isEmpty()) {
            EmptyPieState()
            return
        }

        // ── Pie chart ──────────────────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f)) {
            // Tiny gap between slices only when there are multiple categories
            val sliceGap = if (entries.size > 1) 1.5f else 0f
            val inset = size.width * 0.04f
            val arcTopLeft = Offset(inset, inset)
            val arcSize = Size(size.width - inset * 2, size.height - inset * 2)

            var startAngle = -90f          // start at the top (12 o'clock)
            entries.forEachIndexed { index, (_, value) ->
                val fullSweep = (value.toDouble() / total * 360.0).toFloat()
                val drawnSweep = maxOf(fullSweep - sliceGap, 0f)
                drawArc(
                    color = PIE_PALETTE[index % PIE_PALETTE.size],
                    startAngle = startAngle,
                    sweepAngle = drawnSweep,
                    useCenter = true,
                    topLeft = arcTopLeft,
                    size = arcSize
                )
                startAngle += fullSweep    // advance by the full sweep to keep gaps uniform
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Legend (2-column grid) ─────────────────────────────────────────────
        val legendRows = entries.chunked(2)
        legendRows.forEachIndexed { rowIndex, rowEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowEntries.forEachIndexed { colIndex, (category, value) ->
                    val globalIndex = rowIndex * 2 + colIndex
                    val percent = (value.toDouble() / total * 100.0).roundToInt()
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = PIE_PALETTE[globalIndex % PIE_PALETTE.size],
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$category: $percent%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                // Pad the row with an empty weight cell if it has only one entry
                if (rowEntries.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun EmptyPieState() {
    val ringColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth(0.6f).aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.width * 0.16f
            val inset = strokeWidth / 2 + size.width * 0.04f
            drawArc(
                color = ringColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                style = Stroke(width = strokeWidth)
            )
        }
        Text(
            text = "Sin gastos aún",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}