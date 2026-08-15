package com.nebulatech.lumi.logging

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Sick
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBBTBottomSheet(
    onDismissRequest: () -> Unit,
    onSaveReading: (String, Boolean, Boolean) -> Unit = { _, _, _ -> },
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    modifier: Modifier = Modifier
) {
    var tempValue by remember { mutableStateOf("97.8") }
    var disturbedSleep by remember { mutableStateOf(false) }
    var feverIllness by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFDCD2D6))
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Log BBT + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log BBT",
                    fontFamily = LiterataFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF3B2D34),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time Sublabel
            Text(
                text = "Today, 7:30 AM",
                fontFamily = ManropeFontFamily,
                fontSize = 13.sp,
                color = Color(0xFF5E4E57)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Temperature Value Display
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = tempValue.ifEmpty { "0" },
                        fontFamily = LiterataFontFamily,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "°F",
                        fontFamily = LiterataFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                // Underline line
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(2.dp)
                        .background(Color(0xFFF2DCE5))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Excludable Factor Cards (2 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Disturbed Sleep
                val isSleepSelected = disturbedSleep
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .clickable { disturbedSleep = !disturbedSleep },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSleepSelected) Color(0xFFFDF2F6) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSleepSelected) 1.5.dp else 1.dp,
                        color = if (isSleepSelected) Primary else Color(0xFFEADBDF)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ModeNight,
                            contentDescription = null,
                            tint = if (isSleepSelected) Primary else Color(0xFF4A3B43),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Disturbed Sleep",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isSleepSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSleepSelected) Primary else Color(0xFF4A3B43)
                        )
                    }
                }

                // Card 2: Fever / Illness
                val isFeverSelected = feverIllness
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(84.dp)
                        .clickable { feverIllness = !feverIllness },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFeverSelected) Color(0xFFFDF2F6) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isFeverSelected) 1.5.dp else 1.dp,
                        color = if (isFeverSelected) Primary else Color(0xFFEADBDF)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Sick,
                            contentDescription = null,
                            tint = if (isFeverSelected) Primary else Color(0xFF4A3B43),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Fever / Illness",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            fontWeight = if (isFeverSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isFeverSelected) Primary else Color(0xFF4A3B43)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom Number Numpad Keyboard
            BBTNumpad(
                onNumberClick = { num ->
                    if (tempValue.length < 5) {
                        tempValue += num
                    }
                },
                onDotClick = {
                    if (!tempValue.contains(".")) {
                        tempValue += "."
                    }
                },
                onBackspaceClick = {
                    if (tempValue.isNotEmpty()) {
                        tempValue = tempValue.dropLast(1)
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFF2ECEF))

            Spacer(modifier = Modifier.height(16.dp))

            // Save Reading Button
            Button(
                onClick = {
                    onSaveReading(tempValue, disturbedSleep, feverIllness)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "Save Reading",
                    fontFamily = ManropeFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BBTNumpad(
    onNumberClick: (String) -> Unit,
    onDotClick: () -> Unit,
    onBackspaceClick: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "DEL")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .clickable {
                                when (item) {
                                    "." -> onDotClick()
                                    "DEL" -> onBackspaceClick()
                                    else -> onNumberClick(item)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (item == "DEL") {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = "Delete",
                                tint = Color(0xFF3B2D34),
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = item,
                                fontFamily = LiterataFontFamily,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF26181F),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
