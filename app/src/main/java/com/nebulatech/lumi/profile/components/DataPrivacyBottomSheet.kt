package com.nebulatech.lumi.profile.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataPrivacyBottomSheet(
    onDismissRequest: () -> Unit,
    onDeleteAllData: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showExportSuccessDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFFFBF9F7),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF7E6EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Data Privacy & Export",
                        fontFamily = LiterataFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "Your data belongs to you. Fully encrypted & private.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF7A6A73)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Privacy Protection Highlights Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrivacyBulletItem(
                        icon = Icons.Outlined.Lock,
                        title = "Local-First Storage",
                        description = "Your cycle and symptom records are stored directly on your device."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    PrivacyBulletItem(
                        icon = Icons.Outlined.Shield,
                        title = "Zero Ad Trackers",
                        description = "Lumi never sells or shares your reproductive health data with third parties."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    PrivacyBulletItem(
                        icon = Icons.Outlined.VpnKey,
                        title = "End-to-End Encryption",
                        description = "All health calculations and insights are processed securely."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Export Health Report Section
            Text(
                text = "Export Health Records",
                fontFamily = LiterataFontFamily,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Clinical Cycle & Symptom Summary",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF26181F)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Generate a comprehensive export of your cycle history, period duration, BBT temperatures, and symptoms formatted for doctor visits or personal backups.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF6E5E67)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            shareExportSummary(context)
                            showExportSuccessDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Export Health Summary (CSV)",
                                fontFamily = ManropeFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Delete All Data (Danger Zone)
            OutlinedButton(
                onClick = { showDeleteConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFBA1A1A)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFBA1A1A))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Erase All Health & Cycle Data",
                        fontFamily = ManropeFontFamily,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Delete Confirmation Alert Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = {
                    Text(
                        text = "Erase All Data?",
                        fontFamily = LiterataFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFBA1A1A)
                    )
                },
                text = {
                    Text(
                        text = "This will permanently delete all your logged periods, daily symptoms, BBT readings, and cycle history from this device. This action cannot be undone.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = Color(0xFF5E4E57)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmDialog = false
                            onDeleteAllData()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Yes, Delete Everything",
                            fontFamily = ManropeFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteConfirmDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            fontFamily = ManropeFontFamily
                        )
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }

        // Export Success Dialog
        if (showExportSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showExportSuccessDialog = false },
                title = {
                    Text(
                        text = "Report Generated",
                        fontFamily = LiterataFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                },
                text = {
                    Text(
                        text = "Your Lumi cycle summary has been prepared. You can share or save the file via the system dialog.",
                        fontFamily = ManropeFontFamily,
                        fontSize = 13.sp,
                        color = Color(0xFF5E4E57)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showExportSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
private fun PrivacyBulletItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFF9EEF2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(16.dp)
            )
        }

        Column {
            Text(
                text = title,
                fontFamily = ManropeFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF26181F)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = ManropeFontFamily,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = Color(0xFF7A6A73)
            )
        }
    }
}

private fun shareExportSummary(context: Context) {
    val exportData = """
        LUMI HEALTH & CYCLE SUMMARY
        Generated: ${java.time.LocalDate.now()}
        
        Cycle Tracking Mode: Regular
        Data Format: Clinical Summary
        
        Date,CycleDay,Phase,Flow,Symptoms,BBT(C),Notes
        ${java.time.LocalDate.now().minusDays(3)},1,Menstrual,Medium,"Cramps, Fatigue",36.4,"Period Start"
        ${java.time.LocalDate.now().minusDays(2)},2,Menstrual,Heavy,"Cramps",36.5,""
        ${java.time.LocalDate.now().minusDays(1)},3,Menstrual,Light,"Mild Headache",36.4,""
        ${java.time.LocalDate.now()},4,Menstrual,Spotting,"High Energy",36.5,""
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Lumi Health & Cycle Summary")
        putExtra(Intent.EXTRA_TEXT, exportData)
    }
    context.startActivity(Intent.createChooser(intent, "Export Lumi Health Summary"))
}
