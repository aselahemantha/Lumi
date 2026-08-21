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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.analytics.AnalyticsConstants
import com.nebulatech.lumi.analytics.LocalAnalyticsTracker
import com.nebulatech.lumi.analytics.TrackScreenView
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

enum class LHIntensity(
    val title: String,
    val description: String
) {
    LOW("Low", "Test line is lighter than control"),
    HIGH("High", "Test line is as dark as control"),
    PEAK("Peak", "Test line is darker than control")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogLHTestBottomSheet(
    onDismissRequest: () -> Unit,
    onSaveResult: (LHIntensity, String, String?) -> Unit = { _, _, _ -> },
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    modifier: Modifier = Modifier
) {
    val tracker = LocalAnalyticsTracker.current
    TrackScreenView("log_lh_test_sheet")

    var selectedIntensity by remember { mutableStateOf(LHIntensity.HIGH) }
    var testBrand by remember { mutableStateOf("") }

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
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row: Log LH Test + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log LH Test",
                    fontFamily = LiterataFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                IconButton(
                    onClick = {
                        tracker.trackButtonClick(AnalyticsConstants.Buttons.CANCEL_DAILY_LOG, "log_lh_test_sheet")
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFF5F2F4))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF3B2D34),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: Test Result Intensity
            Text(
                text = "Test Result Intensity",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B2D34)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LHIntensity.entries.forEach { intensity ->
                    val isSelected = selectedIntensity == intensity

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedIntensity = intensity },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFFDF2F6) else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Primary else Color(0xFFEADBDF)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = intensity.title,
                                    fontFamily = LiterataFontFamily,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF26181F)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = intensity.description,
                                    fontFamily = ManropeFontFamily,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5E4E57)
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedIntensity = intensity },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Primary,
                                    unselectedColor = Color(0xFFD4C2C8)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Test Brand (Optional)
            Text(
                text = "Test Brand (Optional)",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B2D34)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = testBrand,
                onValueChange = { testBrand = it },
                placeholder = {
                    Text(
                        text = "e.g., Easy@Home, Clearblue",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF9E8E96)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color(0xFFEADBDF),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Test Strip Photo
            Text(
                text = "Test Strip Photo",
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B2D34)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF7F5F3))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFD8CCD2),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { /* Photo picker hook */ },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEADBDF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Take Photo",
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color(0xFFF2ECEF))

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Buttons Row: Cancel & Save Result
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = ManropeFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        tracker.trackButtonClick(
                            buttonName = "btn_save_lh_result",
                            screenName = "log_lh_test_sheet",
                            extraParams = mapOf(
                                "intensity" to selectedIntensity.name,
                                "has_brand" to testBrand.isNotBlank()
                            )
                        )
                        tracker.trackEvent(
                            "lh_test_logged",
                            mapOf(
                                "intensity" to selectedIntensity.name,
                                "has_brand" to testBrand.isNotBlank()
                            )
                        )
                        onSaveResult(selectedIntensity, testBrand, null)
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        text = "Save Result",
                        fontFamily = ManropeFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
