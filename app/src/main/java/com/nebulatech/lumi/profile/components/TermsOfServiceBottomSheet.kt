package com.nebulatech.lumi.profile.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nebulatech.lumi.ui.theme.LiterataFontFamily
import com.nebulatech.lumi.ui.theme.ManropeFontFamily
import com.nebulatech.lumi.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceBottomSheet(
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Terms of Service",
                        fontFamily = LiterataFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "Last updated: August 2026",
                        fontFamily = ManropeFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF7A6A73)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Medical Disclaimer Highlight Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF0E4)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "Not Medical Advice",
                            fontFamily = LiterataFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8F4D00)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lumi is a wellness tracking tool designed for self-monitoring and personal body literacy. Lumi predictions and phase recommendations do not constitute clinical medical advice or guaranteed contraception.",
                            fontFamily = ManropeFontFamily,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFF633A00)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Terms Sections Card
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TermsParagraphItem(
                        number = "1",
                        title = "Acceptance of Terms",
                        text = "By creating an account or using Lumi, you agree to these Terms of Service. If you disagree with any portion of these terms, please discontinue using the application."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    TermsParagraphItem(
                        number = "2",
                        title = "Data Ownership & Security",
                        text = "You maintain full intellectual property rights and ownership of your personal health data. Lumi implements industry-standard encryption protocols to protect your records."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    TermsParagraphItem(
                        number = "3",
                        title = "User Accuracy & Responsibility",
                        text = "The precision of cycle predictions, fertile windows, and phase insights depends upon the accuracy and regularity of your symptom, temperature, and LH test logs."
                    )

                    HorizontalDivider(color = Color(0xFFF2ECEF), thickness = 1.dp)

                    TermsParagraphItem(
                        number = "4",
                        title = "Modifications to Service",
                        text = "We continually enhance Lumi with new algorithms and features. Material changes to terms will be communicated with appropriate in-app notice."
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "I Understand & Agree",
                    fontFamily = ManropeFontFamily,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun TermsParagraphItem(
    number: String,
    title: String,
    text: String
) {
    Column {
        Text(
            text = "$number. $title",
            fontFamily = LiterataFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF26181F)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontFamily = ManropeFontFamily,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = Color(0xFF6E5E67)
        )
    }
}
